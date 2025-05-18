package org.demo.baoleme.dto.request.rider;

import lombok.Data;

@Data
public class RiderDispatchModeRequest {
    private Integer dispatchMode; // 0 = 手动，1 = 自动
}