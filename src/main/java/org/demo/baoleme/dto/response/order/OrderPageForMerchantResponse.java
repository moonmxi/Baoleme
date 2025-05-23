package org.demo.baoleme.dto.response.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderPageForMerchantResponse {
    private List<OrderReadByMerchantResponse> list;
    private int currentPage;
    private int pageSize;
    private long total;
}
