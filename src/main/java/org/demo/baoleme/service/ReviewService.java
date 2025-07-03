package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Review;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewService {

    /**
     * 创建评论
     * @param review 评论对象（无需id和createdAt）
     * @return 包含生成的id和时间的评论对象
     */
    Review createReview(Review review);

    /**
     * 根据ID获取评论
     * @param reviewId 评论ID
     * @return 评论对象或null
     */
    Review getReviewById(Long reviewId);

    /**
     * 更新评论内容
     * @param review 需包含ID的评论对象
     * @return 是否成功
     */
    boolean updateReview(Review review);

    /**
     * 删除评论
     * @param reviewId 评论ID
     * @return 是否成功
     */
    boolean deleteReview(Long reviewId);

    /**
     * 查询店铺的所有评论
     * @param storeId 店铺ID
     * @return 评论列表
     */
    List<Review> getReviewsByStore(Long storeId);

    /**
     * 根据评分范围查询评论
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @return 评论列表
     */
    List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating);

    Page<Review> getFilteredReviews(
            Long storeId,
            Integer minRating,
            Integer maxRating,
            Boolean hasImage,
            int page,
            int pageSize
    );

    @Transactional(readOnly = true)
    Page<Review> getStoreReviewsPage(Long storeId, int page, int pageSize);

    List<Review> getReviewsByProductId(Long id);
}