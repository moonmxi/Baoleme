package org.demo.baoleme.dto.request.user;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import lombok.Data;

import java.util.List;

@Data
public class UserSearchOrderRequest {
    private Long OrderId;
    private Integer page;
    private Integer pageSize;
}