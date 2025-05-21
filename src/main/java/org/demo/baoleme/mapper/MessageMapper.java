package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.demo.baoleme.pojo.Message;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    // 可自定义扩展方法
    @Select("""
    SELECT sender_id, sender_role, receiver_id, receiver_role, content, created_at
    FROM message
    WHERE (
        (sender_id = #{senderId} AND sender_role = #{senderRole}
         AND receiver_id = #{receiverId} AND receiver_role = #{receiverRole})
        OR
        (sender_id = #{receiverId} AND sender_role = #{receiverRole}
         AND receiver_id = #{senderId} AND receiver_role = #{senderRole})
    )
    ORDER BY created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<Message> selectChatHistory(
            @Param("senderId") Long senderId,
            @Param("senderRole") String senderRole,
            @Param("receiverId") Long receiverId,
            @Param("receiverRole") String receiverRole,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
}