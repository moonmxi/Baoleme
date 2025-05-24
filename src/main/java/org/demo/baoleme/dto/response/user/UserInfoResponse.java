package org.demo.baoleme.dto.response.user;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String phone;
    private String avatar;
    private String description;
    private String location;
    private String gender;
    private LocalDateTime createdAt;
}
