package org.demo.baoleme.dto.request.product;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.demo.baoleme.pojo.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductAddRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String image;
}
