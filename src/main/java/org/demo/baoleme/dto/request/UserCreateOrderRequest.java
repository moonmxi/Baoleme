package org.demo.baoleme.dto.request;

import lombok.Data;

@Data
public class UserCreateOrderRequest {
    private Long addresId;
    private Long couponId;
    private String remark;//订单备注
}
