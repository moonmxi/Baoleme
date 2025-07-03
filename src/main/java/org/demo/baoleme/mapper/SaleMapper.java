package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.dto.response.salesStats.SaleTrendData;
import org.demo.baoleme.pojo.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SaleMapper extends BaseMapper<Sale> {

    /**
     * 查询指定商店在某个日期之后的销售记录
     * @param storeId 商店ID
     * @param saleDate 起始日期（包含该日期）
     * @return 销售记录列表
     */
    @Select("SELECT * FROM sales WHERE store_id = #{storeId} AND sale_date >= #{saleDate}")
    List<Sale> selectSalesByStoreIdAndDateAfter(Long storeId, LocalDate saleDate);

    @Select("SELECT SUM(total_amount) FROM sales WHERE store_id = #{storeId} AND sale_date BETWEEN #{start} AND #{end}")
    BigDecimal sumTotalAmountByStoreAndDate(@Param("storeId") Long storeId,
                                            @Param("start") LocalDate start,
                                            @Param("end") LocalDate end);

    @Select("SELECT DATE_FORMAT(sale_date, #{format}) AS date_label, SUM(total_amount) AS value " +
            "FROM sales WHERE store_id = #{storeId} AND sale_date BETWEEN #{start} AND #{end} " +
            "GROUP BY date_label ORDER BY date_label")
    List<SaleTrendData> findSalesTrend(@Param("storeId") Long storeId,
                                       @Param("format") String format,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    @Select("SELECT COUNT(DISTINCT id) FROM sales WHERE store_id = #{storeId} AND sale_date BETWEEN #{start} AND #{end}")
    int getOrderCount(@Param("storeId") Long storeId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT s.product_id, SUM(s.quantity) as total_quantity " +
            "FROM sales s " +
            "JOIN product p ON s.product_id = p.id " +
            "WHERE s.store_id = #{storeId} " +
            "AND s.sale_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) " + // 限制最近30天
            "GROUP BY s.product_id " +
            "ORDER BY total_quantity DESC " +
            "LIMIT 3")
    List<ProductSalesDTO> selectTop3ProductsByStore(@Param("storeId") Long storeId);
}