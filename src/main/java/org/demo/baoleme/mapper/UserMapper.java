package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.demo.baoleme.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT id, username, password, phone, avatar, created_at " +
            "FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    @Select("SELECT id, username, password, phone, avatar, created_at " +
            "FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(String phone);

    @Select("SELECT id, username, password, phone, avatar, created_at " +
            "FROM user WHERE id = #{id} LIMIT 1")
    User selectById(Long id);

    @Select("""
                SELECT id, username, phone, avatar, created_at
                FROM user
                ORDER BY created_at DESC
                LIMIT #{offset}, #{limit}
            """)
    List<User> selectUsersPaged(@Param("offset") int offset,
                                @Param("limit") int limit);

    @Delete("DELETE FROM user WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);

}
