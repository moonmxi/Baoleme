package org.demo.baoleme.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private Long senderId;
    private String senderRole;
    private String senderName;
    private Long receiverId;
    private String receiverRole;
    private String content;
    private LocalDateTime createdAt;
}