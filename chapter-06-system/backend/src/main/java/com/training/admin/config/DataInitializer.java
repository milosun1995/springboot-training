package com.training.admin.config;

import com.training.admin.entity.Dict;
import com.training.admin.entity.User;
import com.training.admin.repository.DictRepository;
import com.training.admin.repository.UserRepository;
import com.training.admin.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器（第六章：系统优化 - 字典管理）
 * 在应用启动时初始化默认用户和字典数据
 * 
 * 优势：
 * 1. 使用 BCryptPasswordEncoder 动态生成密码哈希，确保准确性
 * 2. 检查数据是否已存在，避免重复创建
 * 3. 更安全，密码哈希在运行时生成，不会硬编码在 SQL 中
 * 4. 更灵活，可以轻松修改默认数据配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final DictRepository dictRepository;
    private final PasswordUtil passwordUtil;
    
    // 默认密码（所有用户）
    private static final String DEFAULT_PASSWORD = "password123";
    
    @Override
    public void run(String... args) {
        initDefaultData();
    }
    
    /**
     * 初始化默认数据
     */
    private void initDefaultData() {
        // 检查是否已有用户数据
        if (userRepository.count() > 0) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }
        
        log.info("开始初始化默认数据...");
        
        // 1. 创建用户
        createUser("admin", "系统管理员", "admin@example.com", 1);
        createUser("jerry", "杰瑞", "jerry@example.com", 1);
        createUser("tom", "汤姆", "tom@example.com", 0);
        createUser("alice", "爱丽丝", "alice@example.com", 1);
        
        // 2. 创建字典数据（第六章核心内容）
        // 性别字典
        createDict("GENDER_MALE", "男", "male", "gender", 1, "性别-男");
        createDict("GENDER_FEMALE", "女", "female", "gender", 2, "性别-女");
        
        // 状态字典
        createDict("STATUS_ENABLED", "启用", "1", "status", 1, "状态-启用");
        createDict("STATUS_DISABLED", "禁用", "0", "status", 2, "状态-禁用");
        
        // 用户类型字典
        createDict("USER_TYPE_ADMIN", "管理员", "admin", "user_type", 1, "用户类型-管理员");
        createDict("USER_TYPE_NORMAL", "普通用户", "normal", "user_type", 2, "用户类型-普通用户");
        createDict("USER_TYPE_GUEST", "访客", "guest", "user_type", 3, "用户类型-访客");
        
        log.info("默认数据初始化成功");
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
    
    /**
     * 创建字典
     */
    private void createDict(String code, String label, String value, String type, Integer sort, String remark) {
        Dict dict = new Dict();
        dict.setCode(code);
        dict.setLabel(label);
        dict.setValue(value);
        dict.setType(type);
        dict.setSort(sort);
        dict.setStatus(1);
        dict.setRemark(remark);
        dict.setCreateTime(LocalDateTime.now());
        dict.setUpdateTime(LocalDateTime.now());
        
        dictRepository.save(dict);
        log.debug("创建字典: code={}, label={}, type={}", code, label, type);
    }
}

