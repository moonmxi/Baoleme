package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.demo.baoleme.pojo.Admin;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据管理员 id 查询管理员信息
     */
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin selectById(Long id);
}