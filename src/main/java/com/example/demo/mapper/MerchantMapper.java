package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.entity.Merchant;

@Mapper // Spring Boot中需添加此注解，MyBatis会自动生成实现类
public interface MerchantMapper {

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
