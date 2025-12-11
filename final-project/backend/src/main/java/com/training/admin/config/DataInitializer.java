package com.training.admin.config;

import com.training.admin.entity.*;
import com.training.admin.repository.*;
import com.training.admin.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据初始化器（Final Project - 完整版）
 * 初始化：用户、角色、权限（menu/button/api）、菜单、字典及关联关系
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final DictRepository dictRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PasswordUtil passwordUtil;
    
    private static final String DEFAULT_PASSWORD = "password123";
    
    @Override
    public void run(String... args) {
        initDefaultData();
    }
    
    private void initDefaultData() {
        if (userRepository.count() > 0) {
            log.info("数据已存在，跳过初始化");
            return;
        }
        
        log.info("开始初始化默认数据...");
        
        // 1. 创建用户
        User admin = createUser("admin", "系统管理员", "admin@example.com");
        User manager = createUser("manager", "经理", "manager@example.com");
        User jerry = createUser("jerry", "杰瑞", "jerry@example.com");
        User tom = createUser("tom", "汤姆", "tom@example.com");
        
        // 2. 创建角色
        Role adminRole = createRole("ADMIN", "管理员", "拥有所有权限");
        Role opsRole = createRole("OPS", "运维人员", "拥有部分权限");
        Role guestRole = createRole("GUEST", "访客", "只读权限");
        
        // 3. 创建权限树（menu + button + api）
        List<Permission> permissions = createPermissions();
        
        // 4. 创建菜单树
        List<Menu> menus = createMenus();
        
        // 5. 创建字典
        createDicts();
        
        // 6. 分配用户-角色
        createUserRole(admin.getId(), adminRole.getId());
        createUserRole(manager.getId(), adminRole.getId());
        createUserRole(jerry.getId(), opsRole.getId());
        createUserRole(tom.getId(), guestRole.getId());
        
        // 7. 分配角色-权限（管理员全部，运维部分，访客只读）
        assignRolePermissions(adminRole, opsRole, guestRole, permissions);
        
        // 8. 分配角色-菜单
        assignRoleMenus(adminRole, opsRole, guestRole, menus);
        
        log.info("默认数据初始化成功");
        log.info("默认密码: {}", DEFAULT_PASSWORD);
        log.info("⚠️  请在生产环境中修改默认密码！");
    }
    
    private User createUser(String username, String nickname, String email) {
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(passwordUtil.encode(DEFAULT_PASSWORD));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    private Role createRole(String code, String name, String description) {
        Role role = new Role();
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        return roleRepository.save(role);
    }
    
    private List<Permission> createPermissions() {
        // 系统管理（父节点）
        Permission sys = savePerm(null, "sys:manage", "系统管理", "menu", null, null, 1);
        
        // ========== 用户管理 ==========
        Permission userMenu = savePerm(sys.getId(), "sys:user", "用户管理", "menu", "/users", null, 10);
        Permission userView = savePerm(userMenu.getId(), "sys:user:view", "查看用户", "button", null, null, 11);
        Permission userCreate = savePerm(userMenu.getId(), "sys:user:create", "新增用户", "button", null, null, 12);
        Permission userUpdate = savePerm(userMenu.getId(), "sys:user:update", "编辑用户", "button", null, null, 13);
        Permission userDelete = savePerm(userMenu.getId(), "sys:user:delete", "删除用户", "button", null, null, 14);
        Permission userToggle = savePerm(userMenu.getId(), "sys:user:toggle", "启用/禁用", "button", null, null, 15);
        
        // API 权限
        Permission userList = savePerm(userMenu.getId(), "sys:user:list", "用户列表", "api", "/api/users", "GET", 16);
        Permission userAdd = savePerm(userMenu.getId(), "sys:user:add", "添加用户", "api", "/api/users", "POST", 17);
        Permission userEdit = savePerm(userMenu.getId(), "sys:user:edit", "修改用户", "api", "/api/users/*", "PUT", 18);
        Permission userDel = savePerm(userMenu.getId(), "sys:user:del", "删除用户", "api", "/api/users/*", "DELETE", 19);
        Permission userStatus = savePerm(userMenu.getId(), "sys:user:status", "用户状态", "api", "/api/users/*/toggle", "PUT", 20);
        
        // ========== 角色管理 ==========
        Permission roleMenu = savePerm(sys.getId(), "sys:role", "角色管理", "menu", "/roles", null, 30);
        Permission roleView = savePerm(roleMenu.getId(), "sys:role:view", "查看角色", "button", null, null, 31);
        Permission roleCreate = savePerm(roleMenu.getId(), "sys:role:create", "新增角色", "button", null, null, 32);
        Permission roleUpdate = savePerm(roleMenu.getId(), "sys:role:update", "编辑角色", "button", null, null, 33);
        Permission roleDelete = savePerm(roleMenu.getId(), "sys:role:delete", "删除角色", "button", null, null, 34);
        Permission roleToggle = savePerm(roleMenu.getId(), "sys:role:toggle", "启用/禁用", "button", null, null, 35);
        Permission roleAssign = savePerm(roleMenu.getId(), "sys:role:assign", "分配权限", "button", null, null, 36);
        
        Permission roleList = savePerm(roleMenu.getId(), "sys:role:list", "角色列表", "api", "/api/roles", "GET", 37);
        Permission roleAdd = savePerm(roleMenu.getId(), "sys:role:add", "添加角色", "api", "/api/roles", "POST", 38);
        Permission roleEdit = savePerm(roleMenu.getId(), "sys:role:edit", "修改角色", "api", "/api/roles/*", "PUT", 39);
        Permission roleDel = savePerm(roleMenu.getId(), "sys:role:del", "删除角色", "api", "/api/roles/*", "DELETE", 40);
        Permission roleStatus = savePerm(roleMenu.getId(), "sys:role:status", "角色状态", "api", "/api/roles/*/toggle", "PUT", 41);
        Permission rolePerm = savePerm(roleMenu.getId(), "sys:role:perm", "角色权限", "api", "/api/roles/*/permissions", "PUT", 42);
        Permission roleMenuApi = savePerm(roleMenu.getId(), "sys:role:menu", "角色菜单", "api", "/api/roles/*/menus", "PUT", 43);
        
        // ========== 权限管理 ==========
        Permission permMenu = savePerm(sys.getId(), "sys:permission", "权限管理", "menu", "/permissions", null, 50);
        Permission permView = savePerm(permMenu.getId(), "sys:permission:view", "查看权限", "button", null, null, 51);
        Permission permCreate = savePerm(permMenu.getId(), "sys:permission:create", "新增权限", "button", null, null, 52);
        Permission permUpdate = savePerm(permMenu.getId(), "sys:permission:update", "编辑权限", "button", null, null, 53);
        Permission permDelete = savePerm(permMenu.getId(), "sys:permission:delete", "删除权限", "button", null, null, 54);
        Permission permToggle = savePerm(permMenu.getId(), "sys:permission:toggle", "启用/禁用", "button", null, null, 55);
        
        Permission permList = savePerm(permMenu.getId(), "sys:permission:list", "权限列表", "api", "/api/permissions", "GET", 56);
        Permission permAdd = savePerm(permMenu.getId(), "sys:permission:add", "添加权限", "api", "/api/permissions", "POST", 57);
        Permission permEdit = savePerm(permMenu.getId(), "sys:permission:edit", "修改权限", "api", "/api/permissions/*", "PUT", 58);
        Permission permDel = savePerm(permMenu.getId(), "sys:permission:del", "删除权限", "api", "/api/permissions/*", "DELETE", 59);
        Permission permStatus = savePerm(permMenu.getId(), "sys:permission:status", "权限状态", "api", "/api/permissions/*/toggle", "PUT", 60);
        
        // ========== 菜单管理 ==========
        Permission menuMenu = savePerm(sys.getId(), "sys:menu", "菜单管理", "menu", "/menus", null, 70);
        Permission menuView = savePerm(menuMenu.getId(), "sys:menu:view", "查看菜单", "button", null, null, 71);
        Permission menuCreate = savePerm(menuMenu.getId(), "sys:menu:create", "新增菜单", "button", null, null, 72);
        Permission menuUpdate = savePerm(menuMenu.getId(), "sys:menu:update", "编辑菜单", "button", null, null, 73);
        Permission menuDelete = savePerm(menuMenu.getId(), "sys:menu:delete", "删除菜单", "button", null, null, 74);
        Permission menuToggle = savePerm(menuMenu.getId(), "sys:menu:toggle", "启用/禁用", "button", null, null, 75);
        
        Permission menuList = savePerm(menuMenu.getId(), "sys:menu:list", "菜单列表", "api", "/api/menus", "GET", 76);
        Permission menuAdd = savePerm(menuMenu.getId(), "sys:menu:add", "添加菜单", "api", "/api/menus", "POST", 77);
        Permission menuEdit = savePerm(menuMenu.getId(), "sys:menu:edit", "修改菜单", "api", "/api/menus/*", "PUT", 78);
        Permission menuDel = savePerm(menuMenu.getId(), "sys:menu:del", "删除菜单", "api", "/api/menus/*", "DELETE", 79);
        Permission menuStatus = savePerm(menuMenu.getId(), "sys:menu:status", "菜单状态", "api", "/api/menus/*/toggle", "PUT", 80);
        
        // ========== 字典管理 ==========
        Permission dictMenu = savePerm(sys.getId(), "sys:dict", "字典管理", "menu", "/dicts", null, 90);
        Permission dictView = savePerm(dictMenu.getId(), "sys:dict:view", "查看字典", "button", null, null, 91);
        Permission dictCreate = savePerm(dictMenu.getId(), "sys:dict:create", "新增字典", "button", null, null, 92);
        Permission dictUpdate = savePerm(dictMenu.getId(), "sys:dict:update", "编辑字典", "button", null, null, 93);
        Permission dictDelete = savePerm(dictMenu.getId(), "sys:dict:delete", "删除字典", "button", null, null, 94);
        Permission dictToggle = savePerm(dictMenu.getId(), "sys:dict:toggle", "启用/禁用", "button", null, null, 95);
        
        Permission dictList = savePerm(dictMenu.getId(), "sys:dict:list", "字典列表", "api", "/api/dicts", "GET", 96);
        Permission dictAdd = savePerm(dictMenu.getId(), "sys:dict:add", "添加字典", "api", "/api/dicts", "POST", 97);
        Permission dictEdit = savePerm(dictMenu.getId(), "sys:dict:edit", "修改字典", "api", "/api/dicts/*", "PUT", 98);
        Permission dictDel = savePerm(dictMenu.getId(), "sys:dict:del", "删除字典", "api", "/api/dicts/*", "DELETE", 99);
        Permission dictStatus = savePerm(dictMenu.getId(), "sys:dict:status", "字典状态", "api", "/api/dicts/*/toggle", "PUT", 100);
        
        return permissionRepository.findAll();
    }
    
    private Permission savePerm(Long parentId, String code, String name, String type, String path, String method, int sort) {
        Permission p = new Permission();
        p.setParentId(parentId);
        p.setCode(code);
        p.setName(name);
        p.setType(type);
        p.setPath(path);
        p.setMethod(method);
        p.setSort(sort);
        p.setStatus(1);
        p.setCreateTime(LocalDateTime.now());
        p.setUpdateTime(LocalDateTime.now());
        return permissionRepository.save(p);
    }
    
    private List<Menu> createMenus() {
        Menu sys = saveMenu(null, "系统管理", "system", null, null, "Setting", 1);
        Menu user = saveMenu(sys.getId(), "用户管理", "user", "/users", "UserList", "User", 2);
        Menu role = saveMenu(sys.getId(), "角色管理", "role", "/roles", "RoleList", "UserFilled", 3);
        Menu perm = saveMenu(sys.getId(), "权限管理", "permission", "/permissions", "PermissionList", "Key", 4);
        Menu menu = saveMenu(sys.getId(), "菜单管理", "menu", "/menus", "MenuList", "Menu", 5);
        Menu dict = saveMenu(sys.getId(), "字典管理", "dict", "/dicts", "DictList", "Document", 6);
        return menuRepository.findAll();
    }
    
    private Menu saveMenu(Long parentId, String name, String code, String path, String component, String icon, int sort) {
        Menu m = new Menu();
        m.setParentId(parentId);
        m.setName(name);
        m.setCode(code);
        m.setPath(path);
        m.setComponent(component);
        m.setIcon(icon);
        m.setSort(sort);
        m.setStatus(1);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        return menuRepository.save(m);
    }
    
    private void createDicts() {
        saveDict("gender_male", "男", "male", "gender", 1, "性别-男");
        saveDict("gender_female", "女", "female", "gender", 2, "性别-女");
        saveDict("status_enable", "启用", "1", "status", 1, "状态-启用");
        saveDict("status_disable", "禁用", "0", "status", 2, "状态-禁用");
        saveDict("user_type_admin", "管理员", "admin", "user_type", 1, "用户类型-管理员");
        saveDict("user_type_normal", "普通用户", "normal", "user_type", 2, "用户类型-普通用户");
    }
    
    private void saveDict(String code, String label, String value, String type, int sort, String remark) {
        Dict d = new Dict();
        d.setCode(code);
        d.setLabel(label);
        d.setValue(value);
        d.setType(type);
        d.setSort(sort);
        d.setStatus(1);
        d.setRemark(remark);
        d.setCreateTime(LocalDateTime.now());
        d.setUpdateTime(LocalDateTime.now());
        dictRepository.save(d);
    }
    
    private void createUserRole(Long userId, Long roleId) {
        UserRole ur = new UserRole();
        ur.setUserId(userId);
        ur.setRoleId(roleId);
        userRoleRepository.save(ur);
    }
    
    private void assignRolePermissions(Role admin, Role ops, Role guest, List<Permission> permissions) {
        // 管理员拥有所有权限
        for (Permission p : permissions) {
            createRolePerm(admin.getId(), p.getId());
        }
        
        // 运维人员：用户、角色的查看和部分操作
        for (Permission p : permissions) {
            String code = p.getCode();
            if (code.startsWith("sys:user:") || code.startsWith("sys:role:")) {
                if (!code.contains("delete") && !code.contains("del")) {
                    createRolePerm(ops.getId(), p.getId());
                }
            }
        }
        
        // 访客：只有查看权限
        for (Permission p : permissions) {
            String code = p.getCode();
            if (code.contains("view") || code.contains("list") || code.equals("sys:manage")) {
                createRolePerm(guest.getId(), p.getId());
            }
        }
    }
    
    private void createRolePerm(Long roleId, Long permId) {
        RolePermission rp = new RolePermission();
        rp.setRoleId(roleId);
        rp.setPermissionId(permId);
        rolePermissionRepository.save(rp);
    }
    
    private void assignRoleMenus(Role admin, Role ops, Role guest, List<Menu> menus) {
        // 管理员拥有所有菜单
        for (Menu m : menus) {
            createRoleMenu(admin.getId(), m.getId());
        }
        
        // 运维人员：用户和角色管理菜单
        for (Menu m : menus) {
            String code = m.getCode();
            if (code == null || code.equals("system") || code.equals("user") || code.equals("role")) {
                createRoleMenu(ops.getId(), m.getId());
            }
        }
        
        // 访客：只有用户管理菜单（只读）
        for (Menu m : menus) {
            String code = m.getCode();
            if (code == null || code.equals("system") || code.equals("user")) {
                createRoleMenu(guest.getId(), m.getId());
            }
        }
    }
    
    private void createRoleMenu(Long roleId, Long menuId) {
        RoleMenu rm = new RoleMenu();
        rm.setRoleId(roleId);
        rm.setMenuId(menuId);
        roleMenuRepository.save(rm);
    }
}
