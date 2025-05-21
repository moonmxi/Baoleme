package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.demo.baoleme.mapper.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private final StoreMapper storeMapper;

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

    @Override
    @Transactional
    public boolean updateProduct(Product product) {
        // Step1: 校验product或ID是否为空
        if (product == null || product.getId() == null) {
            return false;
        }

        // Step2: 动态更新非空字段
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", product.getId());
        if (product.getName() != null) {
            updateWrapper.set("name", product.getName());
        }
        if (product.getPrice() != null) {
            updateWrapper.set("price", product.getPrice());
        }
        if (product.getStatus() != null) {
            updateWrapper.set("status", product.getStatus());
        }
        if (product.getStoreId() != null) {
            updateWrapper.set("store_id", product.getStoreId());
        }

        // Step3: 执行更新操作
        int result = productMapper.update(null, updateWrapper);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Long productId, int status) {
        // Step1: 校验productId是否为空
        if (productId == null) {
            return false;
        }

        // Step2: 更新商品状态
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int result = productMapper.updateById(product);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId) {
        // Step1: 校验productId是否为空
        if (productId == null) {
            return false;
        }

        // Step2: 执行删除操作
        int result = productMapper.deleteById(productId);
        return result > 0;
    }
}