package com.qoder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoder.backend.config.WechatPayConfig;
import com.qoder.backend.dto.CreateOrderResponse;
import com.qoder.backend.dto.OrderStatusVO;
import com.qoder.backend.dto.OrderVO;
import com.qoder.backend.entity.Order;
import com.qoder.backend.entity.Plan;
import com.qoder.backend.mapper.OrderMapper;
import com.qoder.backend.service.OrderService;
import com.qoder.backend.service.PlanService;
import com.qoder.backend.service.UserPlanService;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    private final PlanService planService;
    private final UserPlanService userPlanService;
    
    @Autowired(required = false)
    private WechatPayConfig wechatPayConfig;
    
    @Autowired(required = false)
    private NativePayService nativePayService;
    
    @Override
    @Transactional
    public CreateOrderResponse createOrder(Long userId, Long planId) {
        // 获取套餐信息
        Plan plan = planService.getById(planId);
        if (plan == null || plan.getStatus() != 1) {
            throw new RuntimeException("套餐不存在或已下架");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPlanId(planId);
        order.setPlanName(plan.getName());
        order.setAmount(plan.getPrice());
        order.setStatus(Order.STATUS_PENDING);
        order.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟过期
        
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
    public OrderStatusVO getOrderStatus(String orderNo) {
        Order order = getByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        
        // 订单未支付时，主动查询微信支付状态
        if (order.getStatus() == Order.STATUS_PENDING && nativePayService != null && wechatPayConfig != null) {
            try {
                QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
                queryRequest.setMchid(wechatPayConfig.getMchId());
                queryRequest.setOutTradeNo(orderNo);
                Transaction transaction = nativePayService.queryOrderByOutTradeNo(queryRequest);
                
                if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                    // 支付成功，更新订单状态
                    paySuccess(orderNo, transaction.getTransactionId());
                    order = getByOrderNo(orderNo); // 重新获取更新后的订单
                }
            } catch (Exception e) {
                log.warn("主动查询微信支付状态失败: orderNo={}, error={}", orderNo, e.getMessage());
            }
        }
        
        OrderStatusVO vo = new OrderStatusVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderVO.getStatusText(order.getStatus()));
        
        return vo;
    }
    
    @Override
    public IPage<OrderVO> getMyOrders(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);
        
        Page<Order> orderPage = new Page<>(page, pageSize);
        IPage<Order> result = page(orderPage, wrapper);
        
        return result.convert(this::convertToVO);
    }
    
    @Override
    public OrderVO getOrderDetail(String orderNo, Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo)
               .eq(Order::getUserId, userId);
        
        Order order = getOne(wrapper);
        if (order == null) {
            return null;
        }
        
        return convertToVO(order);
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(String orderNo, Long userId) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo)
               .eq(Order::getUserId, userId)
               .eq(Order::getStatus, Order.STATUS_PENDING)
               .set(Order::getStatus, Order.STATUS_CANCELLED);
        
        return update(wrapper);
    }
    
    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return getOne(wrapper);
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
        
        boolean updated = update(wrapper);
        
        if (updated) {
            // 创建用户套餐
            Plan plan = planService.getById(order.getPlanId());
            if (plan != null) {
                userPlanService.createUserPlan(
                    order.getUserId(),
                    order.getPlanId(),
                    order.getPlanName(),
                    plan.getCredits(),
                    plan.getDuration(),
                    orderNo
                );
            }
        }
        
        return updated;
    }
    
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setPlanName(order.getPlanName());
        vo.setAmount(order.getAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderVO.getStatusText(order.getStatus()));
        vo.setPayTime(order.getPayTime());
        vo.setExpireTime(order.getExpireTime());
        vo.setCreateTime(order.getCreateTime());
        return vo;
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
