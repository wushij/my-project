package com.qoder.backend.service.impl;

import com.qoder.backend.config.WechatPayConfig;
import com.qoder.backend.dto.NativePayResponse;
import com.qoder.backend.entity.Order;
import com.qoder.backend.service.OrderService;
import com.qoder.backend.service.WechatPayService;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(WechatPayConfig.class)
public class WechatPayServiceImpl implements WechatPayService {
    
    private final WechatPayConfig wechatPayConfig;
    private final NativePayService nativePayService;
    private final NotificationParser notificationParser;
    private final OrderService orderService;
    
    @Override
    public NativePayResponse createNativePay(Order order) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(wechatPayConfig.getAppId());
        request.setMchid(wechatPayConfig.getMchId());
        request.setDescription(order.getPlanName() + " 套餐订阅");
        request.setOutTradeNo(order.getOrderNo());
        request.setNotifyUrl(wechatPayConfig.getNotifyUrl());
        
        // 设置金额（单位：分）
        Amount amount = new Amount();
        amount.setTotal(order.getAmount().multiply(new BigDecimal("100")).intValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);
        
        // 调用微信支付API
        PrepayResponse response = nativePayService.prepay(request);
        
        NativePayResponse result = new NativePayResponse();
        result.setCodeUrl(response.getCodeUrl());
        result.setOrderNo(order.getOrderNo());
        
        log.info("Native支付下单成功: orderNo={}, codeUrl={}", order.getOrderNo(), response.getCodeUrl());
        
        return result;
    }
    
    @Override
    public Map<String, String> handlePayNotify(HttpServletRequest request, String body) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // 构建请求参数
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(request.getHeader("Wechatpay-Serial"))
                    .nonce(request.getHeader("Wechatpay-Nonce"))
                    .signature(request.getHeader("Wechatpay-Signature"))
                    .timestamp(request.getHeader("Wechatpay-Timestamp"))
                    .body(body)
                    .build();
            
            // 解析通知数据
            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
            
            String orderNo = transaction.getOutTradeNo();
            String transactionId = transaction.getTransactionId();
            
            log.info("收到支付回调: orderNo={}, transactionId={}, state={}", 
                    orderNo, transactionId, transaction.getTradeState());
            
            // 支付成功
            if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                orderService.paySuccess(orderNo, transactionId);
            }
            
            result.put("code", "SUCCESS");
            result.put("message", "成功");
            
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            result.put("code", "FAIL");
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public boolean queryPayResult(String orderNo) {
        try {
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(wechatPayConfig.getMchId());
            request.setOutTradeNo(orderNo);
            
            Transaction transaction = nativePayService.queryOrderByOutTradeNo(request);
            
            log.info("查询支付结果: orderNo={}, state={}", orderNo, transaction.getTradeState());
            
            if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                orderService.paySuccess(orderNo, transaction.getTransactionId());
                return true;
            }
            
        } catch (Exception e) {
            log.error("查询支付结果失败: orderNo={}", orderNo, e);
        }
        
        return false;
    }
}
