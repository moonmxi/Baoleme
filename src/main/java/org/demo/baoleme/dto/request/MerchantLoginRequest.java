package org.demo.baoleme.dto.request;

import lombok.Data;

@Data
public class MerchantLoginRequest {
    private String phone;
    private String password;
}