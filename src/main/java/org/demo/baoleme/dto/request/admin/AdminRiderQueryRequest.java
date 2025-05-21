package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员分页查询骑手信息 请求 DTO
 */
@Data
public class AdminRiderQueryRequest {

    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Min(1)
    private Integer pageSize;
}