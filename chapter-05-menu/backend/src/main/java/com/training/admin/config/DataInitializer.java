package com.training.admin.config;

import com.training.admin.entity.Menu;
import com.training.admin.entity.User;
import com.training.admin.repository.MenuRepository;
import com.training.admin.repository.UserRepository;
import com.training.admin.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器（第五章：菜单管理）
 * 在应用启动时初始化默认用户和菜单树数据
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
    private final MenuRepository menuRepository;
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
        
        // 2. 创建菜单树（第五章核心内容）
        // 一级菜单：系统管理
        Menu systemMenu = createMenu(null, "MENU_SYSTEM", "系统管理", "/system", "Layout", "Setting", 1);
        
        // 二级菜单
        createMenu(systemMenu.getId(), "MENU_USER", "用户管理", "/users", "UserPage", "User", 10);
        createMenu(systemMenu.getId(), "MENU_ROLE", "角色管理", "/roles", "RolePage", "Avatar", 20);
        createMenu(systemMenu.getId(), "MENU_PERMISSION", "权限管理", "/permissions", "PermissionPage", "Lock", 30);
        createMenu(systemMenu.getId(), "MENU_MENU", "菜单管理", "/menus", "MenuPage", "Menu", 40);
        
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
     * 创建菜单
     */
    private Menu createMenu(Long parentId, String code, String name, String path, 
                           String component, String icon, Integer sort) {
        Menu menu = new Menu();
        menu.setParentId(parentId);
        menu.setCode(code);
        menu.setName(name);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setIcon(icon);
        menu.setSort(sort);
        menu.setStatus(1);
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        
        Menu saved = menuRepository.save(menu);
        log.debug("创建菜单: code={}, name={}", code, name);
        return saved;
    }
}

