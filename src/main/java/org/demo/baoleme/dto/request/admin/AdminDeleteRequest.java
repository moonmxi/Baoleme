package org.demo.baoleme.dto.request.admin;

import lombok.Data;

/**
 * 管理员删除请求体（所有字段可选，但至少传一个）
 */
@Data
public class AdminDeleteRequest {

    private String userName;

    private String riderName;

    private String merchantName;

    private String storeName;

    private String productName;
}