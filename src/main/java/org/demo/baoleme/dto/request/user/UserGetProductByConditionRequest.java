package org.demo.baoleme.dto.request.user;

import lombok.Data;

@Data
public class UserGetProductByConditionRequest {
    private Long storeId;
    private String category;
    private Integer page;
    private Integer pageSize;
}
