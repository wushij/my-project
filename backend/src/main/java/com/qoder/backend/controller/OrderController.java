package com.qoder.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoder.backend.dto.*;
import com.qoder.backend.service.OrderService;
import com.qoder.backend.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final JwtUtils jwtUtils;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<CreateOrderResponse> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateOrderRequest request) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        try {
            CreateOrderResponse response = orderService.createOrder(userId, request.getPlanId());
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询订单状态（轮询用）
     */
    @GetMapping("/status/{orderNo}")
    public Result<OrderStatusVO> getOrderStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderNo) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        OrderStatusVO status = orderService.getOrderStatus(orderNo);
        if (status == null) {
            return Result.error("订单不存在");
        }
        return Result.success(status);
    }
    
    /**
     * 我的订单列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getMyOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        IPage<OrderVO> orders = orderService.getMyOrders(userId, page, pageSize);
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", orders.getTotal());
        data.put("list", orders.getRecords());
        
        return Result.success(data);
    }
    
    /**
     * 订单详情
     */
    @GetMapping("/{orderNo}")
    public Result<OrderVO> getOrderDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderNo) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        OrderVO order = orderService.getOrderDetail(orderNo, userId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result<Void> cancelOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderNo) {
        
        Long userId = getUserId(authHeader);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        boolean success = orderService.cancelOrder(orderNo, userId);
        if (!success) {
            return Result.error("取消订单失败，订单可能已支付或不存在");
        }
        return Result.success();
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
