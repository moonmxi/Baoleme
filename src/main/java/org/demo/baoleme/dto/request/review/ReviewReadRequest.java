package org.demo.baoleme.dto.request.review;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;

@Data
public class ReviewReadRequest {
    private Long storeId;
    private int page = 1;
    private int pageSize = 10;
    private ReviewFilterType type;
    private Boolean hasImage; // 是否带图（true/false）

    public enum ReviewFilterType {
        POSITIVE(1, "好评（4-5星）"),
        NEGATIVE(2, "差评（1-2星）");

        @EnumValue
        private final int code;
        private final String desc;

        ReviewFilterType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
