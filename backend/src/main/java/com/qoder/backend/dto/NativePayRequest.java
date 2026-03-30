package com.qoder.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NativePayRequest {
    
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
}
