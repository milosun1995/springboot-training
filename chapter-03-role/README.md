# 第三章：角色管理

## 📖 章节说明

本章实现了完整的角色管理功能，是 RBAC（基于角色的访问控制）系统的核心模块之一。通过本章学习，你将掌握如何设计和实现角色管理，为后续的权限分配打下基础。

**与前一章的关系**：
- 第二章实现了用户管理（User）
- 本章实现角色管理（Role）
- 下一章将实现角色与用户的关联（User-Role）

## 🎯 学习目标

通过本章学习，你将掌握：

### 后端技能
- ✅ 角色 CRUD 基础操作
- ✅ 角色编码（code）的唯一性约束
- ✅ 角色状态管理（启用/禁用）
- ✅ 动态条件查询（Specification）
- ✅ 业务异常处理

### 前端技能
- ✅ 列表展示与分页
- ✅ 表单验证（编码唯一性）
- ✅ 状态切换交互
- ✅ 查询条件联动

### 设计思想
- ✅ 角色编码的设计规范（如：ADMIN、OPS、GUEST）
- ✅ 角色与用户的关系（一对多）
- ✅ 角色的状态管理

## 📁 项目结构

```
chapter-03-role/
├── backend/                  # 后端项目
│   ├── src/main/java/com/training/admin/
│   │   ├── controller/
│   │   │   └── RoleController.java       # 角色管理 API
│   │   ├── service/
│   │   │   └── RoleService.java          # 角色业务逻辑
│   │   ├── repository/
│   │   │   └── RoleRepository.java       # 角色数据访问
│   │   ├── entity/
│   │   │   └── Role.java                 # 角色实体
│   │   ├── dto/
│   │   │   ├── RoleCreateDTO.java       # 新增角色 DTO
│   │   │   ├── RoleUpdateDTO.java       # 更新角色 DTO
│   │   │   └── RoleQueryDTO.java        # 查询条件 DTO
│   │   └── vo/
│   │       └── RoleVO.java               # 角色视图对象
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/
│   │   │   └── role.js                   # 角色 API 调用
│   │   └── views/
│   │       ├── Login.vue                 # 登录页
│   │       └── RoleList.vue              # 角色列表页
│   └── package.json
├── database/                # 数据库脚本
│   ├── schema.sql          # 角色表结构
│   └── init-data.sql       # 初始角色数据
└── README.md               # 本文件
```

## 💡 核心知识点

### 1. 角色表设计

```sql
CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码（如：ADMIN）',
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称（如：系统管理员）',
  description VARCHAR(255) COMMENT '角色描述',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**设计要点**：
- `code`：角色编码，通常为大写英文（如 ADMIN、OPS、GUEST）
- `name`：角色名称，用于显示给用户
- `description`：角色描述，说明该角色的职责范围
- `status`：角色状态，可以临时禁用某个角色

### 2. 角色编码规范

| 角色编码 | 角色名称 | 说明 |
|---------|---------|------|
| ADMIN | 系统管理员 | 拥有所有权限 |
| OPS | 运维人员 | 拥有部分管理权限 |
| GUEST | 访客 | 只有查看权限 |

### 3. JPA Specification 动态查询

```java
Specification<Role> spec = (root, cq, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    
    // 关键字查询：名称/编码/描述
    if (StringUtils.hasText(query.getKeyword())) {
        String like = "%" + query.getKeyword().trim() + "%";
        predicates.add(
            cb.or(
                cb.like(root.get("name"), like),
                cb.like(root.get("code"), like),
                cb.like(root.get("description"), like)
            )
        );
    }
    
    // 状态查询
    if (query.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), query.getStatus()));
    }
    
    // 组合所有条件（AND 连接）
    return cb.and(predicates.toArray(new Predicate[0]));
};
```

**关键点**：
- 使用 `List<Predicate>` 收集查询条件
- 使用 `cb.and()` 组合多个条件（AND 逻辑）
- 使用 `cb.or()` 实现多字段模糊查询

## ✨ 功能特性

### 后端功能
- ✅ 角色列表查询（分页 + 条件过滤）
- ✅ 新增角色（编码唯一性验证）
- ✅ 编辑角色（编码不可修改）
- ✅ 删除角色（软删除或物理删除）
- ✅ 启用/禁用角色

### 前端功能
- ✅ 角色列表展示（表格 + 分页）
- ✅ 关键字搜索（名称/编码/描述）
- ✅ 状态筛选（启用/禁用）
- ✅ 新增/编辑对话框
- ✅ 表单验证（必填项、编码格式）
- ✅ 状态切换按钮
- ✅ 删除确认

## 🚀 快速开始

### 1. 数据库准备（使用 H2）

本章使用 H2 内存数据库，无需手动创建数据库。应用启动时会自动：
- 创建表结构（根据 `schema.sql`）
- 初始化数据（通过 `DataInitializer.java`）

**初始角色数据**：
```java
// ADMIN - 系统管理员
// OPS - 运维人员  
// GUEST - 访客
```

### 2. 启动后端

```bash
cd chapter-03-role/backend

# 启动项目（Maven Wrapper）
./mvnw spring-boot:run

# 或者使用系统 Maven
mvn spring-boot:run
```

**后端地址**：http://localhost:8080

### 3. 启动前端

```bash
cd chapter-03-role/frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

**前端地址**：http://localhost:5173

### 4. 测试功能

#### 登录
- 访问：http://localhost:5173
- 用户名：`admin`
- 密码：`password123`

#### 测试场景
1. **查看角色列表**：登录后自动跳转到角色列表
2. **搜索角色**：输入关键字（如 "管理"）测试模糊查询
3. **新增角色**：
   - 编码：`DEVELOPER`
   - 名称：`开发人员`
   - 描述：`负责系统开发`
4. **编辑角色**：修改已有角色的名称或描述
5. **禁用角色**：点击"禁用"按钮，观察状态变化
6. **删除角色**：删除测试创建的角色

## 🧪 API 测试

### 1. 获取角色列表
```bash
curl -X GET "http://localhost:8080/api/roles?page=0&size=10&keyword=管理" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. 新增角色
```bash
curl -X POST "http://localhost:8080/api/roles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "code": "DEVELOPER",
    "name": "开发人员",
    "description": "负责系统开发",
    "status": 1
  }'
```

### 3. 更新角色
```bash
curl -X PUT "http://localhost:8080/api/roles/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "超级管理员",
    "description": "拥有所有权限"
  }'
```

### 4. 切换角色状态
```bash
curl -X POST "http://localhost:8080/api/roles/1/toggle" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 📚 核心代码解析

### 1. 角色实体（Role.java）

```java
@Entity
@Table(name = "sys_role")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50, unique = true)
    private String code;  // 角色编码：ADMIN、OPS 等
    
    @Column(nullable = false, length = 50, unique = true)
    private String name;  // 角色名称：系统管理员、运维人员
    
    @Column(length = 255)
    private String description;  // 角色描述
    
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;  // 0-禁用 1-启用
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
```

### 2. 角色服务（RoleService.java）

```java
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    
    // 分页查询 + 条件过滤
    public Page<RoleVO> page(RoleQueryDTO query) {
        Pageable pageable = PageRequest.of(
            query.getPage(), 
            query.getSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        Specification<Role> spec = buildSpecification(query);
        return roleRepository.findAll(spec, pageable).map(this::toVO);
    }
    
    // 新增角色（验证编码唯一性）
    public RoleVO create(RoleCreateDTO dto) {
        if (roleRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "角色编码已存在");
        }
        
        Role role = new Role();
        role.setCode(dto.getCode().toUpperCase());  // 编码转大写
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        
        return toVO(roleRepository.save(role));
    }
}
```

## 🎓 学习要点

### 1. 唯一性约束

**数据库层面**：
```sql
UNIQUE KEY uk_role_code (code),
UNIQUE KEY uk_role_name (name)
```

**业务层面**：
```java
if (roleRepository.existsByCode(dto.getCode())) {
    throw new BusinessException(400, "角色编码已存在");
}
```

### 2. 编码规范

- 使用大写英文：`ADMIN`、`OPS`
- 简洁易懂：避免使用 `SYS_ADMIN_ROLE_001` 这类冗长编码
- 语义化：编码应能直观体现角色含义

### 3. 状态管理

角色的启用/禁用不会删除数据，而是修改状态字段：
```java
public RoleVO toggleStatus(Long id) {
    Role role = findById(id);
    role.setStatus(role.getStatus() == 1 ? 0 : 1);
    role.setUpdateTime(LocalDateTime.now());
    return toVO(roleRepository.save(role));
}
```

## 🔗 与其他章节的关系

```
第一章：登录功能
    ↓
第二章：用户管理 (User)
    ↓
第三章：角色管理 (Role)  ← 【当前章节】
    ↓
第四章：权限管理 (Permission) + 用户-角色关联
    ↓
第五章：菜单管理 (Menu) + 角色-权限-菜单关联
    ↓
第六章：系统功能 (Dict)
    ↓
最终项目：完整的 RBAC 系统
```

## 🎯 下一步

完成本章后，你已经掌握了：
- ✅ 角色的 CRUD 操作
- ✅ 动态查询的实现
- ✅ 唯一性约束的处理
- ✅ 状态管理的实现

**继续学习**：
- **第四章：权限管理** - 实现权限的增删改查，并建立用户-角色-权限的关联关系

## 📝 常见问题

### 1. 角色编码应该如何设计？
- 使用大写英文，简洁易懂
- 建议格式：`ADMIN`、`OPS`、`GUEST`
- 避免使用数字和特殊字符

### 2. 角色和用户的关系是什么？
- 一个用户可以有多个角色（多对多）
- 一个角色可以分配给多个用户
- 通过中间表 `sys_user_role` 建立关联

### 3. 为什么要有角色状态？
- 临时禁用某个角色，而不删除数据
- 禁用后，该角色的用户无法使用该角色的权限
- 可以随时重新启用

### 4. 如何防止删除正在使用的角色？
```java
public void delete(Long id) {
    long count = userRoleRepository.countByRoleId(id);
    if (count > 0) {
        throw new BusinessException(400, "该角色下还有用户，无法删除");
    }
    roleRepository.deleteById(id);
}
```

## 📖 参考资料

- [Spring Data JPA Specification](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications)
- [RBAC 权限模型](https://en.wikipedia.org/wiki/Role-based_access_control)
- [RESTful API 设计规范](https://restfulapi.net/)

---

**🎉 恭喜！完成本章后，你已经掌握了角色管理的核心功能！**
