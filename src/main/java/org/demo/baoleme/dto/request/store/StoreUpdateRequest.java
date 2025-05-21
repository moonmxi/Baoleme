package org.demo.baoleme.dto.request.store;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.baoleme.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreUpdateRequest {
    private Long id;
    private String name;
    private String desc;
    private String location;
    private BigDecimal rating;
    private BigDecimal balance;
    private int status;
    private LocalDateTime createdAt;
    private String image;
}
