package com.qoder.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoder.backend.dto.CreateOrderResponse;
import com.qoder.backend.dto.OrderStatusVO;
import com.qoder.backend.dto.OrderVO;
import com.qoder.backend.entity.Order;

public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    CreateOrderResponse createOrder(Long userId, Long planId);
    
    /**
     * 查询订单状态
     */
    OrderStatusVO getOrderStatus(String orderNo);
    
    /**
     * 获取我的订单列表
     */
    IPage<OrderVO> getMyOrders(Long userId, Integer page, Integer pageSize);
    
    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(String orderNo, Long userId);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(String orderNo, Long userId);
    
    /**
     * 根据订单号获取订单
     */
    Order getByOrderNo(String orderNo);
    
    /**
     * 更新订单为已支付状态
     */
    boolean paySuccess(String orderNo, String transactionId);
}
