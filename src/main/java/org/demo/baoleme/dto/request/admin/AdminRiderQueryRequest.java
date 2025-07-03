package org.demo.baoleme.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员分页查询骑手信息 请求 DTO
 */
@Data
public class AdminRiderQueryRequest {

    private String keyword;
    private Long startId;
    private Long endId;
    private Integer status;
    private Integer dispatchMode;
    private Long startBalance;
    private Long endBalance;

    private Integer page;

    private Integer pageSize;
}