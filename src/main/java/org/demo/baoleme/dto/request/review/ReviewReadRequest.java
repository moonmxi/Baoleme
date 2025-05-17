package org.demo.baoleme.dto.request.review;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;

@Data
public class ReviewReadRequest {
    private Long storeId;
    private int page;
    private int pageSize;

    private ReviewFilterType type;

    public enum ReviewFilterType {
        POSITIVE(1, "好评（4-5星）"),
        NEGATIVE(2, "差评（1-2星）"),
        WITH_IMAGES(3, "带图评论");

        @EnumValue  // 标记枚举值用于数据库/参数绑定
        private final int code;
        private final String desc;

        ReviewFilterType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
