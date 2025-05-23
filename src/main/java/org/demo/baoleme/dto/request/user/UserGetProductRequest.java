package org.demo.baoleme.dto.request.user;

import lombok.Data;

@Data
public class UserGetProductRequest {
    private String category; // 可为空

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
