package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;

@Data
public class UserReviewResponse {
    private Long orderId;
    private int rating;
    private String comment;
    private StringArraySerializer images;
}
