package org.demo.baoleme.dto.response;

import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;

@Data
public class UserReviewResponse {
    private Long orderId;
    private int rating;
    private String comment;
    private StringArraySerializer images;
}
