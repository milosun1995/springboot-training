# 第一章：登录功能 - 前端项目

## 项目说明

这是 SpringBoot 后台管理系统的第一章前端项目，实现了完整的用户登录页面和 Token 管理。

## 技术栈

- Vue 3.3.4
- Vue Router 4.2.5
- Pinia 2.1.7
- Element Plus 2.4.4
- Axios 1.6.2
- Vite 5.0.8

## 快速开始

### 1. 环境要求

- Node.js 16+
- npm 或 yarn

### 2. 安装依赖

```bash
npm install
# 或
yarn install
```

### 3. 配置环境变量

创建 `.env` 文件（如果不存在）：

```env
VITE_API_BASE_URL=http://localhost:8080
```

### 4. 运行项目

```bash
npm run dev
# 或
yarn dev
```

项目将在 http://localhost:5173 启动

### 5. 构建生产版本

```bash
npm run build
# 或
yarn build
```

## 项目结构

```
chapter-01-login/
├── src/
│   ├── api/                    # API 接口
│   │   └── auth.js
│   ├── views/                  # 页面组件
│   │   └── Login.vue
│   ├── router/                 # 路由配置
│   │   └── index.js
│   ├── store/                  # 状态管理
│   │   └── auth.js
│   ├── utils/                  # 工具函数
│   │   ├── request.js
│   │   └── auth.js
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── .env
```

## 功能说明

### 登录功能

1. **登录页面** (`src/views/Login.vue`)
   - 用户名和密码输入
   - 表单验证
   - 登录按钮和加载状态

2. **Token 管理** (`src/utils/auth.js`)
   - Token 存储到 localStorage
   - Token 获取和删除

3. **请求拦截** (`src/utils/request.js`)
   - 自动添加 Token 到请求头
   - 统一错误处理
   - Token 过期自动跳转登录

4. **路由守卫** (`src/router/index.js`)
   - 未登录自动跳转到登录页
   - 已登录访问登录页自动跳转

5. **状态管理** (`src/store/auth.js`)
   - 用户信息存储
   - 登录和登出方法

## 测试账号

- 用户名：admin
- 密码：admin123

## 常见问题

### 1. 跨域问题

确保后端已配置 CORS，参考 `backend/chapter-01-login/src/main/java/com/training/admin/config/CorsConfig.java`

### 2. 接口请求失败

- 检查后端服务是否启动
- 检查 `.env` 中的 `VITE_API_BASE_URL` 配置
- 检查浏览器控制台的错误信息

### 3. Token 存储问题

- 检查浏览器是否禁用了 localStorage
- 检查 Token 是否正确返回

## 下一步

完成本章后，可以继续学习第二章：用户管理模块。

