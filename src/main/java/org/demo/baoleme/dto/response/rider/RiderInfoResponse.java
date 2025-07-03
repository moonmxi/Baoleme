package org.demo.baoleme.dto.response.rider;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RiderInfoResponse {
    private Long userId;
    private String username;
    private String phone;
    private Integer orderStatus;
    private Integer dispatchMode;
    private Long balance;
    private String avatar;
}