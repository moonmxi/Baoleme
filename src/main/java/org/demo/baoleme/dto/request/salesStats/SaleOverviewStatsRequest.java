package org.demo.baoleme.dto.request.salesStats;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;

@Data
public class SaleOverviewStatsRequest {
    private Long storeId;
    private TimeRange timeRange;

    public enum TimeRange {
        TODAY(0),
        THIS_WEEK(1),
        THIS_MONTH(2);

        @EnumValue
        private final int code;

        TimeRange(int code) {
            this.code = code;
        }
    }
}
