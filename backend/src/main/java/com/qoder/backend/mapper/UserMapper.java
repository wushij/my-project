package com.qoder.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoder.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
