# 第一章：登录功能

## 📖 章节说明

本章是整个 Spring Boot + Vue 3 全栈项目的起点，实现了完整的用户登录功能。通过本章学习，你将搭建起前后端分离的基础架构，并掌握 JWT Token 认证的核心原理。

**本章是所有后续章节的基础**，建议初学者从这里开始，逐步掌握：
- 如何搭建 Spring Boot 项目
- 如何搭建 Vue 3 项目  
- 前后端如何联调
- JWT 认证的完整流程

## 🎯 学习目标

通过本章学习，你将掌握：

### 后端技能
- ✅ Spring Boot 3.x 项目搭建
- ✅ Spring Data JPA 基础使用
- ✅ JWT Token 生成和验证
- ✅ BCrypt 密码加密
- ✅ 全局异常处理
- ✅ 跨域（CORS）配置
- ✅ 统一响应格式（Result）

### 前端技能
- ✅ Vue 3 项目搭建（Vite）
- ✅ Element Plus UI 组件库
- ✅ Vue Router 路由管理
- ✅ Pinia 状态管理
- ✅ Axios 请求封装
- ✅ Token 存储和管理
- ✅ 请求拦截器配置

### 核心概念
- ✅ 前后端分离架构
- ✅ RESTful API 设计
- ✅ JWT 认证流程
- ✅ 密码加密存储
- ✅ Token 刷新机制

## 📁 项目结构

```
chapter-01-login/
├── backend/                          # 后端项目（Spring Boot）
│   ├── src/main/java/com/training/admin/
│   │   ├── config/                   # 配置类
│   │   │   ├── CorsConfig.java       # 跨域配置
│   │   │   └── DataInitializer.java  # 数据初始化
│   │   ├── controller/               # 控制器层
│   │   │   └── AuthController.java   # 认证接口（登录）
│   │   ├── service/                  # 业务层
│   │   │   └── AuthService.java      # 认证服务
│   │   ├── repository/               # 数据访问层
│   │   │   └── UserRepository.java   # 用户数据访问
│   │   ├── entity/                   # 实体类
│   │   │   └── User.java             # 用户实体
│   │   ├── dto/                      # 数据传输对象
│   │   │   └── LoginDTO.java         # 登录请求 DTO
│   │   ├── vo/                       # 视图对象
│   │   │   └── LoginVO.java          # 登录响应 VO
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
│   ├── mvnw                          # Maven Wrapper（Unix）
│   ├── mvnw.cmd                      # Maven Wrapper（Windows）
│   └── pom.xml                       # Maven 配置
│
├── frontend/                         # 前端项目（Vue 3）
│   ├── src/
│   │   ├── api/                      # API 调用
│   │   │   └── auth.js               # 认证 API
│   │   ├── router/
│   │   │   └── index.js              # 路由配置
│   │   ├── store/
│   │   │   └── auth.js               # 认证状态管理
│   │   ├── utils/
│   │   │   ├── auth.js               # Token 管理
│   │   │   └── request.js            # Axios 封装
│   │   ├── views/
│   │   │   ├── Login.vue             # 登录页
│   │   │   └── Home.vue              # 首页
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── index.html
│   ├── package.json                  # NPM 配置
│   └── vite.config.js                # Vite 配置
│
├── database/                         # 数据库脚本（可选，示例表结构）
│   └── schema.sql                    # 表结构（参考）
│
└── README.md                         # 本文件
```

## 💡 核心知识点

### 1. 用户表设计

```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
  nickname VARCHAR(50) COMMENT '昵称',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(255) COMMENT '头像 URL',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**设计要点**：
- `username`：用户名，唯一索引
- `password`：密码，使用 BCrypt 加密存储（不可逆）
- `status`：用户状态，可以禁用用户登录

### 2. JWT Token 结构

```
Header（头部）
{
  "alg": "HS512",           // 签名算法
  "typ": "JWT"              // Token 类型
}

Payload（载荷）
{
  "sub": "admin",           // 用户名
  "iat": 1234567890,        // 签发时间
  "exp": 1234654290         // 过期时间（24小时）
}

Signature（签名）
HMACSHA512(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### 3. 登录流程

```
┌─────────────┐
│ 1. 用户输入  │
│  用户名+密码 │
└──────┬──────┘
       │
       ↓
┌─────────────────────────┐
│ 2. 前端发送 POST 请求   │
│    /api/auth/login      │
└──────┬──────────────────┘
       │
       ↓
┌─────────────────────────┐
│ 3. 后端验证             │
│  • 查询用户是否存在     │
│  • 验证密码是否正确     │
│  • 检查用户状态         │
└──────┬──────────────────┘
       │
       ↓
┌─────────────────────────┐
│ 4. 生成 JWT Token       │
│  • 包含用户名           │
│  • 设置过期时间（24h）  │
│  • 使用密钥签名         │
└──────┬──────────────────┘
       │
       ↓
┌─────────────────────────┐
│ 5. 返回 Token 给前端    │
│  {                      │
│    token: "eyJhbG...",  │
│    username: "admin",   │
│    nickname: "管理员"   │
│  }                      │
└──────┬──────────────────┘
       │
       ↓
┌─────────────────────────┐
│ 6. 前端存储 Token       │
│  • localStorage 存储    │
│  • 后续请求携带 Token   │
└─────────────────────────┘
```

### 4. 密码加密（BCrypt）

```java
// 加密密码
String rawPassword = "admin123";
String encodedPassword = passwordUtil.encode(rawPassword);
// 结果：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z...

// 验证密码
boolean matches = passwordUtil.matches(rawPassword, encodedPassword);
// 结果：true

// 特点：
// • 同一个密码每次加密结果不同（动态盐值）
// • 不可逆（无法解密）
// • 验证时使用 matches() 方法
```

## ✨ 功能特性

### 后端功能
- ✅ 用户登录（用户名 + 密码）
- ✅ 密码加密存储（BCrypt）
- ✅ JWT Token 生成
- ✅ Token 验证（JwtUtil）
- ✅ 统一响应格式（Result）
- ✅ 全局异常处理
- ✅ 跨域配置（CORS）
- ✅ 用户数据初始化

### 前端功能
- ✅ 登录页面（表单验证）
- ✅ Token 存储（localStorage）
- ✅ Axios 请求拦截器（自动添加 Token）
- ✅ Axios 响应拦截器（统一错误处理）
- ✅ 路由守卫（未登录拦截）
- ✅ 登录成功跳转
- ✅ 退出登录（清除 Token）

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- Maven 3.6+（或使用 Maven Wrapper）

### 1. 数据库准备（MySQL）

- 创建数据库：`CREATE DATABASE springboot_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- 配置账号/密码：在 `backend/src/main/resources/application.yml` 的 `spring.datasource.username/password` 填写你的 MySQL 账户。
- 连接 URL 示例（已内置）：`jdbc:mysql://localhost:3306/springboot_admin?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true`
- 表结构与初始数据：由代码内的 `schema.sql` 与 `DataInitializer` 自动创建与插入，无需手工导入。

**初始用户**：
```
用户名：admin
密码：password123
昵称：系统管理员
```

### 2. 启动后端

```bash
cd chapter-01-login/backend

# 使用 Maven Wrapper（推荐）
./mvnw spring-boot:run

# 或使用系统 Maven
mvn spring-boot:run
```

**后端地址**：http://localhost:8080

**查看日志**，确认启动成功：
```
Started Application in 3.5 seconds
默认数据初始化成功！
```

### 3. 启动前端

```bash
cd chapter-01-login/frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

**前端地址**：http://localhost:5173

### 4. 测试登录

1. 访问：http://localhost:5173
2. 输入用户名：`admin`
3. 输入密码：`password123`
4. 点击"登录"按钮

**预期结果**：
- ✅ 登录成功，跳转到首页（/home）
- ✅ 控制台显示 Token
- ✅ localStorage 中存储了 Token

## 🧪 功能测试

### 测试场景 1：正常登录

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**预期响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxODg3...",
    "username": "admin",
    "nickname": "系统管理员"
  }
}
```

### 测试场景 2：用户名错误

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wrong",
    "password": "password123"
  }'
```

**预期响应**：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 测试场景 3：密码错误

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrong"
  }'
```

**预期响应**：
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

### 测试场景 4：前端路由守卫

1. 清除 localStorage 中的 Token
2. 直接访问 http://localhost:5173/home
3. **预期结果**：自动跳转到 `/login`

## 📚 核心代码解析

### 1. 用户实体（User.java）

```java
@Entity
@Table(name = "sys_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50, unique = true)
    private String username;  // 用户名（唯一）
    
    @Column(nullable = false, length = 255)
    private String password;  // 密码（BCrypt 加密）
    
    @Column(length = 50)
    private String nickname;  // 昵称
    
    @Column(length = 100)
    private String email;  // 邮箱
    
    @Column(length = 20)
    private String phone;  // 手机号
    
    @Column(length = 255)
    private String avatar;  // 头像
    
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;  // 0-禁用 1-启用
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
```

### 2. 认证服务（AuthService.java）

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 查找用户
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        
        // 2. 验证密码
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }
        
        // 4. 生成 Token
        String token = jwtUtil.generateToken(user.getUsername());
        
        // 5. 返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        
        return loginVO;
    }
}
```

### 3. JWT 工具（JwtUtil.java）

```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;  // 密钥（至少 64 字节）
    
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;  // 24 小时
    
    // 生成 Token
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    // 验证 Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    // 从 Token 获取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### 4. 密码工具（PasswordUtil.java）

```java
@Component
public class PasswordUtil {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    // 加密密码
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    // 验证密码
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 5. 统一响应格式（Result.java）

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;     // 响应码：200-成功，其他-失败
    private String message;   // 响应消息
    private T data;           // 响应数据
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
```

### 6. 前端 Axios 封装（request.js）

```javascript
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { getToken } from './auth';

const request = axios.create({
  baseURL: '',  // 使用 Vite 代理
  timeout: 5000
});

// 请求拦截器（自动添加 Token）
request.interceptors.request.use(
  config => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器（统一错误处理）
request.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return res;
  },
  error => {
    const status = error.response?.status;
    let message = '网络错误';
    
    if (status === 401) {
      message = '未授权，请重新登录';
    } else if (status === 403) {
      message = '拒绝访问';
    } else if (status === 404) {
      message = '请求地址不存在';
    } else if (status === 500) {
      message = '服务器错误';
    }
    
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default request;
```

### 7. 前端路由守卫（router/index.js）

```javascript
import { createRouter, createWebHistory } from 'vue-router';
import { getToken } from '@/utils/auth';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken();
  
  // 如果去登录页，直接放行
  if (to.path === '/login') {
    if (token) {
      // 已登录，跳转到首页
      return next('/home');
    }
    return next();
  }
  
  // 其他页面需要登录
  if (!token) {
    return next('/login');
  }
  
  next();
});

export default router;
```

## 🎓 学习要点

### 1. 为什么使用 JWT？

**优点**：
- ✅ 无状态：服务器不需要存储 Session
- ✅ 跨域：可以跨域使用
- ✅ 性能：减轻服务器压力
- ✅ 扩展性：Token 可以包含用户信息

**与 Session 的区别**：
| 项目 | Session | JWT |
|-----|---------|-----|
| 存储位置 | 服务器 | 客户端 |
| 扩展性 | 差（需要 Session 共享） | 好（无状态） |
| 安全性 | 高（服务器控制） | 中（客户端存储） |
| 性能 | 差（需要查询 Session） | 好（无需查询） |

### 2. 密码安全最佳实践

✅ **推荐**：
```java
// 使用 BCrypt 加密
String encoded = passwordUtil.encode("password123");

// 特点：
// • 加密过程包含随机盐值
// • 同一密码每次加密结果不同
// • 不可逆，无法解密
// • 验证使用 matches() 方法
```

❌ **不推荐**：
```java
// MD5（已被破解，不安全）
String md5 = DigestUtils.md5Hex("password123");

// 明文存储（严重安全问题）
user.setPassword("password123");
```

### 3. Token 存储位置

| 存储位置 | 优点 | 缺点 | 安全性 |
|---------|------|------|--------|
| **localStorage** | 持久化，刷新不丢失 | 容易被 XSS 攻击 | ⚠️ 中 |
| **sessionStorage** | 关闭浏览器自动清除 | 刷新会丢失 | ⚠️ 中 |
| **Cookie（HttpOnly）** | 防止 XSS 攻击 | 容易被 CSRF 攻击 | ✅ 高 |
| **内存（Vuex/Pinia）** | 最安全 | 刷新会丢失 | ✅ 最高 |

**本项目使用**：localStorage（简单易用，适合学习）

### 4. 跨域（CORS）配置

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");  // 前端地址
        config.addAllowedMethod("*");                      // 允许所有方法
        config.addAllowedHeader("*");                      // 允许所有请求头
        config.setAllowCredentials(true);                  // 允许携带凭证
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

## 🔗 与其他章节的关系

```
第一章：登录功能  ← 【当前章节】
    ↓
第二章：用户管理（CRUD 操作）
    ↓
第三章：角色管理（RBAC 第一步）
    ↓
第四章：权限管理（树形结构）
    ↓
第五章：菜单管理（动态路由）
    ↓
第六章：系统功能（字典管理）
    ↓
最终项目：完整的 RBAC 系统
```

## 🎯 下一步

完成本章后，你已经掌握了：
- ✅ Spring Boot 项目搭建
- ✅ Vue 3 项目搭建
- ✅ JWT Token 认证
- ✅ 前后端联调

**继续学习**：
- **第二章：用户管理** - 实现用户的增删改查、分页查询等完整功能

## 📝 常见问题

### 1. JWT Secret 密钥长度要求？

使用 HS512 算法时，密钥至少需要 64 字节（512 位）：

```yaml
# application.yml
jwt:
  secret: your-secret-key-must-be-at-least-64-bytes-for-hs512-algorithm-to-work
```

### 2. Token 过期后如何处理？

**方案一：重新登录**（本章采用）
```javascript
if (error.response?.status === 401) {
  removeToken();
  router.push('/login');
}
```

**方案二：刷新 Token**（进阶）
- 后端提供 `/api/auth/refresh` 接口
- 前端拦截器自动刷新 Token

### 3. 如何防止暴力破解？

```java
// 方案：登录失败次数限制
private Map<String, Integer> loginFailCount = new ConcurrentHashMap<>();

public LoginVO login(LoginDTO dto) {
    String key = dto.getUsername();
    int failCount = loginFailCount.getOrDefault(key, 0);
    
    // 失败次数超过 5 次，锁定 10 分钟
    if (failCount >= 5) {
        throw new BusinessException(403, "登录失败次数过多，请 10 分钟后再试");
    }
    
    // 验证密码...
    if (!passwordUtil.matches(dto.getPassword(), user.getPassword())) {
        loginFailCount.put(key, failCount + 1);
        throw new BusinessException(401, "用户名或密码错误");
    }
    
    // 登录成功，清除失败计数
    loginFailCount.remove(key);
    // ...
}
```

### 4. 前端如何自动添加 Token？

使用 Axios 请求拦截器：
```javascript
request.interceptors.request.use(config => {
  const token = getToken();
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});
```

## 📖 参考资料

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [JWT 官网](https://jwt.io/)
- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 组件库](https://element-plus.org/zh-CN/)
- [Vite 官方文档](https://cn.vitejs.dev/)
- [BCrypt 介绍](https://en.wikipedia.org/wiki/Bcrypt)

---

**🎉 恭喜！完成本章后，你已经搭建起了完整的前后端分离架构，并掌握了 JWT 认证的核心原理！**
