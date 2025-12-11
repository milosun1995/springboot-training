# 第一章：登录功能 - 后端项目

## 项目说明

这是 SpringBoot 后台管理系统的第一章项目，实现了完整的用户登录功能。

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

2. 执行数据库脚本：
```bash
# 执行表结构脚本
mysql -u root -p springboot_admin < ../../database/schema.sql

# 执行初始化数据脚本
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

### 4. 测试接口

项目启动后，可以使用 Postman 或 curl 测试登录接口：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**测试账号**：
- 用户名：admin
- 密码：admin123

## 项目结构

```
chapter-01-login/
├── src/
│   ├── main/
│   │   ├── java/com/training/admin/
│   │   │   ├── AdminApplication.java      # 主启动类
│   │   │   ├── entity/                    # 实体类
│   │   │   │   └── User.java
│   │   │   ├── repository/                # 数据访问层
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                   # 业务逻辑层
│   │   │   │   └── AuthService.java
│   │   │   ├── controller/                # 控制器层
│   │   │   │   └── AuthController.java
│   │   │   ├── dto/                       # 数据传输对象
│   │   │   │   └── LoginDTO.java
│   │   │   ├── vo/                        # 视图对象
│   │   │   │   └── LoginVO.java
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

## API 接口

### 登录接口

**URL**: `/api/auth/login`  
**Method**: `POST`  
**Content-Type**: `application/json`

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "username": "admin",
    "nickname": "系统管理员"
  }
}
```

**错误响应**:
```json
{
  "code": 401,
  "message": "用户名或密码错误"
}
```

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

完成本章后，可以继续学习第二章：用户管理模块。

