package com.training.admin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 
 * 功能：
 * 1. 启用方法级安全验证（@PreAuthorize、@PostAuthorize 等）
 * 2. 配置 JWT 认证过滤器
 * 3. 禁用 Session（使用 JWT 无状态认证）
 * 4. 配置接口访问权限
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // ⭐ 关键：启用方法级安全验证（@PreAuthorize）
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 不需要）
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 允许登录接口无需认证
                        .requestMatchers("/api/auth/login").permitAll()
                        // 允许错误页面访问
                        .requestMatchers("/error").permitAll()
                        // 其他所有接口都需要认证
                        .anyRequest().authenticated()
                )
                
                // 禁用 Session（使用 JWT 无状态认证）
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 添加自定义 JWT 过滤器（在 UsernamePasswordAuthenticationFilter 之前执行）
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

