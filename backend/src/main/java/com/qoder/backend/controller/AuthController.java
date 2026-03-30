package com.qoder.backend.controller;

import com.qoder.backend.dto.*;
import com.qoder.backend.entity.User;
import com.qoder.backend.service.UserService;
import com.qoder.backend.util.JwtUtils;
import com.qoder.backend.util.PasswordUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtils jwtUtils;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 查找用户
        User user = userService.findByEmail(request.getEmail());
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        
        // 验证密码
        if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            return Result.error(401, "密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            return Result.error(403, "账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        
        // 构建响应
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setEmail(user.getEmail());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        
        return Result.success(new LoginResponse(token, userVO));
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        boolean success = userService.register(
            request.getEmail(), 
            request.getPassword(), 
            request.getNickname()
        );
        
        if (!success) {
            return Result.error("该邮箱已被注册");
        }
        
        return Result.success();
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public Result<UserVO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return Result.error(401, "Token已过期");
        }
        
        Long userId = jwtUtils.getUserIdFromToken(token);
        User user = userService.getById(userId);
        
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setEmail(user.getEmail());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        
        return Result.success(userVO);
    }
    
    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT是无状态的，登出只需要前端删除Token即可
        return Result.success();
    }
}
