package org.demo.baoleme.dto.request.merchant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantRegisterRequest {
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "手机号不能为空")
    private String phone;
}