package org.demo.baoleme.dto.request.cart;

import lombok.Data;

import java.util.List;


@Data
public class RemoveCartRequest {
    private List<Long> productIds;
}