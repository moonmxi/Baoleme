package org.demo.baoleme.dto.response.admin;

import lombok.Data;

import java.util.List;

/**
 * 管理员删除响应体
 */
@Data
public class AdminDeleteResponse {

    private List<String> deleted;

    private List<String> failed;
}