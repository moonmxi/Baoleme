package org.demo.baoleme.dto.request.admin;

import lombok.Data;

@Data
public class AdminProductQueryRequest {
    private Long storeId;
    private int page;
    private int pageSize;
}
