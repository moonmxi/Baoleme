// 新增响应类：ReviewPageResponse.java
package org.demo.baoleme.dto.response.review;

import lombok.Data;
import java.util.List;

@Data
public class ReviewPageResponse {
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPages;
    private Integer prePage;
    private Integer nextPage;
    private List<ReviewReadResponse> reviews;
}