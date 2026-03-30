package com.qoder.backend.controller;

import com.qoder.backend.dto.PlanVO;
import com.qoder.backend.dto.Result;
import com.qoder.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {
    
    private final PlanService planService;
    
    /**
     * 获取套餐列表
     */
    @GetMapping("/list")
    public Result<List<PlanVO>> getPlanList() {
        List<PlanVO> plans = planService.getAvailablePlans();
        return Result.success(plans);
    }
    
    /**
     * 获取套餐详情
     */
    @GetMapping("/{id}")
    public Result<PlanVO> getPlanDetail(@PathVariable Long id) {
        PlanVO plan = planService.getPlanDetail(id);
        if (plan == null) {
            return Result.error("套餐不存在");
        }
        return Result.success(plan);
    }
}
