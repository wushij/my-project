package com.qoder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoder.backend.dto.PlanVO;
import com.qoder.backend.entity.Plan;
import com.qoder.backend.mapper.PlanMapper;
import com.qoder.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public List<PlanVO> getAvailablePlans() {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Plan::getStatus, 1)
               .orderByAsc(Plan::getSort);
        
        List<Plan> plans = list(wrapper);
        return plans.stream().map(this::convertToVO).toList();
    }
    
    @Override
    public PlanVO getPlanDetail(Long id) {
        Plan plan = getById(id);
        if (plan == null || plan.getStatus() != 1) {
            return null;
        }
        return convertToVO(plan);
    }
    
    private PlanVO convertToVO(Plan plan) {
        PlanVO vo = new PlanVO();
        vo.setId(plan.getId());
        vo.setName(plan.getName());
        vo.setCode(plan.getCode());
        vo.setPrice(plan.getPrice());
        vo.setOriginalPrice(plan.getOriginalPrice());
        vo.setCredits(plan.getCredits());
        vo.setDuration(plan.getDuration());
        vo.setSort(plan.getSort());
        
        // 解析features JSON
        if (plan.getFeatures() != null) {
            try {
                List<String> features = objectMapper.readValue(
                    plan.getFeatures(), 
                    new TypeReference<List<String>>() {}
                );
                vo.setFeatures(features);
            } catch (Exception e) {
                log.error("解析套餐特性JSON失败", e);
                vo.setFeatures(new ArrayList<>());
            }
        }
        
        return vo;
    }
}
