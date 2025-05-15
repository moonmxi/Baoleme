package org.demo.baoleme.dto.request;

import lombok.Data;

@Data
public class RiderUpdateRequest {
    private String username;
    private String password;
    private String phone;
    private Integer orderStatus;
    private Integer dispatchMode;
}