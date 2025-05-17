package org.demo.baoleme.dto.request.salesStats;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;

@Data
public class SaleTrendStatsRequest {
    private TimeLine type;
    private int numOfRecentDays;

    public enum TimeLine {
        BY_DAY(0),
        BY_WEEK(1),
        BY_MONTH(2);

        @EnumValue
        private final int code;

        TimeLine(int code) {
            this.code = code;
        }
    }
}
