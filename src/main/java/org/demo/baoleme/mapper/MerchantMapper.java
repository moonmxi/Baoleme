package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.pojo.Rider;

import java.util.List;

public interface MerchantMapper extends BaseMapper<Merchant> {

    @Select("SELECT * FROM merchant WHERE username = #{username}")
    Merchant selectByUsername(String username);

    /**
     * 根据手机号查找商家
     * @param phone 手机号
     * @return Merchant 对象或 null
     */
    @Select("SELECT * FROM merchant WHERE phone = #{phone} LIMIT 1")
    Merchant selectByPhone(String phone);

    // 查询所有商户
    @Select("SELECT * FROM merchant")
    List<Merchant> selectAll();

    // 更新商户信息
    @Update("UPDATE merchant SET username=#{username}, password=#{password}, phone=#{phone} "
            + "WHERE id = #{id}")
    int updateMerchant(Merchant merchant);

    @Select("""
    SELECT *
    FROM merchant
    WHERE (#{keyword} IS NULL OR username LIKE CONCAT('%', #{keyword}, '%'))
      AND (#{startId} IS NULL OR id >= #{startId})
      AND (#{endId} IS NULL OR id <= #{endId})
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
""")
    List<Merchant> selectMerchantsPaged(@Param("offset") int offset,
                                        @Param("limit") int limit,
                                        @Param("keyword") String keyword,
                                        @Param("startId") Long startId,
                                        @Param("endId") Long endId);

    @Delete("DELETE FROM merchant WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);

    // 根据ID删除商户
    @Delete("DELETE FROM merchant WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT m.phone FROM merchant m JOIN store s ON m.id = s.merchant_id WHERE s.id = #{storeId}")
    String selectPhoneByStoreId(Long storeId);

    @Update("UPDATE merchant SET avatar = #{avatarPath} WHERE id = #{merchantId}")
    int updateAvatarById(@Param("merchantId") Long merchantId, @Param("avatarPath") String avatarPath);
}
