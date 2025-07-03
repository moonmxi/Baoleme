package org.demo.baoleme.dto.response.store;

import lombok.Data;
import org.demo.baoleme.pojo.Store;

import java.util.List;

@Data
public class StorePageResponse {
    private List<StoreViewInfoResponse> stores;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
}
