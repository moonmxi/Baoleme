package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rider")
public class Rider {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String phone;

    private Integer orderStatus;    // -1=未激活, 0=接单中, 1=空闲

    private Integer dispatchMode;   // 0=手动，1=自动

    private Long balance;

    private String avatar;

    private LocalDateTime createdAt;
}