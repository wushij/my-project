package com.qoder.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 套餐ID
     */
    private Long planId;
    
    /**
     * 套餐名称
     */
    private String planName;
    
    /**
     * 支付金额（元）
     */
    private BigDecimal amount;
    
    /**
     * 状态：0-待支付 1-已支付 2-已取消 3-已退款
     */
    private Integer status;
    
    /**
     * 支付方式：WECHAT_NATIVE
     */
    private String payType;
    
    /**
     * 微信支付订单号
     */
    private String transactionId;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 订单过期时间
     */
    private LocalDateTime expireTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 订单状态常量
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_CANCELLED = 2;
    public static final int STATUS_REFUNDED = 3;
}
