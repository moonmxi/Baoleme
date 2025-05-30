package org.demo.baoleme.dto.request.user;
import lombok.Data;

@Data
public class UserGetShopRequest {
    private String type;
    private float minRating;
    private float maxRating;
    private int page;
    private int size;
}
