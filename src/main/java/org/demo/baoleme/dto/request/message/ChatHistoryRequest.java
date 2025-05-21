package org.demo.baoleme.dto.request.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatHistoryRequest {
    @NotNull
    private Long receiver_id;

    @NotNull
    private String receiver_role;

    @NotNull
    private Integer page;

    @NotNull
    private Integer page_size;
}