package com.qoder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoder.backend.entity.User;
import com.qoder.backend.mapper.UserMapper;
import com.qoder.backend.service.UserService;
import com.qoder.backend.util.PasswordUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return getOne(wrapper);
    }
    
    @Override
    public boolean register(String email, String password, String nickname) {
        // 检查邮箱是否已存在
        if (findByEmail(email) != null) {
            return false;
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(PasswordUtils.encode(password));
        user.setNickname(nickname != null ? nickname : email.split("@")[0]);
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + email);
        user.setStatus(1);
        
        return save(user);
    }
}
