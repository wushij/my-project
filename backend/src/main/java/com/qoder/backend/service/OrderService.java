package com.qoder.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoder.backend.dto.CreateOrderResponse;
import com.qoder.backend.entity.Order;
import com.qoder.backend.entity.Plan;

import java.util.List;

public interface OrderService extends IService<Order> {
    
    /**
     * 获取可用套餐列表
     */
    List<Plan> getAvailablePlans();
    
    /**
     * 创建订单
     */
    CreateOrderResponse createOrder(Long planId);
    
    /**
     * 根据订单号获取订单
     */
    Order getByOrderNo(String orderNo);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(String orderNo);
    
    /**
     * 更新订单为已支付状态
     */
    boolean paySuccess(String orderNo, String transactionId);
}
