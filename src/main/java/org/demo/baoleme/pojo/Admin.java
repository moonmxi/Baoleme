package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 管理员实体类，仅包含 id 和 password
 */
@Data
@TableName("administrator")
public class Admin {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("password")
    private String password;
}