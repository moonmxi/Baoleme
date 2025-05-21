package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.SaleMapper;
import org.demo.baoleme.pojo.Sale;
import org.demo.baoleme.service.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;

    public SaleServiceImpl(SaleMapper saleMapper) {
        this.saleMapper = saleMapper;
    }

    @Override
    @Transactional
    public Sale createSale(Sale sale) {
        // 确保不手动设置totalAmount
        sale.setTotalAmount(null);
        int result = saleMapper.insert(sale);
        return result > 0 ? sale : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Sale getSaleById(Long saleId) {
        return saleMapper.selectById(saleId);
    }

    @Override
    @Transactional
    public boolean updateSale(Sale sale) {
        // 更新时排除totalAmount字段
        sale.setTotalAmount(null);
        return saleMapper.updateById(sale) > 0;
    }

    @Override
    @Transactional
    public boolean deleteSale(Long saleId) {
        return saleMapper.deleteById(saleId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> getSalesByStore(Long storeId) {
        return saleMapper.selectList(
                new LambdaQueryWrapper<Sale>()
                        .eq(Sale::getStoreId, storeId)
                        .apply("USE INDEX (idx_store_id)") // 强制使用索引
        );
    }
}