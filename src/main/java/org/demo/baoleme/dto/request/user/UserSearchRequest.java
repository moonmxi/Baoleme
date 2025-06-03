package org.demo.baoleme.dto.request.user;
import lombok.Data;

@Data
public class UserSearchRequest {
    private String keyWord;
    private Integer page;
    private Integer pageSize;
}
