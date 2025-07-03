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
import java.time.format.DateTimeFormatter;
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
        System.out.println(session.toString());
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
        System.out.println("111");
        // Step1: 直接用字符串操作，手动解析 JSON 内容
        String json = message.getPayload();
        ChatMessage chatMsg = new ChatMessage();

        // 1. 仅支持 {"body":{...}} 结构，找到 body 部分
        int bodyPos = json.indexOf("\"body\":{");
        if (bodyPos < 0) {
            session.sendMessage(new TextMessage("{\"error\":\"消息格式错误\"}"));
            return;
        }
        int bodyStart = bodyPos + "\"body\":".length();
        int braceCount = 0, i = bodyStart, bodyEnd = -1;
        for (; i < json.length(); ++i) {
            if (json.charAt(i) == '{') braceCount++;
            else if (json.charAt(i) == '}') {
                braceCount--;
                if (braceCount == 0) {
                    bodyEnd = i;
                    break;
                }
            }
        }
        if (bodyEnd == -1) {
            session.sendMessage(new TextMessage("{\"error\":\"消息格式错误\"}"));
            return;
        }
        String bodyJson = json.substring(bodyStart, bodyEnd + 1);

        // 2. 字符串方式查找字段
        // 支持 "xxx":"yyy"  和 "xxx":123  两种
        String createdAt = getJsonString(bodyJson, "created_at");
        chatMsg.setContent(getJsonString(bodyJson, "content"));
        chatMsg.setReceiverRole(getJsonString(bodyJson, "receiver_role"));
        chatMsg.setReceiverName(getJsonString(bodyJson, "receiver_name"));
        chatMsg.setSenderName(getJsonString(bodyJson, "sender_name"));
        chatMsg.setSenderRole(getJsonString(bodyJson, "sender_role"));
        Long senderId = getJsonLong(bodyJson, "sender_id");
        Long receiverId = getJsonLong(bodyJson, "receiver_id");
        if (senderId != null) chatMsg.setSenderId(senderId);
        if (receiverId == null) {
            session.sendMessage(new TextMessage("{\"error\":\"接收方用户不存在\"}"));
            return;
        }
        chatMsg.setReceiverId(receiverId);
        if (createdAt != null) {
            try {
                chatMsg.setTimeStamp(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME));
            } catch (Exception e) {
                // 解析失败忽略
            }
        }

        // 3. 补充时间戳
        chatMsg.setTimeStamp(LocalDateTime.now());

        // 4. Token 校验及补全
        String token = getToken(session);
        Map<String, Object> payload = JwtUtils.parsePayload(token);
        if (payload == null) {
            session.sendMessage(new TextMessage("{\"error\":\"无效的Token\"}"));
            return;
        }

        // 5. 持久化与转发逻辑（原样）
        Message dbMsg = new Message();
        dbMsg.setContent(chatMsg.getContent());
        dbMsg.setSenderId(chatMsg.getSenderId());
        dbMsg.setSenderRole(chatMsg.getSenderRole());
        dbMsg.setReceiverId(chatMsg.getReceiverId());
        dbMsg.setReceiverRole(chatMsg.getReceiverRole());
        dbMsg.setCreatedAt(chatMsg.getTimeStamp());
        messageService.saveMessage(dbMsg);

        String receiverKey = buildKey(chatMsg.getReceiverRole(), chatMsg.getReceiverId());
        WebSocketSession receiverSession = sessionMap.get(receiverKey);

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMsg)));
        } else {
            String redisKey = "offline:msg:" + receiverKey;
            redisTemplate.opsForList().rightPush(redisKey, chatMsg);
        }
    }

// ===================== 工具函数 =========================

    /**
     * 查找 "key":"value" 或 "key":123 这样的内容，返回字符串
     */
    private String getJsonString(String json, String key) {
        String pat = "\"" + key + "\":";
        int p = json.indexOf(pat);
        if (p < 0) return null;
        int vstart = p + pat.length();
        // 跳过空格
        while (vstart < json.length() && (json.charAt(vstart) == ' ' || json.charAt(vstart) == '\n')) vstart++;
        if (vstart >= json.length()) return null;
        if (json.charAt(vstart) == '"') {
            vstart++;
            int vend = json.indexOf('"', vstart);
            return vend > vstart ? json.substring(vstart, vend) : null;
        } else {
            int vend = vstart;
            while (vend < json.length() && (Character.isDigit(json.charAt(vend)) || json.charAt(vend) == '-')) vend++;
            return json.substring(vstart, vend);
        }
    }

    /**
     * 查找并返回 long 整型
     */
    private Long getJsonLong(String json, String key) {
        String v = getJsonString(json, key);
        try { return v == null ? null : Long.parseLong(v); } catch (Exception e) { return null; }
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
        // 0. 防御性编程，检查 session 是否为 null
        if (session == null) {
            return null;
        }

        // 1. 将 WebSocketSession 对象转换为其字符串表示形式
        //    例如："StandardWebSocketSession[id=..., uri=...Authorization=Bearer%20...]"
        String sessionString = session.toString();

        // 2. 定义我们要查找的 Token 的前缀
        //    在 URL query 中，"Bearer " 后面的空格被编码为 "%20"
        String prefix = "Authorization=Bearer%20";

        // 3. 查找此前缀在字符串中的起始位置
        int startIndex = sessionString.indexOf(prefix);

        // 4. 如果没有找到前缀，说明 Token 不存在于字符串中，返回 null
        if (startIndex == -1) {
            return null;
        }

        // 5. 计算 Token 内容的真正起始索引（即前缀之后的位置）
        int tokenStartIndex = startIndex + prefix.length();

        // 6. 查找 Token 的结束位置。根据您提供的格式，Token 在字符串的末尾，并由 ']' 符号结束。
        int endIndex = sessionString.indexOf(']', tokenStartIndex);

        // 7. 截取子字符串，得到最终的 Token
        //    如果找到了结束符 ']'，则截取到它之前
        //    如果没有找到（以防万一格式有变），则截取到字符串末尾
        if (endIndex != -1) {
            return sessionString.substring(tokenStartIndex, endIndex);
        } else {
            return sessionString.substring(tokenStartIndex);
        }
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
