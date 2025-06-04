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
    SELECT id, username, phone, order_status, dispatch_mode, balance, avatar, created_at
    FROM rider
    WHERE (#{keyword} IS NULL OR username LIKE CONCAT('%', #{keyword}, '%'))
      AND (#{startId} IS NULL OR id >= #{startId})
      AND (#{endId} IS NULL OR id <= #{endId})
      AND (#{status} IS NULL OR order_status = #{status})
      AND (#{dispatchMode} IS NULL OR dispatch_mode = #{dispatchMode})
      AND (#{startBalance} IS NULL OR balance >= #{startBalance})
      AND (#{endBalance} IS NULL OR balance <= #{endBalance})
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
""")
    List<Rider> selectRidersPaged(@Param("offset") int offset,
                                  @Param("limit") int limit,
                                  @Param("keyword") String keyword,
                                  @Param("startId") Long startId,
                                  @Param("endId") Long endId,
                                  @Param("status") Integer status,
                                  @Param("dispatchMode") Integer dispatchMode,
                                  @Param("startBalance") Long startBalance,
                                  @Param("endBalance") Long endBalance);

    @Update("UPDATE rider SET order_status = 0 WHERE id = #{riderId}")
    int updateRiderOrderStatusAfterOrderCompletion(@Param("riderId") Long riderId);

    @Delete("DELETE FROM rider WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);

    /**
     * 专门只更新 rider 表中的 avatar 字段
     * @param id     Rider 主键
     * @param avatar 相对路径字符串，例如 "rider/avatar/2025-06-04/abcdef.jpg"
     * @return 影响行数（>0 则成功）
     */
    @Update("UPDATE rider SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
}