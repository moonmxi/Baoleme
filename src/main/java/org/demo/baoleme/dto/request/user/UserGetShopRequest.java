package org.demo.baoleme.dto.request.user;
import lombok.Data;

@Data
public class UserGetShopRequest {
    private String description; // 可为空

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
