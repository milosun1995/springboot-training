package com.training.admin.config;

import com.training.admin.entity.Permission;
import com.training.admin.entity.User;
import com.training.admin.repository.PermissionRepository;
import com.training.admin.repository.UserRepository;
import com.training.admin.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器（第四章：权限管理）
 * 在应用启动时初始化默认用户和权限树数据
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
    private final PermissionRepository permissionRepository;
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
        
        // 2. 创建权限树（第四章核心内容）
        // 一级权限：系统管理
        Permission sysManage = createPermission(null, "MENU_SYSTEM", "系统管理", "menu", "/system", null, 1);
        
        // 二级权限：用户管理
        Permission userManage = createPermission(sysManage.getId(), "MENU_USER", "用户管理", "menu", "/users", null, 10);
        createPermission(userManage.getId(), "BTN_USER_QUERY", "用户查询", "button", "/api/users", "GET", 1);
        createPermission(userManage.getId(), "BTN_USER_ADD", "用户新增", "button", "/api/users", "POST", 2);
        createPermission(userManage.getId(), "BTN_USER_EDIT", "用户编辑", "button", "/api/users/{id}", "PUT", 3);
        createPermission(userManage.getId(), "BTN_USER_DELETE", "用户删除", "button", "/api/users/{id}", "DELETE", 4);
        
        // 二级权限：角色管理
        Permission roleManage = createPermission(sysManage.getId(), "MENU_ROLE", "角色管理", "menu", "/roles", null, 20);
        createPermission(roleManage.getId(), "BTN_ROLE_QUERY", "角色查询", "button", "/api/roles", "GET", 1);
        createPermission(roleManage.getId(), "BTN_ROLE_ADD", "角色新增", "button", "/api/roles", "POST", 2);
        createPermission(roleManage.getId(), "BTN_ROLE_EDIT", "角色编辑", "button", "/api/roles/{id}", "PUT", 3);
        createPermission(roleManage.getId(), "BTN_ROLE_DELETE", "角色删除", "button", "/api/roles/{id}", "DELETE", 4);
        
        // 二级权限：权限管理
        Permission permManage = createPermission(sysManage.getId(), "MENU_PERMISSION", "权限管理", "menu", "/permissions", null, 30);
        createPermission(permManage.getId(), "BTN_PERM_QUERY", "权限查询", "button", "/api/permissions/tree", "GET", 1);
        createPermission(permManage.getId(), "BTN_PERM_ADD", "权限新增", "button", "/api/permissions", "POST", 2);
        createPermission(permManage.getId(), "BTN_PERM_EDIT", "权限编辑", "button", "/api/permissions/{id}", "PUT", 3);
        createPermission(permManage.getId(), "BTN_PERM_DELETE", "权限删除", "button", "/api/permissions/{id}", "DELETE", 4);
        
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
     * 创建权限
     */
    private Permission createPermission(Long parentId, String code, String name, String type, 
                                       String path, String method, Integer sort) {
        Permission permission = new Permission();
        permission.setParentId(parentId);
        permission.setCode(code);
        permission.setName(name);
        permission.setType(type);
        permission.setPath(path);
        permission.setMethod(method);
        permission.setSort(sort);
        permission.setStatus(1);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        
        Permission saved = permissionRepository.save(permission);
        log.debug("创建权限: code={}, name={}, type={}", code, name, type);
        return saved;
    }
}
