package com.qoder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoder.backend.config.WechatPayConfig;
import com.qoder.backend.dto.CreateOrderResponse;
import com.qoder.backend.entity.Order;
import com.qoder.backend.entity.Plan;
import com.qoder.backend.mapper.OrderMapper;
import com.qoder.backend.service.OrderService;
import com.qoder.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    private final PlanService planService;
    
    @Autowired(required = false)
    private WechatPayConfig wechatPayConfig;
    
    @Override
    public List<Plan> getAvailablePlans() {
        return planService.getAvailablePlans();
    }
    
    @Override
    @Transactional
    public CreateOrderResponse createOrder(Long planId) {
        // 获取套餐信息
        Plan plan = planService.getById(planId);
        if (plan == null || plan.getStatus() != 1) {
            throw new RuntimeException("套餐不存在");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setPlanId(planId);
        order.setPlanName(plan.getName());
        order.setAmount(plan.getPrice());
        order.setStatus(Order.STATUS_PENDING);
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        
        save(order);
        
        // 构建响应
        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPlanName(order.getPlanName());
        response.setAmount(order.getAmount());
        response.setExpireTime(order.getExpireTime());
        
        return response;
    }
    
    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return getOne(wrapper);
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(String orderNo) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo)
               .eq(Order::getStatus, Order.STATUS_PENDING)
               .set(Order::getStatus, Order.STATUS_CANCELLED);
        return update(wrapper);
    }
    
    @Override
    @Transactional
    public boolean paySuccess(String orderNo, String transactionId) {
        Order order = getByOrderNo(orderNo);
        if (order == null) {
            log.error("订单不存在: {}", orderNo);
            return false;
        }
        
        // 幂等性检查
        if (order.getStatus() == Order.STATUS_PAID) {
            log.info("订单已支付，跳过: {}", orderNo);
            return true;
        }
        
        // 更新订单状态
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo)
               .eq(Order::getStatus, Order.STATUS_PENDING)
               .set(Order::getStatus, Order.STATUS_PAID)
               .set(Order::getTransactionId, transactionId)
               .set(Order::getPayType, "WECHAT_NATIVE")
               .set(Order::getPayTime, LocalDateTime.now());
        
        return update(wrapper);
    }
    
    /**
     * 生成订单号：年月日时分秒 + 6位随机数
     */
    private String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return dateTime + random;
    }
}
