package org.demo.baoleme.dto.response.message;

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