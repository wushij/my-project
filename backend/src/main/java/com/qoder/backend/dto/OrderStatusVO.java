package com.qoder.backend.dto;

import lombok.Data;

@Data
public class OrderStatusVO {
    
    private String orderNo;
    
    private Integer status;
    
    private String statusText;
}
