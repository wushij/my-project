package com.qoder.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoder.backend.entity.User;

public interface UserService extends IService<User> {
    
    User findByEmail(String email);
    
    boolean register(String email, String password, String nickname);
}
