# 第五章：菜单管理

## 📖 章节说明

本章实现了动态菜单管理功能，是 RBAC 系统中控制用户界面可见性的关键模块。菜单采用树形结构设计，与权限系统紧密关联，为前端动态路由生成奠定基础。

**与前几章的关系**：
- 第二章：用户管理（User）
- 第三章：角色管理（Role）
- 第四章：权限管理（Permission）
- 第五章：菜单管理（Menu）← **当前章节**
- 下一章：将实现完整的用户-角色-权限-菜单关联

## 🎯 学习目标

通过本章学习，你将掌握：

### 后端技能
- ✅ 菜单树形结构设计
- ✅ 菜单与权限的关联关系
- ✅ 动态菜单数据查询
- ✅ 菜单图标和组件路径配置

### 前端技能
- ✅ 动态路由生成
- ✅ 树形菜单展示
- ✅ 图标选择器
- ✅ 路由组件映射

### 系统架构
- ✅ 菜单驱动的前端路由
- ✅ 基于角色的菜单过滤
- ✅ 菜单与权限的协同工作

## 📁 项目结构

```
chapter-05-menu/
├── backend/
│   ├── src/main/java/com/training/admin/
│   │   ├── controller/
│   │   │   └── MenuController.java        # 菜单管理 API
│   │   ├── service/
│   │   │   └── MenuService.java           # 菜单业务逻辑（树形构建）
│   │   ├── repository/
│   │   │   └── MenuRepository.java        # 菜单数据访问
│   │   ├── entity/
│   │   │   └── Menu.java                  # 菜单实体
│   │   ├── dto/
│   │   │   ├── MenuCreateDTO.java
│   │   │   ├── MenuUpdateDTO.java
│   │   │   └── MenuQueryDTO.java
│   │   └── vo/
│   │       └── MenuVO.java                # 菜单视图（含 children）
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── menu.js                    # 菜单 API
│   │   ├── router/
│   │   │   └── index.js                   # 动态路由生成
│   │   └── views/
│   │       ├── Login.vue
│   │       └── MenuList.vue               # 菜单列表（树形表格）
│   └── package.json
├── database/
│   ├── schema.sql                         # 菜单表结构
│   └── init-data.sql                      # 初始菜单数据
└── README.md
```

## 💡 核心知识点

### 1. 菜单表设计

```sql
CREATE TABLE sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT DEFAULT NULL COMMENT '父菜单ID',
  code VARCHAR(100) NOT NULL UNIQUE COMMENT '菜单编码（对应权限编码）',
  name VARCHAR(50) NOT NULL COMMENT '菜单名称',
  path VARCHAR(255) COMMENT '路由路径（如：/users）',
  component VARCHAR(255) COMMENT '组件路径（如：UserList）',
  icon VARCHAR(100) COMMENT '图标名称',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_parent (parent_id)
);
```

**设计要点**：
- `code`：菜单编码，通常与权限编码对应（如 `sys:user`）
- `path`：前端路由路径（如 `/users`）
- `component`：Vue 组件名称或路径（如 `UserList`）
- `icon`：Element Plus 图标名称（如 `User`、`Setting`）

### 2. 菜单与权限的关系

```
菜单（Menu）关联 权限（Permission）
  ↓
通过菜单编码（code）对应权限编码

示例：
  菜单 code: sys:user
  对应权限 code: sys:user (type=menu)
```

**关联方式**：
- 方式一：菜单表的 `code` 字段与权限表的 `code` 字段对应
- 方式二：通过中间表 `sys_role_menu` 建立角色与菜单的多对多关系

### 3. 菜单树结构示例

```
系统管理
├── 用户管理 (/users)
├── 角色管理 (/roles)
├── 权限管理 (/permissions)
├── 菜单管理 (/menus)
└── 字典管理 (/dicts)
```

### 4. 动态路由生成流程

```
1. 用户登录成功
   ↓
2. 后端根据用户角色查询菜单树
   ↓
3. 返回用户可访问的菜单列表
   ↓
4. 前端根据菜单数据动态生成路由
   ↓
5. router.addRoute() 添加路由
   ↓
6. 侧边栏根据菜单树渲染
```

## ✨ 功能特性

### 后端功能
- ✅ 菜单树查询（递归构建）
- ✅ 新增菜单（验证父级菜单）
- ✅ 编辑菜单（防止循环引用）
- ✅ 删除菜单（检查子菜单）
- ✅ 启用/禁用菜单
- ✅ 根据用户角色过滤菜单

### 前端功能
- ✅ 树形表格展示
- ✅ 父级菜单选择（el-tree-select）
- ✅ 图标选择（支持预览）
- ✅ 路由路径配置
- ✅ 组件路径配置
- ✅ 层级关系说明

## 🚀 快速开始

### 1. 数据库准备（H2）

应用启动时自动初始化菜单树：

```java
// DataInitializer.java
Menu sys = saveMenu(null, "sys:manage", "系统管理", null, null, "Setting", 1);

Menu userMenu = saveMenu(sys.getId(), "sys:user", "用户管理", "/users", "UserList", "User", 10);
Menu roleMenu = saveMenu(sys.getId(), "sys:role", "角色管理", "/roles", "RoleList", "Avatar", 20);
Menu permMenu = saveMenu(sys.getId(), "sys:permission", "权限管理", "/permissions", "PermissionList", "Lock", 30);
Menu menuMenu = saveMenu(sys.getId(), "sys:menu", "菜单管理", "/menus", "MenuList", "Menu", 40);
```

### 2. 启动后端

```bash
cd chapter-05-menu/backend
./mvnw spring-boot:run
```

### 3. 启动前端

```bash
cd chapter-05-menu/frontend
npm install
npm run dev
```

### 4. 测试功能

#### 登录
- 用户名：`admin`
- 密码：`password123`

#### 测试场景
1. **查看菜单树**：
   - 展开/折叠节点
   - 观察菜单层级
   - 查看图标显示

2. **新增菜单**：
   - 父级菜单：选择"系统管理"
   - 菜单编码：`sys:report`
   - 菜单名称：`报表管理`
   - 路由路径：`/reports`
   - 组件名称：`ReportList`
   - 图标：`DataAnalysis`

3. **编辑菜单**：
   - 修改菜单名称
   - 修改图标
   - 调整排序

4. **删除菜单**：
   - 尝试删除有子菜单的节点（应提示错误）
   - 删除叶子节点

## 🧪 API 测试

### 1. 获取菜单树
```bash
curl -X GET "http://localhost:8080/api/menus/tree?status=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "parentId": null,
      "code": "sys:manage",
      "name": "系统管理",
      "icon": "Setting",
      "sort": 1,
      "children": [
        {
          "id": 10,
          "parentId": 1,
          "code": "sys:user",
          "name": "用户管理",
          "path": "/users",
          "component": "UserList",
          "icon": "User",
          "sort": 10
        }
      ]
    }
  ]
}
```

### 2. 新增菜单
```bash
curl -X POST "http://localhost:8080/api/menus" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "parentId": 1,
    "code": "sys:report",
    "name": "报表管理",
    "path": "/reports",
    "component": "ReportList",
    "icon": "DataAnalysis",
    "sort": 50
  }'
```

## 📚 核心代码解析

### 1. 菜单实体（Menu.java）

```java
@Entity
@Table(name = "sys_menu")
@Data
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "parent_id")
    private Long parentId;  // 父菜单ID
    
    @Column(nullable = false, length = 100, unique = true)
    private String code;  // 菜单编码：sys:user
    
    @Column(nullable = false, length = 50)
    private String name;  // 菜单名称：用户管理
    
    @Column(length = 255)
    private String path;  // 路由路径：/users
    
    @Column(length = 255)
    private String component;  // 组件名称：UserList
    
    @Column(length = 100)
    private String icon;  // 图标名称：User
    
    @Column(columnDefinition = "int default 0")
    private Integer sort;  // 排序
    
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;  // 状态
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
```

### 2. 动态路由生成（router/index.js）

```javascript
// 组件映射表
const viewMap = {
  '/users': () => import('@/views/UserList.vue'),
  '/roles': () => import('@/views/RoleList.vue'),
  '/permissions': () => import('@/views/PermissionList.vue'),
  '/menus': () => import('@/views/MenuList.vue'),
  '/dicts': () => import('@/views/DictList.vue')
};

// 根据菜单生成路由
function buildDynamicRoutes(menus) {
  const routes = [];
  
  const traverse = (nodes) => {
    nodes.forEach(menu => {
      if (menu.path && viewMap[menu.path]) {
        routes.push({
          path: menu.path,
          name: menu.code || menu.path,
          component: viewMap[menu.path],
          meta: {
            title: menu.name,
            icon: menu.icon
          }
        });
      }
      
      if (menu.children && menu.children.length) {
        traverse(menu.children);
      }
    });
  };
  
  traverse(menus);
  return routes;
}

// 路由守卫（动态添加路由）
router.beforeEach(async (to, from, next) => {
  const token = getToken();
  
  if (!token) {
    return next('/login');
  }
  
  // 加载用户菜单
  if (!hasLoadedMenus) {
    const authStore = useAuthStore();
    await authStore.loadProfile();  // 获取用户菜单
    
    const dynamicRoutes = buildDynamicRoutes(authStore.menus);
    dynamicRoutes.forEach(route => {
      router.addRoute(route);  // 动态添加路由
    });
    
    hasLoadedMenus = true;
    return next({ ...to, replace: true });
  }
  
  next();
});
```

### 3. 侧边栏菜单渲染（Layout.vue）

```vue
<template>
  <el-aside width="200px">
    <el-menu
      :default-active="activeMenu"
      router
    >
      <menu-item
        v-for="menu in menus"
        :key="menu.id"
        :menu="menu"
      />
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const route = useRoute();
const authStore = useAuthStore();

// 当前激活的菜单
const activeMenu = computed(() => route.path);

// 用户的菜单树
const menus = computed(() => authStore.menus);
</script>

<!-- 递归菜单组件 -->
<script setup name="MenuItem">
defineProps({
  menu: {
    type: Object,
    required: true
  }
});
</script>

<template>
  <!-- 有子菜单 -->
  <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path || String(menu.id)">
    <template #title>
      <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
      <span>{{ menu.name }}</span>
    </template>
    <menu-item
      v-for="child in menu.children"
      :key="child.id"
      :menu="child"
    />
  </el-sub-menu>
  
  <!-- 叶子节点 -->
  <el-menu-item v-else :index="menu.path">
    <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
    <span>{{ menu.name }}</span>
  </el-menu-item>
</template>
```

## 🎓 学习要点

### 1. 菜单与权限的区别

| 项目 | 菜单（Menu） | 权限（Permission） |
|-----|------------|------------------|
| **作用范围** | 前端界面 | 前端 + 后端 |
| **控制内容** | 路由、侧边栏 | 路由、按钮、API |
| **数据结构** | 树形 | 树形 |
| **类型** | 无类型区分 | menu/button/api |

### 2. 路由路径命名规范

✅ **推荐**：
- `/users` - 小写，复数形式
- `/user/profile` - 嵌套路由使用斜杠
- `/system/dict` - 模块化路径

❌ **不推荐**：
- `/Users` - 首字母大写
- `/user_list` - 下划线分隔
- `/userManagement` - 驼峰命名

### 3. 图标选择

Element Plus 常用图标：
- `User` - 用户
- `Avatar` - 角色
- `Lock` - 权限
- `Menu` - 菜单
- `Document` - 文档
- `Setting` - 设置
- `DataAnalysis` - 数据分析

查看所有图标：https://element-plus.org/zh-CN/component/icon.html

### 4. 动态路由的生命周期

```
1. 应用启动
   ↓
2. 显示静态路由（登录页）
   ↓
3. 用户登录成功
   ↓
4. 调用 /api/auth/profile 获取菜单
   ↓
5. 根据菜单数据生成路由配置
   ↓
6. router.addRoute() 动态添加
   ↓
7. 重定向到首页或目标页
   ↓
8. 侧边栏根据菜单渲染
```

## 🔗 与其他章节的关系

```
第一章：登录功能
    ↓
第二章：用户管理 (User)
    ↓
第三章：角色管理 (Role)
    ↓
第四章：权限管理 (Permission)
    ↓
第五章：菜单管理 (Menu)  ← 【当前章节】
    ↓
第六章：系统功能 (Dict)
    ↓
最终项目：完整的 RBAC 系统
  • 用户-角色-权限-菜单 完整关联
  • 基于角色的菜单过滤
  • 动态路由 + 权限控制
```

## 🎯 下一步

完成本章后，你已经掌握了：
- ✅ 菜单树形结构设计
- ✅ 动态路由生成
- ✅ 侧边栏菜单渲染
- ✅ 菜单与权限的协同

**继续学习**：
- **第六章：系统功能** - 实现字典管理等系统基础功能
- **最终项目** - 整合所有模块，实现完整的 RBAC 权限控制系统

## 📝 常见问题

### 1. 菜单和权限是什么关系？
- **菜单**：控制用户能看到哪些页面（前端路由）
- **权限**：控制用户能进行哪些操作（按钮、API）
- **关联**：通过菜单编码（code）与权限编码对应

### 2. 如何实现多级菜单？
```javascript
// 递归组件 MenuItem.vue
<el-sub-menu v-if="menu.children && menu.children.length">
  <template #title>{{ menu.name }}</template>
  <menu-item
    v-for="child in menu.children"
    :key="child.id"
    :menu="child"
  />
</el-sub-menu>
```

### 3. 动态路由和静态路由的区别？
- **静态路由**：写死在代码中，所有用户都能访问
- **动态路由**：根据用户权限动态生成，不同用户看到的路由不同

### 4. 如何防止用户直接访问未授权的路由？
```javascript
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const hasRoute = authStore.menus.some(menu => menu.path === to.path);
  
  if (!hasRoute && to.path !== '/login') {
    return next('/403');  // 无权限访问
  }
  
  next();
});
```

## 📖 参考资料

- [Vue Router 动态路由](https://router.vuejs.org/zh/guide/advanced/dynamic-routing.html)
- [Element Plus Menu 组件](https://element-plus.org/zh-CN/component/menu.html)
- [Element Plus Icon 图标](https://element-plus.org/zh-CN/component/icon.html)

---

**🎉 恭喜！完成本章后，你已经掌握了动态菜单和路由的核心技术！**
