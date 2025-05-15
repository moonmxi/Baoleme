package org.demo.baoleme.dto.request;

import lombok.Data;

@Data
public class RiderLoginRequest {
    private String phone;
    private String password;
}