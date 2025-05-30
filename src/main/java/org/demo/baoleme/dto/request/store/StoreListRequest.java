package org.demo.baoleme.dto.request.store;

import lombok.Data;

@Data
public class StoreListRequest {
    private int page = 1;
    private int pageSize = 10;
}
