package org.demo.baoleme.pojo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatMessage  implements Serializable {
    private Long senderId;
    private String senderRole;
    private String senderName;
    private String receiverRole;
    private Long receiverId;
    private String receiverName;
    private String content;
    private LocalDateTime timeStamp;
}