# Final Project - 完整的 RBAC 权限管理系统

## 📖 项目说明

这是整合了前六章所有功能的**完整企业级 RBAC（基于角色的访问控制）系统**。本项目实现了从用户登录到权限控制的完整流程，是一个可以直接用于生产环境的后台管理系统基础框架。

**🎯 本项目整合了以下功能**：
1. **用户管理** - 用户的增删改查、状态管理
2. **角色管理** - 角色的增删改查、权限分配
3. **权限管理** - 三层权限控制（menu/button/api）
4. **菜单管理** - 动态菜单、路由生成
5. **字典管理** - 系统配置数据管理
6. **完整的 RBAC** - 用户-角色-权限-菜单完整关联

## 🎯 学习目标

通过学习本项目，你将掌握：

### 系统架构
- ✅ 完整的 RBAC 权限模型设计
- ✅ 前后端分离架构
- ✅ RESTful API 设计规范
- ✅ 分层架构（Controller-Service-Repository）

### 后端技术栈
- ✅ Spring Boot 3.x
- ✅ Spring Data JPA（数据持久化）
- ✅ Spring Security（安全框架）
- ✅ JWT（Token 认证）
- ✅ H2 Database（开发环境）
- ✅ Maven（项目管理）
- ✅ Lombok（代码简化）
- ✅ Bean Validation（参数校验）

### 前端技术栈
- ✅ Vue 3（渐进式框架）
- ✅ Vue Router（路由管理）
- ✅ Pinia（状态管理）
- ✅ Element Plus（UI 组件库）
- ✅ Axios（HTTP 客户端）
- ✅ Vite（构建工具）

### 权限控制
- ✅ 三层权限模型（menu/button/api）
- ✅ 前端路由守卫（动态路由）
- ✅ 前端按钮权限（v-perm 指令）
- ✅ 后端 API 权限（@PreAuthorize）
- ✅ 基于角色的菜单过滤

## 📁 项目结构

```
final-project/
├── backend/                          # 后端项目
│   ├── src/main/java/com/training/admin/
│   │   ├── config/                   # 配置类
│   │   │   ├── CorsConfig.java       # 跨域配置
│   │   │   ├── SecurityConfig.java   # Spring Security 配置
│   │   │   ├── SecurityFilter.java   # JWT 认证过滤器
│   │   │   └── DataInitializer.java  # 数据初始化
│   │   ├── controller/               # 控制器层
│   │   │   ├── AuthController.java   # 认证接口
│   │   │   ├── UserController.java   # 用户管理
│   │   │   ├── RoleController.java   # 角色管理
│   │   │   ├── PermissionController.java  # 权限管理
│   │   │   ├── MenuController.java   # 菜单管理
│   │   │   └── DictController.java   # 字典管理
│   │   ├── service/                  # 业务层
│   │   │   ├── AuthService.java      # 认证服务
│   │   │   ├── UserService.java      # 用户服务
│   │   │   ├── RoleService.java      # 角色服务
│   │   │   ├── PermissionService.java  # 权限服务
│   │   │   ├── MenuService.java      # 菜单服务
│   │   │   └── DictService.java      # 字典服务
│   │   ├── repository/               # 数据访问层
│   │   │   ├── UserRepository.java
│   │   │   ├── RoleRepository.java
│   │   │   ├── PermissionRepository.java
│   │   │   ├── MenuRepository.java
│   │   │   ├── DictRepository.java
│   │   │   ├── UserRoleRepository.java
│   │   │   ├── RolePermissionRepository.java
│   │   │   └── RoleMenuRepository.java
│   │   ├── entity/                   # 实体类
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   ├── Permission.java
│   │   │   ├── Menu.java
│   │   │   ├── Dict.java
│   │   │   ├── UserRole.java         # 用户-角色关联
│   │   │   ├── RolePermission.java   # 角色-权限关联
│   │   │   └── RoleMenu.java         # 角色-菜单关联
│   │   ├── dto/                      # 数据传输对象
│   │   ├── vo/                       # 视图对象
│   │   ├── common/                   # 公共类
│   │   │   └── Result.java           # 统一响应格式
│   │   ├── exception/                # 异常类
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   └── util/                     # 工具类
│   │       ├── JwtUtil.java          # JWT 工具
│   │       └── PasswordUtil.java     # 密码工具
│   ├── src/main/resources/
│   │   ├── application.yml           # 应用配置
│   │   └── schema.sql                # 数据库表结构
│   └── pom.xml                       # Maven 配置
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── api/                      # API 调用
│   │   │   ├── auth.js               # 认证 API
│   │   │   ├── user.js               # 用户 API
│   │   │   ├── role.js               # 角色 API
│   │   │   ├── permission.js         # 权限 API
│   │   │   ├── menu.js               # 菜单 API
│   │   │   └── dict.js               # 字典 API
│   │   ├── router/
│   │   │   └── index.js              # 路由配置（动态路由）
│   │   ├── store/
│   │   │   └── auth.js               # 认证状态管理
│   │   ├── utils/
│   │   │   ├── auth.js               # Token 管理
│   │   │   └── request.js            # Axios 封装
│   │   ├── views/                    # 页面组件
│   │   │   ├── Layout.vue            # 布局组件
│   │   │   ├── Home.vue              # 首页
│   │   │   ├── Login.vue             # 登录页
│   │   │   ├── UserList.vue          # 用户列表
│   │   │   ├── RoleList.vue          # 角色列表
│   │   │   ├── PermissionList.vue    # 权限列表
│   │   │   ├── MenuList.vue          # 菜单列表
│   │   │   └── DictList.vue          # 字典列表
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件（v-perm 指令）
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── database/                         # 数据库脚本
│   └── schema.sql                    # 完整表结构
│
└── README.md                         # 本文件
```

## 💡 核心功能详解

### 1. 完整的 RBAC 模型

```
用户（User）
  ↓ 多对多
角色（Role）
  ↓ 多对多
权限（Permission）─→ 三种类型：menu/button/api
  ↓
菜单（Menu）─→ 动态路由生成
```

**数据库关系**：
```sql
-- 用户表
sys_user (id, username, password, ...)

-- 角色表
sys_role (id, code, name, ...)

-- 权限表（树形结构）
sys_permission (id, parent_id, code, name, type, ...)

-- 菜单表（树形结构）
sys_menu (id, parent_id, code, name, path, component, ...)

-- 用户-角色关联表
sys_user_role (user_id, role_id)

-- 角色-权限关联表
sys_role_permission (role_id, permission_id)

-- 角色-菜单关联表
sys_role_menu (role_id, menu_id)

-- 字典表
sys_dict (id, code, label, dict_value, dict_type, ...)
```

### 2. 三层权限控制

#### 第一层：Menu 权限（路由级）
**控制**：用户能访问哪些页面

**实现**：
- 后端：查询用户的菜单权限
- 前端：动态生成路由、显示侧边栏菜单

```javascript
// router/index.js
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  await authStore.loadProfile();  // 获取用户菜单
  
  const routes = buildDynamicRoutes(authStore.menus);
  routes.forEach(route => router.addRoute('Layout', route));
  
  next();
});
```

#### 第二层：Button 权限（按钮级）
**控制**：用户能进行哪些操作

**实现**：
- 前端：v-perm 自定义指令

```vue
<template>
  <!-- 只有拥有 sys:user:create 权限的用户才能看到新增按钮 -->
  <el-button type="primary" v-perm="'sys:user:create'">新增用户</el-button>
  
  <!-- 只有拥有 sys:user:delete 权限的用户才能看到删除按钮 -->
  <el-button type="danger" v-perm="'sys:user:delete'">删除</el-button>
</template>

<script setup>
// main.js 中定义的 v-perm 指令
// app.directive('perm', {
//   mounted(el, binding) {
//     const authStore = useAuthStore();
//     const need = binding.value;
//     const has = authStore.permissions?.includes(need);
//     if (!has) {
//       el.parentNode && el.parentNode.removeChild(el);
//     }
//   }
// })
</script>
```

#### 第三层：API 权限（接口级）
**控制**：用户能调用哪些 API

**实现**：
- 后端：@PreAuthorize 注解

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    // 只有拥有 sys:user:list 权限的用户才能调用此接口
    @GetMapping
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<Page<UserVO>> page(@Validated UserQueryDTO dto) {
        return Result.success(userService.pageUsers(dto));
    }
    
    // 只有拥有 sys:user:add 权限的用户才能调用此接口
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:add')")
    public Result<UserVO> create(@Validated @RequestBody UserCreateDTO dto) {
        return Result.success("创建成功", userService.create(dto));
    }
    
    // 只有拥有 sys:user:del 权限的用户才能调用此接口
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:del')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功", null);
    }
}
```

### 3. 权限验证流程

```
┌─────────────┐
│ 1. 用户登录  │
└──────┬──────┘
       │
       ↓
┌────────────────────────────┐
│ 2. 后端生成 JWT Token      │
│    • 包含用户名            │
│    • 包含权限列表          │
│    • 设置过期时间（24h）   │
└────────┬───────────────────┘
         │
         ↓
┌────────────────────────────┐
│ 3. 前端存储 Token          │
│    • localStorage          │
│    • 每次请求携带 Token    │
└────────┬───────────────────┘
         │
         ↓
┌────────────────────────────┐
│ 4. 调用 /api/auth/profile  │
│    • SecurityFilter 解析   │
│    • 返回用户信息          │
│    • 返回权限列表          │
│    • 返回菜单树            │
└────────┬───────────────────┘
         │
         ↓
┌────────────────────────────┐
│ 5. 前端权限控制            │
│    • 动态生成路由          │
│    • 渲染侧边栏菜单        │
│    • v-perm 控制按钮显示   │
└────────┬───────────────────┘
         │
         ↓
┌────────────────────────────┐
│ 6. 后端 API 权限验证       │
│    • @PreAuthorize 注解    │
│    • Spring Security 验证  │
│    • 无权限返回 403        │
└────────────────────────────┘
```

### 4. 初始数据

系统启动时自动初始化以下数据：

#### 用户
| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | password123 | ADMIN | 系统管理员（所有权限） |
| manager | password123 | ADMIN | 管理员（所有权限） |
| jerry | password123 | OPS | 运维人员（部分权限） |
| tom | password123 | GUEST | 访客（只读权限） |

#### 角色
| 角色编码 | 角色名称 | 权限比例 | 说明 |
|---------|---------|---------|------|
| ADMIN | 系统管理员 | 100% | 拥有所有权限 |
| OPS | 运维人员 | 60% | 用户、角色管理（无删除权限） |
| GUEST | 访客 | 20% | 只能查看用户列表 |

#### 权限（70+）
```
系统管理 (sys:manage) - menu
├── 用户管理 (sys:user) - menu
│   ├── 查看用户 (sys:user:view) - button
│   ├── 新增用户 (sys:user:create) - button
│   ├── 编辑用户 (sys:user:update) - button
│   ├── 删除用户 (sys:user:delete) - button
│   ├── 启用/禁用 (sys:user:toggle) - button
│   ├── 用户列表 API (sys:user:list) - api
│   ├── 新增用户 API (sys:user:add) - api
│   ├── 编辑用户 API (sys:user:edit) - api
│   ├── 删除用户 API (sys:user:del) - api
│   └── 状态切换 API (sys:user:status) - api
├── 角色管理 (sys:role) - menu
│   └── ...（类似用户管理）
├── 权限管理 (sys:permission) - menu
│   └── ...（类似用户管理）
├── 菜单管理 (sys:menu) - menu
│   └── ...（类似用户管理）
└── 字典管理 (sys:dict) - menu
    └── ...（类似用户管理）
```

#### 菜单
```
系统管理
├── 用户管理 (/users)
├── 角色管理 (/roles)
├── 权限管理 (/permissions)
├── 菜单管理 (/menus)
└── 字典管理 (/dicts)
```

#### 字典
- **性别**：男、女、未知
- **状态**：启用、禁用
- **用户类型**：管理员、普通用户、访客

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- Maven 3.6+

### 1. 启动后端

```bash
cd final-project/backend

# 使用 Maven Wrapper（推荐）
./mvnw spring-boot:run

# 或使用系统 Maven
mvn spring-boot:run
```

**后端地址**：http://localhost:8080

**H2 控制台**：http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (留空)

### 2. 启动前端

```bash
cd final-project/frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

**前端地址**：http://localhost:5173

### 3. 登录测试

#### 超级管理员
- 用户名：`admin`
- 密码：`password123`
- 权限：✅ 所有功能

#### 运维人员
- 用户名：`jerry`
- 密码：`password123`
- 权限：⚠️ 用户、角色管理（无删除按钮）

#### 访客
- 用户名：`tom`
- 密码：`password123`
- 权限：⚠️ 只能查看用户列表

## 🧪 功能测试

### 测试场景 1：超级管理员（admin）

1. **用户管理**
   - ✅ 查看用户列表
   - ✅ 新增用户
   - ✅ 编辑用户
   - ✅ 删除用户
   - ✅ 启用/禁用用户

2. **角色管理**
   - ✅ 查看角色列表
   - ✅ 新增角色
   - ✅ 编辑角色
   - ✅ 删除角色
   - ✅ 分配权限
   - ✅ 分配菜单

3. **权限管理**
   - ✅ 查看权限树
   - ✅ 新增权限
   - ✅ 编辑权限
   - ✅ 删除权限

4. **菜单管理**
   - ✅ 查看菜单树
   - ✅ 新增菜单
   - ✅ 编辑菜单
   - ✅ 删除菜单

5. **字典管理**
   - ✅ 查看字典列表
   - ✅ 新增字典
   - ✅ 编辑字典
   - ✅ 删除字典

### 测试场景 2：运维人员（jerry）

1. **侧边栏菜单**
   - ✅ 只显示"用户管理"和"角色管理"
   - ❌ 看不到"权限管理"、"菜单管理"、"字典管理"

2. **用户管理**
   - ✅ 查看用户列表
   - ✅ 新增用户按钮
   - ✅ 编辑用户按钮
   - ❌ **没有删除按钮**（button 权限控制）
   - ✅ 启用/禁用用户按钮

3. **角色管理**
   - ✅ 查看角色列表
   - ✅ 新增角色按钮
   - ✅ 编辑角色按钮
   - ❌ **没有删除按钮**（button 权限控制）
   - ✅ 分配权限按钮
   - ✅ 分配菜单按钮

4. **API 测试**
   - 尝试调用删除用户 API：
     ```bash
     curl -X DELETE "http://localhost:8080/api/users/1" \
       -H "Authorization: Bearer JERRY_TOKEN"
     ```
   - **预期结果**：403 Forbidden（API 权限控制）

### 测试场景 3：访客（tom）

1. **侧边栏菜单**
   - ✅ 只显示"用户管理"
   - ❌ 看不到其他所有菜单

2. **用户管理**
   - ✅ 查看用户列表
   - ❌ **没有任何操作按钮**（所有 button 权限都没有）

3. **路由访问测试**
   - 尝试直接访问 `/roles`：**无法访问**（路由守卫拦截）
   - 尝试直接访问 `/permissions`：**无法访问**（路由守卫拦截）

## 🎓 核心技术点

### 1. JWT Token 生成与验证

```java
// JwtUtil.java
public String generateToken(String username, List<String> permissions) {
    return Jwts.builder()
        .setSubject(username)
        .claim("permissions", permissions)  // ⭐ 权限列表存入 Token
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
}

public List<String> getPermissionsFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.get("permissions", List.class);  // ⭐ 从 Token 提取权限
}
```

### 2. Spring Security 配置

```java
// SecurityConfig.java
@Configuration
@EnableMethodSecurity  // ⭐ 启用方法级安全
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // ⭐ 无状态
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/error").permitAll()  // 登录接口放行
                .anyRequest().authenticated()  // 其他接口需要认证
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);  // ⭐ 添加 JWT 过滤器
        
        return http.build();
    }
}
```

### 3. SecurityFilter（JWT 过滤器）

```java
// SecurityFilter.java
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            List<String> permissions = jwtUtil.getPermissionsFromToken(token);  // ⭐ 提取权限
            
            // 将权限转换为 Spring Security 的 Authority
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            
            // 设置认证信息
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);  // ⭐ 设置到上下文
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 4. 全局异常处理

```java
// GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @Value("${app.dev-mode:false}")
    private boolean devMode;
    
    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    // 权限不足
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足：{}", e.getMessage());
        return Result.error(403, "权限不足");
    }
    
    // 系统异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        String message = devMode ? e.getMessage() : "系统异常";
        return Result.error(500, message);
    }
}
```

### 5. 动态查询（JPA Specification）

```java
// 正确的 Specification 写法
public Page<User> pageUsers(UserQueryDTO query) {
    Specification<User> spec = (root, cq, cb) -> {
        List<Predicate> predicates = new ArrayList<>();  // ⭐ 使用 List 收集条件
        
        if (StringUtils.hasText(query.getKeyword())) {
            String like = "%" + query.getKeyword().trim() + "%";
            predicates.add(
                cb.or(
                    cb.like(root.get("username"), like),
                    cb.like(root.get("nickname"), like),
                    cb.like(root.get("email"), like)
                )
            );
        }
        
        if (query.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), query.getStatus()));
        }
        
        return cb.and(predicates.toArray(new Predicate[0]));  // ⭐ 正确组合条件
    };
    
    return userRepository.findAll(spec, pageable);
}
```

## 📝 最佳实践

### 1. 权限编码规范

✅ **推荐**：
```
sys:user         - 用户管理菜单
sys:user:create  - 新增用户按钮
sys:user:add     - 新增用户 API
sys:user:delete  - 删除用户按钮
sys:user:del     - 删除用户 API
```

❌ **不推荐**：
```
USER_MANAGEMENT_001
user.create
createUser
```

### 2. 密码安全

```java
// 使用 BCrypt 加密密码
String encoded = passwordUtil.encode("password123");

// 验证密码
boolean matches = passwordUtil.matches("password123", encoded);
```

### 3. 前端 Token 管理

```javascript
// utils/auth.js
const TOKEN_KEY = 'admin_token';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY);
}
```

### 4. Axios 拦截器

```javascript
// utils/request.js
request.interceptors.request.use(config => {
  const token = getToken();
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;  // ⭐ 自动添加 Token
  }
  return config;
});

request.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return res;
  },
  error => {
    if (error.response?.status === 401) {
      // Token 过期，跳转登录
      removeToken();
      router.push('/login');
    }
    return Promise.reject(error);
  }
);
```

## 🔥 进阶扩展

完成本项目后，你可以继续扩展以下功能：

### 1. 性能优化
- ✅ Redis 缓存（用户信息、权限、菜单）
- ✅ 权限树懒加载
- ✅ 分页查询优化
- ✅ SQL 索引优化

### 2. 安全增强
- ✅ 登录日志记录
- ✅ 操作日志审计
- ✅ IP 白名单
- ✅ 防止暴力破解（登录失败锁定）
- ✅ XSS/CSRF 防护

### 3. 功能扩展
- ✅ 部门管理（树形结构）
- ✅ 数据权限（部门数据隔离）
- ✅ 在线用户管理
- ✅ 定时任务管理
- ✅ 系统监控（CPU、内存、磁盘）

### 4. 部署相关
- ✅ MySQL/PostgreSQL 数据库
- ✅ Docker 容器化部署
- ✅ Nginx 反向代理
- ✅ HTTPS 配置
- ✅ CI/CD 流程

## 📚 参考资料

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security 官方文档](https://docs.spring.io/spring-security/reference/)
- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 组件库](https://element-plus.org/zh-CN/)
- [JWT 介绍](https://jwt.io/)
- [RBAC 权限模型](https://en.wikipedia.org/wiki/Role-based_access_control)

## 🎯 学习路径

```
第一章：登录功能
  ↓
第二章：用户管理
  ↓
第三章：角色管理
  ↓
第四章：权限管理
  ↓
第五章：菜单管理
  ↓
第六章：系统功能（字典管理）
  ↓
Final Project：完整的 RBAC 系统  ← 【当前项目】
```

## 📝 常见问题

### 1. 如何添加新的权限？
1. 在数据库或 DataInitializer 中添加权限数据
2. 在 Controller 方法上添加 @PreAuthorize 注解
3. 在前端按钮上添加 v-perm 指令

### 2. 如何添加新的菜单？
1. 在数据库或 DataInitializer 中添加菜单数据
2. 在前端创建对应的 Vue 组件
3. 在 router/index.js 的 viewMap 中添加路由映射

### 3. 如何切换到 MySQL 数据库？
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_db?useUnicode=true&characterEncoding=utf8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update  # 生产环境改为 none
    show-sql: true
```

### 4. Token 过期时间如何修改？
```java
// JwtUtil.java
private static final long EXPIRATION = 24 * 60 * 60 * 1000;  // 24小时

// 修改为 7 天
private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;
```

---

**🎉 恭喜！你已经完成了一个完整的企业级 RBAC 权限管理系统的学习！**

**这个项目可以作为你的：**
- ✅ 毕业设计/课程设计
- ✅ 面试作品集
- ✅ 实际项目的基础框架
- ✅ Spring Boot + Vue 3 学习范例

**祝你学习愉快，前程似锦！** 🚀
