package com.qoder.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_plan")
public class UserPlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 套餐ID
     */
    private Long planId;
    
    /**
     * 套餐名称
     */
    private String planName;
    
    /**
     * 总Credits
     */
    private Integer credits;
    
    /**
     * 已使用Credits
     */
    private Integer usedCredits;
    
    /**
     * 生效时间
     */
    private LocalDateTime startTime;
    
    /**
     * 到期时间
     */
    private LocalDateTime endTime;
    
    /**
     * 关联订单号
     */
    private String orderNo;
    
    /**
     * 状态：0-已过期 1-生效中
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
