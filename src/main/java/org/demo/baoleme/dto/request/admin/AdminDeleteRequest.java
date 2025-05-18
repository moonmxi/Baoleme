package org.demo.baoleme.dto.request.admin;

import lombok.Data;

/**
 * 管理员删除请求体（所有字段可选，但至少传一个）
 */
@Data
public class AdminDeleteRequest {

    private String user_name;

    private String rider_name;

    private String merchant_name;

    private String store_name;

    private String product_name;
}