package org.demo.baoleme.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.pojo.Message;
import org.demo.baoleme.pojo.ChatMessage;
import org.demo.baoleme.service.MessageService;
import org.demo.baoleme.mapper.UserMapper;
import org.demo.baoleme.mapper.RiderMapper;
import org.demo.baoleme.mapper.MerchantMapper;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.pojo.Rider;
import org.demo.baoleme.pojo.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();


    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;  // Spring注入，带JSR310支持


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RiderMapper riderMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getToken(session);
        Map<String, Object> payload = JwtUtils.parsePayload(token);
        if (payload == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("无效Token"));
            return;
        }

        Long userId = ((Number) payload.get("user_id")).longValue();
        String role = (String) payload.get("role");

        String key = buildKey(role, userId);
        sessionMap.put(key, session);
        log.info("✅ 用户连接成功：{}", key);

        // 推送离线消息
        String redisKey = "offline:msg:" + key;
        while (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            ChatMessage msg = (ChatMessage) redisTemplate.opsForList().leftPop(redisKey);
            if (msg == null) break;
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String json = message.getPayload();
        ChatMessage chatMsg = objectMapper.readValue(json, ChatMessage.class);
        chatMsg.setTimeStamp(LocalDateTime.now());

        // 解析发送方身份
        String token = getToken(session);
        Map<String, Object> payload = JwtUtils.parsePayload(token);
        if (payload == null) return;

        Long senderId = ((Number) payload.get("user_id")).longValue();
        String senderRole = (String) payload.get("role");
        String senderName = (String) payload.get("username"); // 可以为空

        chatMsg.setSenderId(senderId);
        chatMsg.setSenderRole(senderRole);
        chatMsg.setSenderName(senderName);

        // 获取 receiverId
        Long receiverId = resolveReceiverId(chatMsg.getReceiverRole(), chatMsg.getReceiverName());
        if (receiverId == null) {
            session.sendMessage(new TextMessage("{\"error\":\"接收方用户不存在\"}"));
            return;
        }
        chatMsg.setReceiverId(receiverId);

        // 存入数据库
        Message dbMsg = new Message();
        dbMsg.setContent(chatMsg.getContent());
        dbMsg.setSenderId(chatMsg.getSenderId());
        dbMsg.setSenderRole(chatMsg.getSenderRole());
        dbMsg.setReceiverId(chatMsg.getReceiverId());
        dbMsg.setReceiverRole(chatMsg.getReceiverRole());
        dbMsg.setCreatedAt(chatMsg.getTimeStamp());
        messageService.saveMessage(dbMsg);

        // 推送消息
        String receiverKey = buildKey(chatMsg.getReceiverRole(), chatMsg.getReceiverId());
        WebSocketSession receiverSession = sessionMap.get(receiverKey);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMsg)));
        } else {
            // 不在线 => 存入离线消息队列
            String redisKey = "offline:msg:" + receiverKey;
            redisTemplate.opsForList().rightPush(redisKey, chatMsg);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.values().removeIf(s -> s.equals(session));
        log.info("🚪 用户断开连接");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            log.error(" WebSocket连接异常", exception);
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            log.error(" WebSocket关闭失败", e);
        }
    }

    // 提取 Authorization 中的 token
    private String getToken(WebSocketSession session) {
        String raw = session.getHandshakeHeaders().getFirst("Authorization");
        return raw != null ? raw.replace("Bearer ", "") : null;
    }

    // 构造唯一身份 key
    private String buildKey(String role, Long id) {
        return role + ":" + id;
    }

    // 根据用户名与角色查找用户ID
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