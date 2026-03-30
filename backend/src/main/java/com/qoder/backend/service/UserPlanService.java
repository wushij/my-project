package com.qoder.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoder.backend.entity.UserPlan;

public interface UserPlanService extends IService<UserPlan> {
    
    /**
     * 创建用户套餐（支付成功后调用）
     */
    boolean createUserPlan(Long userId, Long planId, String planName, Integer credits, Integer duration, String orderNo);
}
