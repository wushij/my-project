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
    
    private String statusText;
    
    private LocalDateTime payTime;
    
    private LocalDateTime expireTime;
    
    private LocalDateTime createTime;
    
    /**
     * 获取状态文本
     */
    public static String getStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已取消";
            case 3 -> "已退款";
            default -> "";
        };
    }
}
