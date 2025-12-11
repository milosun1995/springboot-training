package com.training.admin.service;

import com.training.admin.dto.LoginDTO;
import com.training.admin.entity.User;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.UserRepository;
import com.training.admin.util.JwtUtil;
import com.training.admin.util.PasswordUtil;
import com.training.admin.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        
        // 验证密码
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }
        
        // 生成 Token
        String token = jwtUtil.generateToken(user.getUsername());
        
        // 返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        
        return loginVO;
    }
}

