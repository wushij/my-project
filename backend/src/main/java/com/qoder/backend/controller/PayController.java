package com.qoder.backend.controller;

import com.qoder.backend.dto.NativePayRequest;
import com.qoder.backend.dto.NativePayResponse;
import com.qoder.backend.dto.Result;
import com.qoder.backend.entity.Order;
import com.qoder.backend.service.OrderService;
import com.qoder.backend.service.WechatPayService;
import com.qoder.backend.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {
    
    private final WechatPayService wechatPayService;
    private final OrderService orderService;
    private final JwtUtils jwtUtils;
    
    /**
     * 发起Native支付（返回二维码链接）
     */
    @PostMapping("/native")
    public Result<NativePayResponse> createNativePay(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody NativePayRequest request) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        // 获取订单
        Order order = orderService.getByOrderNo(request.getOrderNo());
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 验证订单归属
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        
        // 验证订单状态
        if (order.getStatus() != Order.STATUS_PENDING) {
            return Result.error("订单状态异常");
        }
        
        try {
            NativePayResponse response = wechatPayService.createNativePay(order);
            return Result.success(response);
        } catch (Exception e) {
            log.error("发起支付失败", e);
            return Result.error("发起支付失败: " + e.getMessage());
        }
    }
    
    /**
     * 微信支付回调通知
     */
    @PostMapping("/notify")
    public Map<String, String> payNotify(HttpServletRequest request, @RequestBody String body) {
        log.info("收到微信支付回调通知");
        return wechatPayService.handlePayNotify(request, body);
    }
    
    /**
     * 主动查询支付结果
     */
    @GetMapping("/query/{orderNo}")
    public Result<Boolean> queryPayResult(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderNo) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        boolean paid = wechatPayService.queryPayResult(orderNo);
        return Result.success(paid);
    }
    
    private Long getUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return null;
        }
        return jwtUtils.getUserIdFromToken(token);
    }
}
