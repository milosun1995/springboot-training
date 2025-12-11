# 第四章：权限管理 - 前端项目

## 项目说明

在登录基础上新增权限树管理：查询、创建、编辑、删除、状态切换，支持树形展示。

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
chapter-04-permission/
├── src/
│   ├── api/                    # API 接口
│   │   ├── auth.js
│   │   └── permission.js
│   ├── views/                  # 页面组件
│   │   ├── Login.vue
│   │   └── PermissionList.vue
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

- 登录与 Token 管理（沿用第一章）
- 权限树查询与展示（el-table tree）
- 权限新增/编辑：表单校验、类型/路径/方法等字段
- 状态切换与删除
- 路由：登录后跳转 `/permissions`

## 测试账号

- 用户名：admin
- 密码：admin123

## 常见问题

### 1. 跨域问题
- 确保后端已配置 CORS（`backend/src/main/java/com/training/admin/config/CorsConfig.java`）

### 2. 接口请求失败
- 检查后端服务是否启动
- 检查 `.env` 中的 `VITE_API_BASE_URL` 配置
- 检查浏览器控制台的错误信息

### 3. Token 存储问题
- 确认浏览器未禁用 localStorage
- 确认后端返回 Token

