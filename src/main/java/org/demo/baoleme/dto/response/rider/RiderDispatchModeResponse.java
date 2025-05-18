package org.demo.baoleme.dto.response.rider;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RiderDispatchModeResponse {
    private Integer currentMode;         // 当前接单模式
    private String modeChangedAt;        // 切换时间（可选，前端显示用）
}