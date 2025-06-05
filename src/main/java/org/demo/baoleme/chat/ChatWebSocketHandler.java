package org.demo.baoleme.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.service.MessageService;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息处理器
 * <p>
 * 负责处理 WebSocket 连接的生命周期事件（连接建立/关闭/错误）和文本消息处理。
 * 实现了用户在线状态管理、消息路由、离线消息存储等功能。
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    /**
     * 在线会话映射表
     * <p>
     * Key: 用户唯一标识 (格式: "角色:ID", 如 "user:123")
     * Value: 对应的 WebSocketSession
     */
    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private MessageService messageService;  // 消息存储服务

    @Autowired
    private ObjectMapper objectMapper;      // JSON 序列化/反序列化工具（支持JSR310日期时间）

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;  // Redis操作模板

    @Autowired
    private UserMapper userMapper;          // 用户数据访问

    @Autowired
    private RiderMapper riderMapper;        // 骑手数据访问

    @Autowired
    private MerchantMapper merchantMapper;  // 商家数据访问

    /**
     * 当 WebSocket 连接成功建立时调用
     *
     * @param session 当前建立的WebSocket会话
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        // Step1: 从请求头获取并验证JWT Token
        // 从请求头提取Token
        String token = getToken(session);
        Map<String, Object> payload = JwtUtils.parsePayload(token);

        // Token无效则拒绝连接
        if (payload == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("无效Token"));
            return;
        }

        // Step2: 将用户会话加入在线映射表
        // 解析用户身份信息
        Long userId = ((Number) payload.get("user_id")).longValue();
        String role = (String) payload.get("role");
        String key = buildKey(role, userId);

        // 将会话加入在线列表
        sessionMap.put(key, session);
        log.info("✅ 用户连接成功：{}", key);

        // Step3: 检查并推送该用户的离线消息
        // 处理离线消息：从Redis队列读取并发送
        String redisKey = "offline:msg:" + key;
        while (redisTemplate.hasKey(redisKey)) {
            ChatMessage msg = (ChatMessage) redisTemplate.opsForList().leftPop(redisKey);
            if (msg == null) break;
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
    }

    /**
     * 处理接收到的文本消息
     *
     * @param session 当前发送消息的会话
     * @param message 接收到的文本消息
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        // Step1: 解析消息内容并补充发送者信息
        // 反序列化消息内容
        String json = message.getPayload();
        ChatMessage chatMsg = objectMapper.readValue(json, ChatMessage.class);
        chatMsg.setTimeStamp(LocalDateTime.now());

        // 从会话中获取发送者身份
        String token = getToken(session);
        Map<String, Object> payload = JwtUtils.parsePayload(token);
        if (payload == null) return;

        // Step2: 根据接收者用户名查找对应ID
        // 设置发送者信息
        Long senderId = ((Number) payload.get("user_id")).longValue();
        String senderRole = (String) payload.get("role");
        String senderName = (String) payload.get("username");
        chatMsg.setSenderId(senderId);
        chatMsg.setSenderRole(senderRole);
        chatMsg.setSenderName(senderName);

        // 根据接收者用户名解析其ID
        Long receiverId = resolveReceiverId(chatMsg.getReceiverRole(), chatMsg.getReceiverName());
        if (receiverId == null) {
            session.sendMessage(new TextMessage("{\"error\":\"接收方用户不存在\"}"));
            return;
        }
        chatMsg.setReceiverId(receiverId);

        // Step3: 持久化消息到数据库
        Message dbMsg = new Message();
        dbMsg.setContent(chatMsg.getContent());
        dbMsg.setSenderId(chatMsg.getSenderId());
        dbMsg.setSenderRole(chatMsg.getSenderRole());
        dbMsg.setReceiverId(chatMsg.getReceiverId());
        dbMsg.setReceiverRole(chatMsg.getReceiverRole());
        dbMsg.setCreatedAt(chatMsg.getTimeStamp());
        messageService.saveMessage(dbMsg);

        // Step4: 实时转发或存储为离线消息
        // 消息路由：实时转发或离线存储
        String receiverKey = buildKey(chatMsg.getReceiverRole(), chatMsg.getReceiverId());
        WebSocketSession receiverSession = sessionMap.get(receiverKey);

        if (receiverSession != null && receiverSession.isOpen()) {
            // 接收者在线：实时转发
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMsg)));
        } else {
            // 接收者离线：存入Redis队列
            String redisKey = "offline:msg:" + receiverKey;
            redisTemplate.opsForList().rightPush(redisKey, chatMsg);
        }
    }

    /**
     * 当 WebSocket 连接关闭时调用
     *
     * @param session 关闭的会话
     * @param status  关闭状态码
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // Step: 从在线会话映射中移除当前会话
        sessionMap.values().removeIf(s -> s.equals(session));
        log.info("🚪 用户断开连接");
    }

    /**
     * 处理传输层错误
     * <p>
     * 记录错误日志并强制关闭连接
     *
     * @param session   发生错误的会话
     * @param exception 异常对象
     */
    @Override
    public void handleTransportError(WebSocketSession session, @NonNull Throwable exception) {
        try {
            log.error(" WebSocket连接异常", exception);
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            log.error(" WebSocket关闭失败", e);
        }
    }

    // ------------ 私有工具方法 ------------ //

    /**
     * 从请求头提取JWT Token
     *
     * @param session WebSocket会话
     * @return 去除"Bearer "前缀后的Token字符串
     */
    private String getToken(WebSocketSession session) {
        String raw = session.getHandshakeHeaders().getFirst("Authorization");
        return raw != null ? raw.replace("Bearer ", "") : null;
    }

    /**
     * 构建用户唯一标识键
     *
     * @param role 用户角色
     * @param id   用户ID
     * @return 格式为"role:id"的字符串
     */
    private String buildKey(String role, Long id) {
        return role + ":" + id;
    }

    /**
     * 根据用户名解析用户ID
     * <p>
     * 根据接收者角色查询对应数据表
     *
     * @param role 用户角色(user/rider/merchant)
     * @param name 用户名
     * @return 用户ID，未找到返回null
     */
    private Long resolveReceiverId(String role, String name) {
        switch (role) {
            case "user" -> {
                User user = userMapper.selectByUsername(name);
                return user != null ? user.getId() : null;
            }
            case "rider" -> {
                Rider rider = riderMapper.selectByUsername(name);
                return rider != null ? rider.getId() : null;
            }
            case "merchant" -> {
                Merchant merchant = merchantMapper.selectByUsername(name);
                return merchant != null ? merchant.getId() : null;
            }
            default -> {
                return null;
            }
        }
    }
}