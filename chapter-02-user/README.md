# 第二章：用户管理

## 📖 章节说明

本章实现了完整的用户管理功能，是在第一章登录功能的基础上，实现用户的增删改查（CRUD）操作。通过本章学习，你将掌握企业级应用中最常见的数据管理模式。

**与前一章的关系**：
- 第一章：实现了登录功能（JWT 认证）
- 第二章：实现用户管理（CRUD）← **当前章节**
- 下一章：将实现角色管理，为 RBAC 权限系统做准备

## 🎯 学习目标

通过本章学习，你将掌握：

### 后端技能
- ✅ RESTful API 设计规范
- ✅ CRUD 完整实现（增删改查）
- ✅ 分页查询（Page、Pageable）
- ✅ 动态条件查询（JPA Specification）
- ✅ DTO 模式（数据传输对象）
- ✅ VO 模式（视图对象）
- ✅ Bean Validation（参数校验）
- ✅ 实体映射（Entity ↔ DTO ↔ VO）

### 前端技能
- ✅ Element Plus Table 组件
- ✅ 分页组件（el-pagination）
- ✅ 表单验证（el-form rules）
- ✅ 对话框交互（el-dialog）
- ✅ 条件查询联动
- ✅ 状态切换（启用/禁用）
- ✅ 删除确认（MessageBox）

### 设计思想
- ✅ 分层架构（Controller-Service-Repository）
- ✅ DTO/VO 模式的作用
- ✅ 参数校验的重要性
- ✅ 前端组件化设计

## 📁 项目结构

```
chapter-02-user/
├── backend/
│   ├── src/main/java/com/training/admin/
│   │   ├── config/                   # 配置类
│   │   │   ├── CorsConfig.java       # 跨域配置
│   │   │   └── DataInitializer.java  # 数据初始化
│   │   ├── controller/               # 控制器层
│   │   │   ├── AuthController.java   # 认证接口
│   │   │   └── UserController.java   # 用户管理接口
│   │   ├── service/                  # 业务层
│   │   │   ├── AuthService.java      # 认证服务
│   │   │   └── UserService.java      # 用户服务
│   │   ├── repository/               # 数据访问层
│   │   │   └── UserRepository.java   # 用户数据访问
│   │   ├── entity/                   # 实体类
│   │   │   └── User.java             # 用户实体
│   │   ├── dto/                      # 数据传输对象
│   │   │   ├── LoginDTO.java         # 登录 DTO
│   │   │   ├── UserCreateDTO.java    # 新增用户 DTO
│   │   │   ├── UserUpdateDTO.java    # 更新用户 DTO
│   │   │   └── UserQueryDTO.java     # 查询条件 DTO
│   │   ├── vo/                       # 视图对象
│   │   │   ├── LoginVO.java          # 登录响应 VO
│   │   │   └── UserVO.java           # 用户视图 VO
│   │   ├── common/
│   │   │   └── Result.java           # 统一响应
│   │   ├── exception/
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   └── util/
│   │       ├── JwtUtil.java
│   │       └── PasswordUtil.java
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   ├── auth.js               # 认证 API
│   │   │   └── user.js               # 用户管理 API
│   │   ├── router/
│   │   │   └── index.js              # 路由配置
│   │   ├── store/
│   │   │   └── auth.js               # 认证状态
│   │   ├── utils/
│   │   │   ├── auth.js               # Token 管理
│   │   │   └── request.js            # Axios 封装
│   │   ├── views/
│   │   │   ├── Login.vue             # 登录页
│   │   │   └── UserList.vue          # 用户列表页
│   │   ├── App.vue
│   │   └── main.js
│   └── package.json
│
├── database/
│   └── schema.sql                    # 表结构（参考）
│
└── README.md                         # 本文件
```

## 💡 核心知识点

### 1. RESTful API 设计

| HTTP 方法 | 路径 | 说明 | 示例 |
|-----------|------|------|------|
| **GET** | `/api/users` | 查询用户列表（分页） | `?page=0&size=10&keyword=admin` |
| **POST** | `/api/users` | 新增用户 | Body: `{"username":"test",...}` |
| **PUT** | `/api/users/{id}` | 更新用户 | Body: `{"nickname":"测试"}` |
| **DELETE** | `/api/users/{id}` | 删除用户 | - |
| **POST** | `/api/users/{id}/toggle` | 切换用户状态 | - |

**设计原则**：
- 使用名词复数（users、roles）
- 使用 HTTP 方法表达操作（GET=查、POST=增、PUT=改、DELETE=删）
- ID 放在路径中（`/users/1`）
- 查询参数放在 query string（`?keyword=admin`）

### 2. DTO、Entity、VO 的区别

```
前端请求
    ↓
【DTO】数据传输对象（接收请求参数）
    ↓
Controller 接收并验证
    ↓
Service 处理业务逻辑
    ↓
【Entity】实体对象（与数据库表对应）
    ↓
Repository 数据库操作
    ↓
Service 转换为 VO
    ↓
【VO】视图对象（返回给前端）
    ↓
前端响应
```

**举例说明**：
```java
// DTO - 接收前端数据
public class UserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;  // 明文密码
}

// Entity - 数据库表映射
@Entity
@Table(name = "sys_user")
public class User {
    private Long id;
    private String username;
    private String password;  // 加密后的密码
    private LocalDateTime createTime;
}

// VO - 返回给前端
public class UserVO {
    private Long id;
    private String username;
    // 不包含密码字段（安全考虑）
    private String nickname;
    private String createTime;  // 格式化后的时间
}
```

### 3. 分页查询

```java
// 后端
public Page<UserVO> pageUsers(UserQueryDTO queryDTO) {
    // 1. 创建分页对象
    Pageable pageable = PageRequest.of(
        queryDTO.getPage(),      // 页码（从 0 开始）
        queryDTO.getSize(),      // 每页条数
        Sort.by(Sort.Direction.DESC, "createTime")  // 排序
    );
    
    // 2. 查询数据
    Page<User> page = userRepository.findAll(pageable);
    
    // 3. 转换为 VO
    return page.map(this::toVO);
}
```

```javascript
// 前端
const query = ref({
  page: 0,      // 当前页（从 0 开始）
  size: 10,     // 每页条数
  keyword: '',  // 关键字
  status: null  // 状态
});

const total = ref(0);  // 总条数

async function loadData() {
  const res = await getUsers(query.value);
  list.value = res.data.content;  // 数据列表
  total.value = res.data.totalElements;  // 总条数
}
```

### 4. 动态条件查询（JPA Specification）

```java
public Page<UserVO> pageUsers(UserQueryDTO queryDTO) {
    Specification<User> spec = (root, cq, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        
        // 关键字查询：用户名/昵称/邮箱
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String like = "%" + queryDTO.getKeyword().trim() + "%";
            predicates.add(
                cb.or(
                    cb.like(root.get("username"), like),
                    cb.like(root.get("nickname"), like),
                    cb.like(root.get("email"), like)
                )
            );
        }
        
        // 状态查询
        if (queryDTO.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
        }
        
        // 组合所有条件（AND 连接）
        return cb.and(predicates.toArray(new Predicate[0]));
    };
    
    return userRepository.findAll(spec, pageable).map(this::toVO);
}
```

**关键点**：
- ✅ 使用 `List<Predicate>` 收集条件
- ✅ 使用 `cb.and()` 组合条件（AND）
- ✅ 使用 `cb.or()` 实现多字段模糊查询
- ❌ 不要使用 `cb.conjunction().getExpressions().add()`（会导致条件失效）

### 5. Bean Validation 参数校验

```java
// DTO 中定义校验规则
public class UserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为 3-50 个字符")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少 6 个字符")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}

// Controller 中使用 @Validated 触发校验
@PostMapping
public Result<UserVO> create(@Validated @RequestBody UserCreateDTO dto) {
    return Result.success("创建成功", userService.create(dto));
}

// 全局异常处理器捕获校验异常
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("; "));
    return Result.error(400, message);
}
```

## ✨ 功能特性

### 后端功能
- ✅ 用户列表查询（分页 + 条件过滤）
- ✅ 新增用户（密码加密、唯一性验证）
- ✅ 编辑用户（密码可选更新）
- ✅ 删除用户（物理删除）
- ✅ 启用/禁用用户（状态切换）
- ✅ 参数校验（Bean Validation）
- ✅ 唯一性验证（用户名）

### 前端功能
- ✅ 用户列表展示（表格 + 分页）
- ✅ 关键字搜索（用户名/昵称/邮箱）
- ✅ 状态筛选（启用/禁用）
- ✅ 新增/编辑对话框
- ✅ 表单验证（必填项、格式验证）
- ✅ 状态切换按钮
- ✅ 删除确认提示
- ✅ 操作成功/失败提示

## 🚀 快速开始

### 1. 数据库准备（H2）

应用启动时自动初始化用户数据：

```java
// DataInitializer.java
User admin = new User();
admin.setUsername("admin");
admin.setPassword(passwordUtil.encode("password123"));
admin.setNickname("系统管理员");
admin.setEmail("admin@example.com");
admin.setStatus(1);
userRepository.save(admin);
```

### 2. 启动后端

```bash
cd chapter-02-user/backend
./mvnw spring-boot:run
```

**后端地址**：http://localhost:8080

### 3. 启动前端

```bash
cd chapter-02-user/frontend
npm install
npm run dev
```

**前端地址**：http://localhost:5173

### 4. 测试功能

#### 登录
- 用户名：`admin`
- 密码：`password123`

#### 测试场景
1. **查看用户列表**：
   - 观察分页功能
   - 尝试切换页码

2. **搜索用户**：
   - 输入关键字（如 "admin"）
   - 选择状态（启用/禁用）
   - 点击查询按钮

3. **新增用户**：
   ```
   用户名：test01
   密码：123456
   昵称：测试用户
   邮箱：test@example.com
   手机：13800138000
   ```

4. **编辑用户**：
   - 修改昵称
   - 修改邮箱
   - 不修改密码（留空）

5. **禁用用户**：
   - 点击"禁用"按钮
   - 观察状态变化
   - 尝试用该用户登录（应提示"用户已被禁用"）

6. **删除用户**：
   - 点击"删除"按钮
   - 确认删除提示
   - 用户从列表中消失

## 🧪 API 测试

### 1. 查询用户列表
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10&keyword=admin" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "系统管理员",
        "email": "admin@example.com",
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 2. 新增用户
```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "test01",
    "password": "123456",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000"
  }'
```

### 3. 更新用户
```bash
curl -X PUT "http://localhost:8080/api/users/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "nickname": "超级管理员",
    "email": "admin@newdomain.com"
  }'
```

### 4. 删除用户
```bash
curl -X DELETE "http://localhost:8080/api/users/2" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. 切换用户状态
```bash
curl -X POST "http://localhost:8080/api/users/1/toggle" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 📚 核心代码解析

### 1. 用户服务（UserService.java）

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    
    // 分页查询用户
    public Page<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
            queryDTO.getPage(), 
            queryDTO.getSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字查询
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String like = "%" + queryDTO.getKeyword().trim() + "%";
                predicates.add(
                    cb.or(
                        cb.like(root.get("username"), like),
                        cb.like(root.get("nickname"), like),
                        cb.like(root.get("email"), like)
                    )
                );
            }
            
            // 状态查询
            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return userRepository.findAll(spec, pageable).map(this::toVO);
    }
    
    // 新增用户
    public UserVO create(UserCreateDTO dto) {
        // 验证用户名唯一性
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordUtil.encode(dto.getPassword()));  // 加密密码
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);  // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        return toVO(userRepository.save(user));
    }
    
    // 更新用户
    public UserVO update(UserUpdateDTO dto) {
        User user = userRepository.findById(dto.getId())
            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        // 更新非空字段
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        
        // 更新密码（如果提供）
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordUtil.encode(dto.getPassword()));
        }
        
        user.setUpdateTime(LocalDateTime.now());
        return toVO(userRepository.save(user));
    }
    
    // 删除用户
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(404, "用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    // 切换用户状态
    public UserVO toggleStatus(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        user.setUpdateTime(LocalDateTime.now());
        return toVO(userRepository.save(user));
    }
    
    // Entity 转 VO
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ));
        return vo;
    }
}
```

### 2. 用户控制器（UserController.java）

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    // 分页查询
    @GetMapping
    public Result<Page<UserVO>> page(@Validated UserQueryDTO queryDTO) {
        return Result.success(userService.pageUsers(queryDTO));
    }
    
    // 新增用户
    @PostMapping
    public Result<UserVO> create(@Validated @RequestBody UserCreateDTO dto) {
        return Result.success("创建成功", userService.create(dto));
    }
    
    // 更新用户
    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, 
                                 @Validated @RequestBody UserUpdateDTO dto) {
        dto.setId(id);  // ID 从路径获取
        return Result.success("更新成功", userService.update(dto));
    }
    
    // 删除用户
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功", null);
    }
    
    // 切换状态
    @PostMapping("/{id}/toggle")
    public Result<UserVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", userService.toggleStatus(id));
    }
}
```

### 3. 前端用户列表（UserList.vue）

```vue
<template>
  <div class="page">
    <header class="topbar">
      <div class="logo">用户管理</div>
      <div class="actions">
        <el-button type="primary" @click="openDialog()">新增用户</el-button>
        <el-button link @click="logout">退出登录</el-button>
      </div>
    </header>
    
    <section class="content">
      <!-- 查询条件 -->
      <div class="filters">
        <el-input
          v-model="query.keyword"
          placeholder="用户名/昵称/邮箱"
          clearable
          @keyup.enter="loadData"
        />
        <el-select v-model="query.status" placeholder="状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
      
      <!-- 用户列表表格 -->
      <el-table :data="list" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleToggle(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页器 -->
      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :current-page="query.page + 1"
          :page-size="query.size"
          :page-sizes="[10, 20, 50, 100]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </section>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="560px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" 
                    :placeholder="isEdit ? '留空则不修改' : ''" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUsers, createUser, updateUser, deleteUser, toggleUser } from '@/api/user';

const query = reactive({
  page: 0,
  size: 10,
  keyword: '',
  status: null
});

const list = ref([]);
const total = ref(0);

// 加载数据
async function loadData() {
  const res = await getUsers(query);
  list.value = res.data.content;
  total.value = res.data.totalElements;
}

// 分页切换
function handlePageChange(page) {
  query.page = page - 1;
  loadData();
}

function handleSizeChange(size) {
  query.size = size;
  query.page = 0;
  loadData();
}

// 重置查询
function reset() {
  query.keyword = '';
  query.status = null;
  query.page = 0;
  loadData();
}

// 初始加载
onMounted(() => {
  loadData();
});
</script>
```

## 🎓 学习要点

### 1. DTO 的作用

**为什么要用 DTO？**
- ✅ 参数校验：在 DTO 中定义校验规则
- ✅ 字段过滤：只接收需要的字段
- ✅ 数据转换：前端传来的字符串转数字等
- ✅ 安全性：防止前端传入额外字段（如 ID、createTime）

**示例**：
```java
// ❌ 不使用 DTO（不安全）
@PostMapping
public Result<User> create(@RequestBody User user) {
    // 前端可以传入 id、createTime 等字段，不安全
    return Result.success(userRepository.save(user));
}

// ✅ 使用 DTO（安全）
@PostMapping
public Result<UserVO> create(@Validated @RequestBody UserCreateDTO dto) {
    // 只接收 DTO 中定义的字段，且经过校验
    return Result.success(userService.create(dto));
}
```

### 2. 分页查询最佳实践

```java
// ✅ 推荐：使用 Pageable
Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
Page<User> result = userRepository.findAll(pageable);

// ❌ 不推荐：手动计算 offset
List<User> result = userRepository.findAll(page * size, size);
```

### 3. 密码更新策略

```java
// 编辑用户时，密码字段可选
if (StringUtils.hasText(dto.getPassword())) {
    user.setPassword(passwordUtil.encode(dto.getPassword()));
}
// 如果不传密码，则保持原密码不变
```

### 4. 唯一性验证时机

```java
// 新增：直接验证
if (userRepository.existsByUsername(dto.getUsername())) {
    throw new BusinessException(400, "用户名已存在");
}

// 编辑：排除自己
if (dto.getUsername() != null 
    && !dto.getUsername().equals(user.getUsername()) 
    && userRepository.existsByUsername(dto.getUsername())) {
    throw new BusinessException(400, "用户名已存在");
}
```

## 🔗 与其他章节的关系

```
第一章：登录功能
    ↓
第二章：用户管理  ← 【当前章节】
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
- ✅ CRUD 完整实现
- ✅ 分页查询
- ✅ 动态条件查询
- ✅ DTO/VO 模式
- ✅ 参数校验

**继续学习**：
- **第三章：角色管理** - 实现角色的增删改查，为 RBAC 权限系统做准备

## 📝 常见问题

### 1. 为什么分页从 0 开始？

Spring Data JPA 的 `PageRequest.of(page, size)` 中，page 参数从 0 开始。

**前端处理**：
```javascript
// 前端显示从 1 开始
<el-pagination :current-page="query.page + 1" />

// 切换页码时减 1
function handlePageChange(page) {
  query.page = page - 1;
  loadData();
}
```

### 2. 如何实现软删除？

```java
// 方式一：添加 deleted 字段
@Entity
public class User {
    @Column(columnDefinition = "tinyint default 0")
    private Integer deleted;  // 0-未删除 1-已删除
}

// 方式二：使用 @SQLDelete 和 @Where 注解
@Entity
@SQLDelete(sql = "UPDATE sys_user SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0")
public class User {
    // ...
}
```

### 3. 如何优化查询性能？

```java
// 1. 添加索引
CREATE INDEX idx_username ON sys_user(username);
CREATE INDEX idx_status ON sys_user(status);

// 2. 只查询需要的字段（使用 DTO 投影）
@Query("SELECT new com.training.admin.dto.UserListDTO(u.id, u.username, u.nickname) FROM User u")
List<UserListDTO> findAllSimple();

// 3. 避免 N+1 查询问题
@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = ?1")
Optional<User> findByIdWithRoles(Long id);
```

### 4. 前端表单验证规则？

```javascript
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
};
```

## 📖 参考资料

- [Spring Data JPA 官方文档](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA Specification 使用指南](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications)
- [Bean Validation 注解](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/constraints/package-summary.html)
- [Element Plus Table 组件](https://element-plus.org/zh-CN/component/table.html)
- [Element Plus Form 组件](https://element-plus.org/zh-CN/component/form.html)

---

**🎉 恭喜！完成本章后，你已经掌握了企业级应用中最常见的 CRUD 开发模式！**
