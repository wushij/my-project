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
    
    /**
     * 套餐名称
     */
    private String name;
    
    /**
     * 套餐编码(FREE/PRO/PRO_PLUS/ULTRA)
     */
    private String code;
    
    /**
     * 价格（元）
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * Credits数量
     */
    private Integer credits;
    
    /**
     * 有效期（天）
     */
    private Integer duration;
    
    /**
     * 功能特性（JSON）
     */
    private String features;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态：0-下架 1-上架
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
