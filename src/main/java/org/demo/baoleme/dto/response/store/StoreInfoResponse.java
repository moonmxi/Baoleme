package org.demo.baoleme.dto.response.store;
import lombok.Data;
import org.demo.baoleme.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StoreInfoResponse {
    private Long storeId;
    private String name;
    private String description;
    private String location;
    private String type;
    /**
     * 评分（decimal(2,1), 默认5.0）
     */
    private BigDecimal rating = BigDecimal.valueOf(5.0);
    /**
     * 状态（1-开启，0-关闭）
     */
    private Integer status;
    private LocalDateTime createdAt;
    private String image;
}
