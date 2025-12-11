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
 * 在应用启动时初始化默认管理员用户
 * 
 * 优势：
 * 1. 使用 BCryptPasswordEncoder 动态生成密码哈希，确保准确性
 * 2. 检查用户是否已存在，避免重复创建
 * 3. 更安全，密码哈希在运行时生成，不会硬编码在 SQL 中
 * 4. 更灵活，可以轻松修改默认密码配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    
    // 默认管理员账号配置
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_ADMIN_NICKNAME = "系统管理员";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@example.com";
    
    @Override
    public void run(String... args) {
        initDefaultAdmin();
    }
    
    /**
     * 初始化默认管理员用户
     */
    private void initDefaultAdmin() {
        // 检查管理员用户是否已存在
        if (userRepository.findByUsername(DEFAULT_ADMIN_USERNAME).isPresent()) {
            log.info("默认管理员用户已存在，跳过初始化");
            return;
        }
        
        // 创建默认管理员用户
        User admin = new User();
        admin.setUsername(DEFAULT_ADMIN_USERNAME);
        // 使用 BCryptPasswordEncoder 动态生成密码哈希，确保准确性
        admin.setPassword(passwordUtil.encode(DEFAULT_ADMIN_PASSWORD));
        admin.setNickname(DEFAULT_ADMIN_NICKNAME);
        admin.setEmail(DEFAULT_ADMIN_EMAIL);
        admin.setStatus(1); // 启用状态
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        
        userRepository.save(admin);
        
        log.info("默认管理员用户初始化成功");
        log.info("用户名: {}", DEFAULT_ADMIN_USERNAME);
        log.info("密码: {}", DEFAULT_ADMIN_PASSWORD);
        log.info("⚠️  请在生产环境中修改默认密码！");
    }
}

