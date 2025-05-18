package org.demo.baoleme.dto.request;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserReviewRequest {
    private Long orderId;
    private int rating;
    private String comment;
    private StringArraySerializer images;
}
