package com.qoder.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoder.backend.dto.PlanVO;
import com.qoder.backend.entity.Plan;

import java.util.List;

public interface PlanService extends IService<Plan> {
    
    /**
     * 获取上架的套餐列表
     */
    List<PlanVO> getAvailablePlans();
    
    /**
     * 获取套餐详情
     */
    PlanVO getPlanDetail(Long id);
}
