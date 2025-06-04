package org.demo.baoleme.dto.request.rider;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RiderUpdateRequest {

    @Size(min = 2, max = 20, message = "用户名长度应为2~20个字符")
    private String username;

    @Size(min = 6, max = 20, message = "密码长度应为6~20个字符")
    private String password;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不合法")
    private String phone;

    /**
     * -1=未激活, 0=接单中, 1=空闲
     */
    private Integer orderStatus;

    /**
     * 0=手动接单, 1=自动接单
     */
    private Integer dispatchMode;

    private String avatar;
}