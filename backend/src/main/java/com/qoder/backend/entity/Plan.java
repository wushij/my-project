package com.qoder.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("plan")
public class Plan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String code;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer credits;
    
    private Integer duration;
    
    private Integer sort;
    
    private Integer status;
}
