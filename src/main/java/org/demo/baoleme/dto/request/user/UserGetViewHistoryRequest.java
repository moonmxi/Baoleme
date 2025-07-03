package org.demo.baoleme.dto.request.user;

import lombok.Data;

@Data
public class UserGetViewHistoryRequest {
    private Integer page;
    private Integer pageSize;
}
