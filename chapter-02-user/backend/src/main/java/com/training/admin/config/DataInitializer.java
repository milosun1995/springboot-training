package com.training.admin.config;

import com.training.admin.entity.User;
import com.training.admin.repository.UserRepository;
import com.training.admin.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 在应用启动时初始化默认用户数据
 * 
 * 优势：
 * 1. 使用 BCryptPasswordEncoder 动态生成密码哈希，确保准确性
 * 2. 检查用户是否已存在，避免重复创建
 * 3. 更安全，密码哈希在运行时生成，不会硬编码在 SQL 中
 * 4. 更灵活，可以轻松修改默认数据配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    
    // 默认密码（所有用户）
    private static final String DEFAULT_PASSWORD = "password123";
    
    @Override
    public void run(String... args) {
        initDefaultUsers();
    }
    
    /**
     * 初始化默认用户
     */
    private void initDefaultUsers() {
        // 检查是否已有用户数据
        if (userRepository.count() > 0) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }
        
        // 创建默认用户
        createUser("admin", "系统管理员", "admin@example.com", 1);
        createUser("jerry", "杰瑞", "jerry@example.com", 1);
        createUser("tom", "汤姆", "tom@example.com", 0);
        createUser("alice", "爱丽丝", "alice@example.com", 1);
        
        log.info("默认用户数据初始化成功");
        log.info("默认密码: {}", DEFAULT_PASSWORD);
        log.info("⚠️  请在生产环境中修改默认密码！");
    }
    
    /**
     * 创建用户
     */
    private void createUser(String username, String nickname, String email, Integer status) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.encode(DEFAULT_PASSWORD));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setStatus(status);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userRepository.save(user);
        log.debug("创建用户: username={}, status={}", username, status);
    }
}

