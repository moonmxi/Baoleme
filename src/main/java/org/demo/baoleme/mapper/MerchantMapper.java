package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.pojo.Rider;

import java.util.List;

public interface MerchantMapper extends BaseMapper<Merchant> {
    // 插入商户（返回自增主键）
    @Insert("INSERT INTO merchant (username, password, phone, created_at) "
            + "VALUES (#{username}, #{password}, #{phone}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 获取自增主键
    int insertMerchant(Merchant merchant);

    @Select("SELECT * FROM merchant WHERE username = #{username}")
    Merchant selectByUsername(String username);

    // 根据ID查询商户
    @Select("SELECT * FROM merchant WHERE id = #{id}")
    Merchant selectById(Long id);

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

    // 根据ID删除商户
    @Delete("DELETE FROM merchant WHERE id = #{id}")
    int deleteById(Long id);
}
