# 第四章：权限管理

## 📖 章节说明

本章实现了完整的权限管理功能，是 RBAC 系统的核心。权限采用树形结构设计，支持三种权限类型（menu/button/api），为后续的细粒度权限控制奠定基础。

**与前几章的关系**：
- 第二章：用户管理（User）
- 第三章：角色管理（Role）
- 第四章：权限管理（Permission）← **当前章节**
- 下一章：将实现用户-角色-权限的完整关联

## 🎯 学习目标

通过本章学习，你将掌握：

### 后端技能
- ✅ 树形数据结构的设计与实现
- ✅ 权限的三种类型（menu、button、api）
- ✅ 递归查询构建权限树
- ✅ 父子关系的维护
- ✅ 树形数据的 CRUD 操作

### 前端技能
- ✅ 树形表格的展示（el-table tree-props）
- ✅ 父级权限选择（el-tree-select）
- ✅ 层级关系的可视化
- ✅ 防止循环引用
- ✅ 树形数据的交互优化

### 设计思想
- ✅ 权限编码规范（sys:user:create）
- ✅ 三层权限控制模型
- ✅ 权限树的层级设计
- ✅ 权限与路由、按钮、API 的映射关系

## 📁 项目结构

```
chapter-04-permission/
├── backend/
│   ├── src/main/java/com/training/admin/
│   │   ├── controller/
│   │   │   └── PermissionController.java  # 权限管理 API
│   │   ├── service/
│   │   │   └── PermissionService.java     # 权限业务逻辑（树形构建）
│   │   ├── repository/
│   │   │   └── PermissionRepository.java  # 权限数据访问
│   │   ├── entity/
│   │   │   └── Permission.java            # 权限实体（支持父子关系）
│   │   ├── dto/
│   │   │   ├── PermissionCreateDTO.java
│   │   │   ├── PermissionUpdateDTO.java
│   │   │   └── PermissionQueryDTO.java
│   │   └── vo/
│   │       └── PermissionVO.java          # 权限视图（含 children）
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── permission.js              # 权限 API
│   │   └── views/
│   │       ├── Login.vue
│   │       └── PermissionList.vue         # 权限列表（树形表格）
│   └── package.json
├── database/
│   ├── schema.sql                         # 权限表结构
│   └── init-data.sql                      # 初始权限数据（树形）
└── README.md
```

## 💡 核心知识点

### 1. 权限表设计（树形结构）

```sql
CREATE TABLE sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT DEFAULT NULL COMMENT '父权限ID（NULL=根节点）',
  code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码（如：sys:user:create）',
  name VARCHAR(50) NOT NULL COMMENT '权限名称',
  type VARCHAR(20) COMMENT '权限类型：menu/button/api',
  path VARCHAR(255) COMMENT '路由路径（menu类型）',
  method VARCHAR(10) COMMENT 'HTTP方法（api类型）',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  description VARCHAR(255) COMMENT '描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_parent (parent_id)
);
```

**设计要点**：
- `parent_id`：父权限 ID，NULL 表示根节点
- `code`：权限编码，采用冒号分隔的层级结构
- `type`：权限类型，支持三种类型
- `sort`：同级权限的排序字段

### 2. 权限编码规范

```
格式：模块:资源:操作
示例：
  sys:user           - 用户管理菜单（menu）
  sys:user:create    - 新增用户按钮（button）
  sys:user:add       - 新增用户 API（api）
  sys:user:delete    - 删除用户按钮（button）
  sys:user:del       - 删除用户 API（api）
```

### 3. 三种权限类型

| 类型 | 说明 | 示例 | 控制内容 |
|-----|------|------|---------|
| **menu** | 菜单权限 | `sys:user` | 控制前端路由和侧边栏菜单 |
| **button** | 按钮权限 | `sys:user:create` | 控制页面内的操作按钮 |
| **api** | 接口权限 | `sys:user:add` | 控制后端 API 访问 |

### 4. 权限树结构示例

```
系统管理 (sys:manage) - menu
├── 用户管理 (sys:user) - menu
│   ├── 查看用户 (sys:user:view) - button
│   ├── 新增用户 (sys:user:create) - button
│   ├── 编辑用户 (sys:user:update) - button
│   ├── 删除用户 (sys:user:delete) - button
│   ├── 用户列表 API (sys:user:list) - api
│   ├── 新增用户 API (sys:user:add) - api
│   ├── 编辑用户 API (sys:user:edit) - api
│   └── 删除用户 API (sys:user:del) - api
├── 角色管理 (sys:role) - menu
│   └── ...
└── 权限管理 (sys:permission) - menu
    └── ...
```

### 5. 递归构建权限树

```java
public List<PermissionVO> tree(PermissionQueryDTO query) {
    // 1. 查询所有符合条件的权限
    List<Permission> all = permissionRepository.findAll(spec);
    
    // 2. 转换为 VO
    List<PermissionVO> voList = all.stream()
        .map(this::toVO)
        .collect(Collectors.toList());
    
    // 3. 构建树形结构
    return buildTree(voList, null);  // 从根节点开始
}

private List<PermissionVO> buildTree(List<PermissionVO> all, Long parentId) {
    return all.stream()
        .filter(vo -> Objects.equals(vo.getParentId(), parentId))
        .peek(vo -> vo.setChildren(buildTree(all, vo.getId())))
        .sorted(Comparator.comparing(PermissionVO::getSort))
        .collect(Collectors.toList());
}
```

## ✨ 功能特性

### 后端功能
- ✅ 权限树查询（递归构建）
- ✅ 新增权限（验证父级权限存在）
- ✅ 编辑权限（防止循环引用）
- ✅ 删除权限（检查是否有子权限）
- ✅ 启用/禁用权限

### 前端功能
- ✅ 树形表格展示（展开/折叠）
- ✅ 父级权限选择（el-tree-select）
- ✅ 防止选择自己或子节点作为父级
- ✅ 层级关系说明（el-alert）
- ✅ 权限类型标签展示
- ✅ 排序字段输入
- ✅ 路径和方法配置（根据类型动态显示）

## 🚀 快速开始

### 1. 数据库准备（H2）

应用启动时自动初始化权限树：

```java
// DataInitializer.java
Permission sys = savePerm(null, "sys:manage", "系统管理", "menu", null, null, 1);

Permission userMenu = savePerm(sys.getId(), "sys:user", "用户管理", "menu", "/users", null, 10);
Permission userView = savePerm(userMenu.getId(), "sys:user:view", "查看用户", "button", null, null, 11);
Permission userCreate = savePerm(userMenu.getId(), "sys:user:create", "新增用户", "button", null, null, 12);
// ... 更多权限
```

### 2. 启动后端

```bash
cd chapter-04-permission/backend
./mvnw spring-boot:run
```

### 3. 启动前端

```bash
cd chapter-04-permission/frontend
npm install
npm run dev
```

### 4. 测试功能

#### 登录
- 用户名：`admin`
- 密码：`password123`

#### 测试场景
1. **查看权限树**：展开/折叠节点，观察层级关系
2. **新增权限**：
   - 选择父级权限（使用树形选择器）
   - 输入权限编码（遵循命名规范）
   - 选择权限类型（menu/button/api）
   - 配置路径或方法（根据类型）
3. **编辑权限**：
   - 父级选择器会过滤掉自己和子节点
   - 防止循环引用
4. **删除权限**：
   - 有子权限的节点不能删除
   - 需要先删除子节点

## 🧪 API 测试

### 1. 获取权限树
```bash
curl -X GET "http://localhost:8080/api/permissions/tree?status=1" \
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
      "type": "menu",
      "children": [
        {
          "id": 10,
          "parentId": 1,
          "code": "sys:user",
          "name": "用户管理",
          "type": "menu",
          "path": "/users",
          "children": [
            {
              "id": 11,
              "parentId": 10,
              "code": "sys:user:view",
              "name": "查看用户",
              "type": "button"
            }
          ]
        }
      ]
    }
  ]
}
```

### 2. 新增权限
```bash
curl -X POST "http://localhost:8080/api/permissions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "parentId": 10,
    "code": "sys:user:export",
    "name": "导出用户",
    "type": "button",
    "sort": 15
  }'
```

## 📚 核心代码解析

### 1. 权限实体（Permission.java）

```java
@Entity
@Table(name = "sys_permission")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "parent_id")
    private Long parentId;  // 父权限ID，NULL=根节点
    
    @Column(nullable = false, length = 100, unique = true)
    private String code;  // 权限编码：sys:user:create
    
    @Column(nullable = false, length = 50)
    private String name;  // 权限名称：新增用户
    
    @Column(length = 20)
    private String type;  // 类型：menu/button/api
    
    @Column(length = 255)
    private String path;  // 路由路径（menu类型）
    
    @Column(length = 10)
    private String method;  // HTTP方法（api类型）
    
    @Column(columnDefinition = "int default 0")
    private Integer sort;  // 排序
    
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;  // 状态
    
    @Column(length = 255)
    private String description;  // 描述
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
```

### 2. 权限服务（PermissionService.java）

```java
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    // 构建权限树
    public List<PermissionVO> tree(PermissionQueryDTO query) {
        List<Permission> all = permissionRepository.findAll(buildSpec(query));
        List<PermissionVO> voList = all.stream()
            .map(this::toVO)
            .collect(Collectors.toList());
        
        // 递归构建树形结构
        return buildTree(voList, null);
    }
    
    // 递归构建树（核心方法）
    private List<PermissionVO> buildTree(List<PermissionVO> all, Long parentId) {
        return all.stream()
            .filter(vo -> Objects.equals(vo.getParentId(), parentId))
            .peek(vo -> {
                List<PermissionVO> children = buildTree(all, vo.getId());
                vo.setChildren(children);
            })
            .sorted(Comparator.comparing(PermissionVO::getSort))
            .collect(Collectors.toList());
    }
    
    // 新增权限（验证父级）
    public PermissionVO create(PermissionCreateDTO dto) {
        // 验证编码唯一性
        if (permissionRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "权限编码已存在");
        }
        
        // 验证父级权限存在
        if (dto.getParentId() != null) {
            if (!permissionRepository.existsById(dto.getParentId())) {
                throw new BusinessException(400, "父级权限不存在");
            }
        }
        
        Permission perm = new Permission();
        // ... 设置属性
        return toVO(permissionRepository.save(perm));
    }
    
    // 删除权限（检查子权限）
    public void delete(Long id) {
        long childCount = permissionRepository.countByParentId(id);
        if (childCount > 0) {
            throw new BusinessException(400, "存在子权限，无法删除");
        }
        permissionRepository.deleteById(id);
    }
}
```

### 3. 前端树形选择器（PermissionList.vue）

```vue
<template>
  <el-form-item label="父级权限">
    <el-tree-select
      v-model="form.parentId"
      :data="parentOptions"
      :render-after-expand="false"
      :props="{ value: 'id', label: 'name', children: 'children' }"
      placeholder="选择父级权限（留空=根节点）"
      clearable
      filterable
      check-strictly
    />
  </el-form-item>
</template>

<script>
// 过滤父级选项（防止循环引用）
const parentOptions = computed(() => {
  if (!isEdit.value || !form.value.id) {
    return permissionTree.value;
  }
  
  // 编辑模式：过滤掉自己和子节点
  return filterTree(permissionTree.value, form.value.id);
});

// 递归过滤树（移除指定节点及其子节点）
function filterTree(tree, excludeId) {
  return tree
    .filter(node => node.id !== excludeId)
    .map(node => ({
      ...node,
      children: node.children ? filterTree(node.children, excludeId) : []
    }));
}
</script>
```

## 🎓 学习要点

### 1. 权限编码设计原则

✅ **好的编码**：
- `sys:user` - 清晰、简洁
- `sys:user:create` - 层级分明
- `sys:role:assign` - 语义明确

❌ **不好的编码**：
- `USER_MANAGEMENT_001` - 过于冗长
- `sys.user.create` - 使用点分隔（推荐冒号）
- `createUser` - 驼峰命名（推荐小写+冒号）

### 2. 防止循环引用

在编辑权限时，必须过滤掉：
1. 自己（不能选自己作为父级）
2. 自己的所有子孙节点（不能选后代作为父级）

```java
// 编辑时验证
if (dto.getParentId() != null && dto.getParentId().equals(id)) {
    throw new BusinessException(400, "不能选择自己作为父级");
}

// 检查是否为子孙节点
if (isDescendant(dto.getParentId(), id)) {
    throw new BusinessException(400, "不能选择子孙节点作为父级");
}
```

### 3. 树形数据的性能优化

对于大量权限数据，推荐：
- 使用 `@BatchSize` 优化子节点加载
- 缓存权限树（Redis）
- 只加载启用状态的权限
- 按需展开（懒加载）

## 🔗 与其他章节的关系

```
第一章：登录功能
    ↓
第二章：用户管理 (User)
    ↓
第三章：角色管理 (Role)
    ↓
第四章：权限管理 (Permission)  ← 【当前章节】
    ↓
第五章：菜单管理 (Menu) + 用户-角色-权限关联
    ↓
第六章：系统功能 (Dict)
    ↓
最终项目：权限控制落地（路由守卫 + 按钮控制 + API 保护）
```

## 🎯 下一步

完成本章后，你已经掌握了：
- ✅ 树形数据结构的设计
- ✅ 递归查询构建树
- ✅ 三种权限类型的应用
- ✅ 防止循环引用的方法

**继续学习**：
- **第五章：菜单管理** - 实现动态菜单，并建立角色-权限-菜单的完整关联

## 📝 常见问题

### 1. 权限编码和权限名称有什么区别？
- **编码（code）**：用于程序判断，如 `sys:user:create`
- **名称（name）**：用于界面展示，如 "新增用户"

### 2. menu、button、api 三种类型如何使用？
- **menu**：控制前端路由和侧边栏菜单显示
- **button**：控制页面内按钮显示/隐藏（v-perm 指令）
- **api**：控制后端接口访问（@PreAuthorize 注解）

### 3. 为什么删除父节点前要检查子节点？
- 避免数据孤岛（子节点找不到父节点）
- 保持树形结构的完整性
- 建议先删除子节点，再删除父节点

### 4. 如何优化权限树的查询性能？
```java
// 1. 使用缓存
@Cacheable(value = "permissionTree", key = "#status")
public List<PermissionVO> tree(Integer status) {
    // ...
}

// 2. 只查询启用的权限
Specification<Permission> spec = (root, cq, cb) -> 
    cb.equal(root.get("status"), 1);

// 3. 限制树的深度（可选）
if (level > MAX_LEVEL) {
    return Collections.emptyList();
}
```

## 📖 参考资料

- [树形数据结构设计](https://en.wikipedia.org/wiki/Tree_(data_structure))
- [RBAC 权限模型](https://en.wikipedia.org/wiki/Role-based_access_control)
- [Element Plus Tree Select](https://element-plus.org/zh-CN/component/tree-select.html)

---

**🎉 恭喜！完成本章后，你已经掌握了权限管理的核心设计！**
