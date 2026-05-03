package com.qoder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoder.backend.entity.Plan;
import com.qoder.backend.mapper.PlanMapper;
import com.qoder.backend.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
    
    @Override
    public List<Plan> getAvailablePlans() {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Plan::getStatus, 1)
               .orderByAsc(Plan::getSort);
        return list(wrapper);
    }
}
