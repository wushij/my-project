package com.qoder.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoder.backend.entity.Plan;

import java.util.List;

public interface PlanService extends IService<Plan> {
    
    /**
     * 获取可用套餐列表
     */
    List<Plan> getAvailablePlans();
}
