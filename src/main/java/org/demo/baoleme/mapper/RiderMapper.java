package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.pojo.Rider;

import java.util.List;

@Mapper
public interface RiderMapper extends BaseMapper<Rider> {

    /**
     * 根据用户名查找骑手
     * @param username 用户名
     * @return Rider 对象或 null
     */
    @Select("SELECT id, username, password, phone, order_status, dispatch_mode, balance, created_at, avatar " +
            "FROM rider WHERE username = #{username} LIMIT 1")
    Rider selectByUsername(String username);

    /**
     * 根据手机号查找骑手
     *
     * @param phone 手机号
     * @return Rider 对象或 null
     */
    @Select("SELECT id, username, password, phone,order_status, dispatch_mode, balance, created_at, avatar " +
            "FROM rider WHERE phone = #{phone} LIMIT 1")
    Rider selectByPhone(String phone);

    @Select("""
        SELECT id, username, phone, order_status, dispatch_mode, balance,  avatar, created_at
        FROM rider
        ORDER BY id DESC
        LIMIT #{offset}, #{limit}
    """)
    List<Rider> selectRidersPaged(@Param("offset") int offset, @Param("limit") int limit);

    @Update("UPDATE rider SET order_status = 0 WHERE id = #{riderId}")
    int updateRiderOrderStatusAfterOrderCompletion(@Param("riderId") Long riderId);

    @Delete("DELETE FROM rider WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);

    @Select("""
        SELECT id, username, phone, order_status, dispatch_mode, balance, avatar, created_at
        FROM rider
        WHERE order_status = 1
        AND dispatch_mode = 1
        ORDER BY RAND()
        LIMIT 1
    """)
    Rider selectRandomRiderToSendOrder();
}