package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Sale;
import java.util.List;

public interface SaleService {

    /**
     * 创建销售记录（totalAmount由数据库自动计算）
     * @param sale 销售记录对象（无需id和totalAmount）
     * @return 包含生成的id的销售记录对象
     */
    Sale createSale(Sale sale);

    /**
     * 根据ID查询销售记录
     * @param saleId 销售记录ID
     * @return 销售记录对象或null
     */
    Sale getSaleById(Long saleId);

    /**
     * 更新销售记录（不更新totalAmount字段）
     * @param sale 需包含ID的完整对象
     * @return 是否成功
     */
    boolean updateSale(Sale sale);

    /**
     * 删除销售记录
     * @param saleId 销售记录ID
     * @return 是否成功
     */
    boolean deleteSale(Long saleId);

    /**
     * 查询店铺的销售记录
     * @param storeId 店铺ID
     * @return 销售记录列表
     */
    List<Sale> getSalesByStore(Long storeId);
}