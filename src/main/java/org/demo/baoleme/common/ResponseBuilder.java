package org.demo.baoleme.common;

/**
 * 快捷构建统一响应体
 */
public class ResponseBuilder {

    public static CommonResponse ok() {
        return CommonResponse.builder()
                .success(true)
                .code(200)
                .message("success")
                .build();
    }

    public static CommonResponse ok(Object data) {
        return CommonResponse.builder()
                .success(true)
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static CommonResponse fail(String message) {
        return CommonResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
    }

    public static CommonResponse fail(String message, Object data) {
        return CommonResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .data(data)
                .build();
    }

    public static CommonResponse custom(boolean success, int code, String message, Object data) {
        return CommonResponse.builder()
                .success(success)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}