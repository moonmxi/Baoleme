package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.review.ReviewReadRequest;
import org.demo.baoleme.dto.response.review.ReviewPageResponse;
import org.demo.baoleme.dto.response.review.ReviewReadResponse;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/list")
    public CommonResponse getStoreReviews(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ReviewReadRequest request
    ) {
        System.out.println("收到请求：" + request);

        Long storeId = request.getStoreId();
        int page = request.getPage();
        int pageSize = request.getPageSize();

        // Step1: 验证分页参数合法性
        if (page < 1 || pageSize < 1) {
            return ResponseBuilder.fail("分页参数必须大于0");
        }

        // Step2: 查询分页数据（不进行评分筛选）
        Page<Review> reviewPage = reviewService.getStoreReviewsPage(
                storeId,
                page,
                pageSize
        );

        // Step3: 转换为响应结构（空列表也视为正常结果）
        ReviewPageResponse response = convertToPageResponse(reviewPage);

        System.out.println("回复：" + response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/filter")
    public CommonResponse getFilteredReviews(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ReviewReadRequest request
    ) {
        System.out.println("收到请求：" + request);

        Long storeId = request.getStoreId();

        // Step1: 验证分页参数合法性
        if (request.getPage() < 1 || request.getPageSize() < 1) {
            return ResponseBuilder.fail("分页参数必须大于0");
        }

        // Step2: 根据筛选类型确定评分范围
        Integer min = null;
        Integer max = null;
        if (request.getType() != null) {
            switch (request.getType()) {
                case POSITIVE:
                    min = 4;
                    max = 5;
                    break;
                case NEGATIVE:
                    min = 1;
                    max = 2;
                    break;
                default:
                    return ResponseBuilder.fail("无效的筛选类型");
            }
        }

        // Step3: 查询分页数据
        Page<Review> reviewPage = reviewService.getFilteredReviews(
                storeId,
                min,
                max,
                request.getHasImage(),
                request.getPage(),
                request.getPageSize()
        );

        // Step4: 转换为响应结构
        ReviewPageResponse response = convertToPageResponse(reviewPage);

        // Step5: 返回结果
        return ResponseBuilder.ok(response);
    }

    // 辅助方法：将Review转换为ReviewReadResponse
    private ReviewPageResponse convertToPageResponse(Page<Review> reviewPage) {
        ReviewPageResponse response = new ReviewPageResponse();
        response.setCurrentPage(reviewPage.getCurrPage());
        response.setPageSize(reviewPage.getPageSize());
        response.setTotalCount(reviewPage.getCount());
        response.setTotalPages(reviewPage.getPageCount());
        response.setPrePage(reviewPage.getPrePage());
        response.setNextPage(reviewPage.getNextPage());

        // 转换评论列表
        List<ReviewReadResponse> reviews = reviewPage.getList().stream().map(review -> {
            ReviewReadResponse item = new ReviewReadResponse();
            item.setUsername("用户" + review.getUserId());  // 用户占位逻辑
            item.setRating(review.getRating());
            item.setComment(review.getComment());
            item.setCreatedAt(review.getCreatedAt());
            item.setUserAvatar("");  // 空头像
            item.setImage(review.getImage());
            return item;
        }).collect(Collectors.toList());

        response.setReviews(reviews);

        System.out.println("回复：" + response);
        return response;
    }
}