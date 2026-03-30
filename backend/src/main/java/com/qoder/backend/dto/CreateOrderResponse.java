package com.qoder.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateOrderResponse {
    
    private String orderNo;
    
    private String planName;
    
    private BigDecimal amount;
    
    private LocalDateTime expireTime;
}
