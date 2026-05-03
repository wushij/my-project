package com.qoder.backend.controller;

import com.qoder.backend.dto.CreateOrderRequest;
import com.qoder.backend.dto.CreateOrderResponse;
import com.qoder.backend.dto.NativePayRequest;
import com.qoder.backend.dto.NativePayResponse;
import com.qoder.backend.dto.OrderVO;
import com.qoder.backend.dto.Result;
import com.qoder.backend.entity.Order;
import com.qoder.backend.entity.Plan;
import com.qoder.backend.service.OrderService;
import com.qoder.backend.service.WechatPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    
    private final WechatPayService wechatPayService;
    private final OrderService orderService;
    
    /**
     * 获取套餐列表
     */
    @GetMapping("/plans")
    public Result<List<Plan>> getPlans() {
        return Result.success(orderService.getAvailablePlans());
    }
    
    /**
     * 创建订单
     */
    @PostMapping("/order/create")
    public Result<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            CreateOrderResponse response = orderService.createOrder(request.getPlanId());
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 发起Native支付
     */
    @PostMapping("/pay/native")
    public Result<NativePayResponse> createNativePay(@Valid @RequestBody NativePayRequest request) {
        Order order = orderService.getByOrderNo(request.getOrderNo());
        if (order == null) {
            return Result.error("订单不存在");
        }
        
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
     * 查询订单状态
     */
    @GetMapping("/order/status/{orderNo}")
    public Result<OrderVO> getOrderStatus(@PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 订单未支付时，主动查询微信支付状态
        if (order.getStatus() == Order.STATUS_PENDING) {
            try {
                boolean paid = wechatPayService.queryPayResult(orderNo);
                if (paid) {
                    order = orderService.getByOrderNo(orderNo);
                }
            } catch (Exception e) {
                log.warn("查询微信支付状态失败: {}", e.getMessage());
            }
        }
        
        OrderVO vo = new OrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setPlanName(order.getPlanName());
        vo.setAmount(order.getAmount());
        vo.setStatus(order.getStatus());
        vo.setExpireTime(order.getExpireTime());
        vo.setCreateTime(order.getCreateTime());
        
        return Result.success(vo);
    }
    
    /**
     * 微信支付回调通知
     */
    @PostMapping("/pay/notify")
    public Map<String, String> payNotify(HttpServletRequest request, @RequestBody String body) {
        log.info("收到微信支付回调通知");
        return wechatPayService.handlePayNotify(request, body);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/order/cancel/{orderNo}")
    public Result<Void> cancelOrder(@PathVariable String orderNo) {
        boolean success = orderService.cancelOrder(orderNo);
        if (!success) {
            return Result.error("取消订单失败");
        }
        return Result.success();
    }
}
