package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.ReviewMapper;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        review.setId(null); // 确保ID由数据库生成
        review.setCreatedAt(null); // 由自动填充处理
        int result = reviewMapper.insert(review);
        return result > 0 ? review : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReviewById(Long reviewId) {
        return reviewMapper.selectById(reviewId);
    }

    @Override
    @Transactional
    public List<Review> getReviewsByProductId(Long id) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProductId, id)
        );
    }

    @Override
    @Transactional
    public boolean updateReview(Review review) {
        // 禁止更新createdAt字段
        review.setCreatedAt(null);
        return reviewMapper.updateById(review) > 0;
    }

    @Override
    @Transactional
    public boolean deleteReview(Long reviewId) {
        return reviewMapper.deleteById(reviewId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByStore(Long storeId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getStoreId, storeId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .between(Review::getRating, minRating, maxRating)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getFilteredReviews(
            Long storeId,
            Integer minRating,
            Integer maxRating,
            Boolean hasImage,
            int page,
            int pageSize
    ) {
        // Step1: 构建查询条件（不包含分页）
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStoreId, storeId);

        if (minRating != null && maxRating != null) {
            wrapper.between(Review::getRating, minRating, maxRating);
        }

        if (hasImage != null && hasImage) {
            wrapper.isNotNull(Review::getImage);
        }

        // Step2: 查询总记录数
        int totalCount = reviewMapper.selectCount(wrapper).intValue();

        // Step3: 计算总页数
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // Step4: 修正非法页码
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        return getReviewPage(page, pageSize, wrapper, totalCount, totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getStoreReviewsPage(Long storeId, int page, int pageSize) {
        // Step1: 构建基础查询条件
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStoreId, storeId);

        // Step2: 查询总记录数
        int totalCount = reviewMapper.selectCount(wrapper).intValue();

        // Step3: 计算总页数
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // Step4: 修正非法页码
        page = Math.max(page, 1);
        page = totalPages > 0 ? Math.min(page, totalPages) : 1;

        return getReviewPage(page, pageSize, wrapper, totalCount, totalPages);
    }

    private Page<Review> getReviewPage(int page, int pageSize, LambdaQueryWrapper<Review> wrapper, int totalCount, int totalPages) {
        // Step5: 计算分页偏移量
        int offset = (page - 1) * pageSize;
        wrapper.last("LIMIT " + pageSize + " OFFSET " + offset);

        // Step6: 执行分页查询
        List<Review> records = reviewMapper.selectList(wrapper);

        // Step7: 构建自定义分页对象
        Page<Review> resultPage = new Page<>();
        resultPage.setCurrPage(page);
        resultPage.setPageSize(pageSize);
        resultPage.setCount(totalCount);
        resultPage.setPageCount(totalPages);
        resultPage.setList(records);

        // Step8: 计算相邻页码
        if (page > 1) resultPage.setPrePage(page - 1);
        if (page < totalPages) resultPage.setNextPage(page + 1);

        return resultPage;
    }
}