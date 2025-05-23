package org.demo.baoleme.dto.request.salesStats;

import com.baomidou.mybatisplus.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaleTrendStatsRequest {
    @NotBlank(message = "店铺id不能为空")
    private Long storeId;

    private TimeAxis type = TimeAxis.BY_DAY;
    private int numOfRecentDays = 30;

    public enum TimeAxis {
        BY_DAY(0),
        BY_WEEK(1),
        BY_MONTH(2);

        @EnumValue
        private final int code;

        TimeAxis(int code) {
            this.code = code;
        }
    }
}
