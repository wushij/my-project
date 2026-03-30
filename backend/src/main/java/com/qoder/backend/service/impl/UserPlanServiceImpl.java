package com.qoder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoder.backend.entity.UserPlan;
import com.qoder.backend.mapper.UserPlanMapper;
import com.qoder.backend.service.UserPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserPlanServiceImpl extends ServiceImpl<UserPlanMapper, UserPlan> implements UserPlanService {
    
    @Override
    public boolean createUserPlan(Long userId, Long planId, String planName, Integer credits, Integer duration, String orderNo) {
        UserPlan userPlan = new UserPlan();
        userPlan.setUserId(userId);
        userPlan.setPlanId(planId);
        userPlan.setPlanName(planName);
        userPlan.setCredits(credits);
        userPlan.setUsedCredits(0);
        userPlan.setStartTime(LocalDateTime.now());
        userPlan.setEndTime(LocalDateTime.now().plusDays(duration));
        userPlan.setOrderNo(orderNo);
        userPlan.setStatus(1);
        
        boolean saved = save(userPlan);
        if (saved) {
            log.info("用户套餐创建成功: userId={}, planName={}, orderNo={}", userId, planName, orderNo);
        }
        return saved;
    }
}
