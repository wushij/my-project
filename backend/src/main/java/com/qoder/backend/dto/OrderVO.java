package com.qoder.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    
    private String orderNo;
    
    private String planName;
    
    private BigDecimal amount;
    
    private Integer status;
    
    private LocalDateTime payTime;
    
    private LocalDateTime expireTime;
    
    private LocalDateTime createTime;
}
