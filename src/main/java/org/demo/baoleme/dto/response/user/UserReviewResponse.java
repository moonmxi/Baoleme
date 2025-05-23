package org.demo.baoleme.dto.response.user;

import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;

import java.util.List;

@Data
public class UserReviewResponse {
    private Long orderId;
    private int rating;
    private String comment;
    private List<String> images;
}
