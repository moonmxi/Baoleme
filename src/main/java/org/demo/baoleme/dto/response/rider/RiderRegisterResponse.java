package org.demo.baoleme.dto.response.rider;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RiderRegisterResponse {
    private Long userId;
    private String username;
    private String phone;
}