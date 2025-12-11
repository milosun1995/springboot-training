# 第二章：用户管理 - 后端项目

## 项目说明

实现用户管理接口：分页/条件查询、创建、更新、删除、启用/禁用；登录沿用第一章。

## 技术栈

- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0+
- JWT (jjwt 0.11.5)
- Lombok
- Spring Security (仅用于密码加密)

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE springboot_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行本章脚本：
```bash
mysql -u root -p springboot_admin < ../../database/schema.sql
mysql -u root -p springboot_admin < ../../database/init-data.sql
```

3. 修改配置文件 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    username: root  # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码
```

### 3. 运行项目

```bash
# 使用 Maven 运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/admin-system-1.0.0.jar
```

### 4. 测试接口示例

- 登录（沿用第一章）
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

- 分页查询
```bash
curl "http://localhost:8080/api/users?page=0&size=10&keyword=adm&status=1" \
  -H "Authorization: Bearer <token>"
```

- 新增用户
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password123","nickname":"新用户","email":"new@example.com","status":1}'
```

- 状态切换
```bash
curl -X POST http://localhost:8080/api/users/1/toggle \
  -H "Authorization: Bearer <token>"
```

## 项目结构

```
chapter-02-user/
├── src/
│   ├── main/
│   │   ├── java/com/training/admin/
│   │   │   ├── AdminApplication.java      # 主启动类
│   │   │   ├── entity/                    # 实体类
│   │   │   │   └── User.java
│   │   │   ├── repository/                # 数据访问层
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                   # 业务逻辑层
│   │   │   │   ├── AuthService.java       # 登录
│   │   │   │   └── UserService.java       # 用户管理
│   │   │   ├── controller/                # 控制器层
│   │   │   │   ├── AuthController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                       # 数据传输对象
│   │   │   │   ├── LoginDTO.java
│   │   │   │   ├── UserCreateDTO.java
│   │   │   │   ├── UserUpdateDTO.java
│   │   │   │   └── UserQueryDTO.java
│   │   │   ├── vo/                        # 视图对象
│   │   │   │   ├── LoginVO.java
│   │   │   │   └── UserVO.java
│   │   │   ├── common/                    # 通用类
│   │   │   │   └── Result.java
│   │   │   ├── exception/                 # 异常类
│   │   │   │   ├── BusinessException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── config/                     # 配置类
│   │   │   │   └── CorsConfig.java
│   │   │   └── util/                       # 工具类
│   │   │       ├── JwtUtil.java
│   │   │       └── PasswordUtil.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## API 摘要
- POST `/api/auth/login`
- GET `/api/users` （page/size/keyword/status）
- POST `/api/users`
- PUT `/api/users/{id}`
- DELETE `/api/users/{id}`
- POST `/api/users/{id}/toggle`

## 常见问题

### 1. 数据库连接失败

- 检查 MySQL 服务是否启动
- 检查数据库用户名密码是否正确
- 检查数据库是否存在

### 2. 端口被占用

修改 `application.yml` 中的 `server.port`

### 3. JWT Token 生成失败

检查 `application.yml` 中的 `jwt.secret` 配置，确保长度足够（至少32个字符）

## 下一步

完成本章后，可继续第三章：角色管理。

