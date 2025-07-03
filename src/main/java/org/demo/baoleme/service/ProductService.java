package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Product;

public interface ProductService {

    /**
     * 创建商品
     * @param product 商品信息（无需ID）
     * @return 包含ID的商品对象
     */
    Product createProduct(Product product);

    /**
     * 根据ID获取商品
     * @param productId 商品ID
     * @return 商品对象或null
     */
    Product getProductById(Long productId);

    /**
     * 查询店铺商品列表
     * @param storeId 店铺ID
     * @return 商品列表
     */
    Page<Product> getProductsByStore(Long storeId, int page, int pageSize);

    /**
     * 更新商品信息
     * @param product 商品对象（需包含ID）
     * @return 是否成功
     */
    boolean updateProduct(Product product);

    /**
     * 更新商品状态
     * @param productId 商品ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateProductStatus(Long productId, int status);

    /**
     * 删除商品
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean deleteProduct(Long productId);

    /**
     * 更新产品图片路径（单张产品图；若需多图，可改为保存到关联表）
     * @param productId 产品主键 ID
     * @param imagePath 新图片相对路径
     * @return true 表示成功，false 表示失败
     */
    boolean updateImage(Long productId, String imagePath);
}