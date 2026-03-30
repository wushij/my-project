package com.qoder.backend.service;

import com.qoder.backend.dto.NativePayResponse;
import com.qoder.backend.entity.Order;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface WechatPayService {
    
    /**
     * 发起Native支付
     */
    NativePayResponse createNativePay(Order order);
    
    /**
     * 处理支付回调通知
     */
    Map<String, String> handlePayNotify(HttpServletRequest request, String body);
    
    /**
     * 主动查询支付结果
     */
    boolean queryPayResult(String orderNo);
}
