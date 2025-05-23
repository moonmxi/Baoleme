package org.demo.baoleme.dto.request.user;
import lombok.Data;

@Data
public class UserFavoriteRequest {
    private Long storeId;
    String storeName;
    // 记得加 getter 和 setter
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {

        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
