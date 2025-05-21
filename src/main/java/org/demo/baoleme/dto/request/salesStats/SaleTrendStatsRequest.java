package org.demo.baoleme.dto.request.salesStats;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;

@Data
public class SaleTrendStatsRequest {
    private Long storeId;
    private TimeAxis type;
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
