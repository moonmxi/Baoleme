package org.demo.baoleme.dto.request.user;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;

import java.util.List;

@Data
public class UserReviewRequest {
    private Long storeId;
    private Long productId;//可选
    private int rating;
    private String comment;
    private List<String> images;
}