package org.demo.baoleme.dto.request;
import lombok.Data;

@Data
public class UserSearchRequest {
    private String keyword;
    private int page;
    private int size;
}
