package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.LoginDTO;
import com.training.admin.service.AuthService;
import com.training.admin.vo.LoginVO;
import com.training.admin.vo.ProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import com.training.admin.exception.BusinessException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @GetMapping("/profile")
    public Result<ProfileVO> profile(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        if (username == null) {
            throw new BusinessException(401, "未登录");
        }
        ProfileVO profile = authService.profile(username);
        return Result.success(profile);
    }
}

