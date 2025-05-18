package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("store_id")
    private Long storeId;

    @TableField("product_id")
    private Long productId;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评论内容
     */
    private String comment;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 评分枚举（可选扩展）
     */
    public enum Rating {
        ONE_STAR(1), TWO_STAR(2), THREE_STAR(3),
        FOUR_STAR(4), FIVE_STAR(5);

        @EnumValue
        private final int value;

        Rating(int value) {
            this.value = value;
        }
    }
}