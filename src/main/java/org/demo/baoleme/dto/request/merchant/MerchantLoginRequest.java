package org.demo.baoleme.dto.request.merchant;

import lombok.Data;

@Data
public class MerchantLoginRequest {
    private String username;
    private String phone;
    private String password;
}