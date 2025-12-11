package com.training.admin.config;

import com.training.admin.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Security Filter - 解析 JWT 并设置 Spring Security 上下文
 * 
 * 功能：
 * 1. 从请求头提取 JWT Token
 * 2. 解析 Token 获取用户名和权限列表
 * 3. 创建 Authentication 对象
 * 4. 设置到 SecurityContextHolder（供 @PreAuthorize 使用）
 */
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            
            try {
                // 验证 Token 有效性
                if (jwtUtil.validateToken(token)) {
                    // 提取用户名和权限
                    String username = jwtUtil.getUsernameFromToken(token);
                    List<String> permissions = jwtUtil.getPermissionsFromToken(token);
                    
                    if (username != null) {
                        // 将权限字符串转换为 Spring Security 的 GrantedAuthority
                        List<SimpleGrantedAuthority> authorities = permissions.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        
                        // 创建 Authentication 对象
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        
                        // 设置到 SecurityContextHolder（关键步骤！）
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // 兼容旧代码：继续设置 request attribute
                        request.setAttribute("currentUsername", username);
                    }
                }
            } catch (Exception e) {
                // Token 解析失败，继续执行（不设置认证信息）
                // Spring Security 会判断为未认证用户
            }
        }
        
        filterChain.doFilter(request, response);
    }
}


