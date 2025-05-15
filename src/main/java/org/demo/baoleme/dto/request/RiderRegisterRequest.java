package org.demo.baoleme.dto.request;

import lombok.Data;

@Data
public class RiderRegisterRequest {
    private String username;
    private String password;
    private String phone;
}