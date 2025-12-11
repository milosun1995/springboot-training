# 第四章：权限管理 - 后端项目

## 项目说明

实现权限点（菜单/按钮/API）的树形管理：查询、创建、编辑、删除、状态切换；登录沿用第一章。

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

- 查询权限树
```bash
curl "http://localhost:8080/api/permissions/tree" \
  -H "Authorization: Bearer <token>"
```

- 新增权限
```bash
curl -X POST http://localhost:8080/api/permissions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"数据看板","code":"MENU_DASH","type":"menu","path":"/dashboard","status":1,"sort":1}'
```

- 状态切换
```bash
curl -X POST http://localhost:8080/api/permissions/1/toggle \
  -H "Authorization: Bearer <token>"
```

## 项目结构

```
chapter-04-permission/
├── src/
│   ├── main/
│   │   ├── java/com/training/admin/
│   │   │   ├── AdminApplication.java      # 主启动类
│   │   │   ├── entity/                    # 实体类
│   │   │   │   ├── User.java
│   │   │   │   └── Permission.java
│   │   │   ├── repository/                # 数据访问层
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── PermissionRepository.java
│   │   │   ├── service/                   # 业务逻辑层
│   │   │   │   ├── AuthService.java       # 登录
│   │   │   │   └── PermissionService.java # 权限管理
│   │   │   ├── controller/                # 控制器层
│   │   │   │   ├── AuthController.java
│   │   │   │   └── PermissionController.java
│   │   │   ├── dto/                       # 数据传输对象
│   │   │   │   ├── LoginDTO.java
│   │   │   │   ├── PermissionCreateDTO.java
│   │   │   │   ├── PermissionUpdateDTO.java
│   │   │   │   └── PermissionQueryDTO.java
│   │   │   ├── vo/                        # 视图对象
│   │   │   │   ├── LoginVO.java
│   │   │   │   └── PermissionVO.java
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
- GET `/api/permissions/tree` （支持 keyword/status 过滤）
- POST `/api/permissions`
- PUT `/api/permissions/{id}`
- DELETE `/api/permissions/{id}`
- POST `/api/permissions/{id}/toggle`

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

完成本章后，可继续第五章：菜单管理。

