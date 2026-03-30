package com.qoder.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlanVO {
    
    private Long id;
    
    private String name;
    
    private String code;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer credits;
    
    private Integer duration;
    
    private List<String> features;
    
    private Integer sort;
}
