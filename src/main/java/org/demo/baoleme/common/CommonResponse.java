package org.demo.baoleme.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * 统一响应体结构，适用于所有接口
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 字段自动忽略
public class CommonResponse {
    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 返回信息（成功或失败的说明）
     */
    private String message;

    /**
     * 实际返回数据内容
     */
    private Object data;

    /**
     * 自定义状态码（可选，建议配合业务语义使用）
     */
    private Integer code;
}