package org.demo.baoleme.dto.request.merchant;

import lombok.Data;

@Data
public class MerchantRegisterRequest {
    private String username;
    private String password;
    private String phone;
}