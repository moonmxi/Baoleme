package org.demo.baoleme.dto.request.user;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;

@Data
public class UserReviewRequest {
    private Long orderId;
    private int rating;
    private String comment;
    private StringArraySerializer images;
}
