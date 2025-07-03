package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.ProductService;
import org.demo.baoleme.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private final StoreMapper storeMapper;
    @Autowired
    private StoreService storeService;

    public ProductServiceImpl(
            ProductMapper productMapper,
            StoreMapper storeMapper
    ) {
        this.productMapper = productMapper;
        this.storeMapper = storeMapper;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        // Step1: 校验storeId、name和price是否为空
        if (product.getStoreId() == null || product.getName() == null || product.getPrice() == null) {
            return null;
        }

        // Step2: 检查store_id是否存在
        Store store = storeMapper.selectById(product.getStoreId());
        if (store == null) {
            System.out.println("错误：店铺ID不存在");
            return null;
        }

        // Step3: 插入商品数据
        int result = productMapper.insert(product);
        return result > 0 ? product : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        // Step1: 校验productId是否为空
        if (productId == null) {
            return null;
        }

        // Step2: 查询商品详情
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByStore(Long storeId, int currentPage, int pageSize) {
        // Step1: 校验storeId是否为空
        if (storeId == null) {
            System.out.println("警告：店铺ID为空");
            return new Page<>();
        }

        // Step1: 创建分页对象
        Page<Product> page = new Page<>();
        page.setCurrPage(currentPage);
        page.setPageSize(pageSize);

        // Step2: 计算偏移量
        int offset = (currentPage - 1) * pageSize;

        // Step3: 查询数据
        List<Product> products = productMapper.selectByStore(storeId, offset, pageSize);
        int totalCount = productMapper.countByStore(storeId);

        // Step4: 计算分页信息
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // Step5: 填充分页对象
        page.setList(products);
        page.setCount(totalCount);
        page.setPageCount(totalPages);
        page.setPrePage(currentPage > 1 ? currentPage - 1 : null);
        page.setNextPage(currentPage < totalPages ? currentPage + 1 : null);

        return page;
    }

    /* ========================= 商品更新 ========================= */

    @Override
    @Transactional
    public boolean updateProduct(Product product) {
        // Step1: 基础参数校验
        if (product == null || product.getId() == null) {
            System.out.println("[WARN] 更新失败：商品参数不完整");
            return false;
        }

        // Step2: 获取持久化对象
        Product existing = getExistingProduct(product.getId());
        if (existing == null) return false;

        // Step3: 安全合并字段更新
        applyProductUpdates(product, existing);

        // Step4: 执行更新操作
        return productMapper.updateById(existing) > 0;
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Long productId, int status) {
        // Step1: 参数有效性校验
        if (productId == null || (status != 0 && status != 1)) {
            System.out.println("[WARN] 状态更新失败：参数不合法");
            return false;
        }

        // Step2: 获取持久化对象
        Product existing = getExistingProduct(productId);
        if (existing == null) return false;

        // Step3: 应用状态更新
        existing.setStatus(status);

        // Step4: 执行更新
        return productMapper.updateById(existing) > 0;
    }

    /* ------------------------- 安全更新策略 ------------------------- */

    /**
     * 安全合并商品字段更新
     * @param source 包含新数据的源对象
     * @param target 需要更新的目标对象
     */
    private void applyProductUpdates(Product source, Product target) {
        // 名称更新
        if (source.getName() != null) {
            target.setName(source.getName());
        }

        // 价格更新
        if (source.getPrice() != null) {
            target.setPrice(source.getPrice());
        }

        if(source.getCategory() != null) {
            target.setCategory(source.getCategory());
        }

        if(source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }

        if(source.getImage() != null) {
            target.setImage(source.getImage());
        }

        if(source.getStock() != null){
            target.setStock(source.getStock());
        }

        if(source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
    }

    /**
     * 获取存在的商品对象
     */
    private Product getExistingProduct(Long productId) {
        Product existing = productMapper.selectById(productId);
        if (existing == null) {
            System.out.println("[WARN] 操作失败：商品不存在 ID=" + productId);
            return null;
        }
        if(!storeService.validateStoreOwnership(existing.getStoreId(), UserHolder.getId()))
            return null;
        return existing;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId) {
        // Step1: 校验productId是否为空
        if (productId == null) {
            return false;
        }

        Product existing = getExistingProduct(productId);
        if(existing == null) return false;

        // Step2: 执行删除操作
        int result = productMapper.deleteById(productId);
        return result > 0;
    }

    @Override
    public boolean updateImage(Long productId, String imagePath) {
        if (productId == null || !StringUtils.hasText(imagePath)) {
            return false;
        }
        int rows = productMapper.updateImageById(productId, imagePath);
        return rows > 0;
    }
}