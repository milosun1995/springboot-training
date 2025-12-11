# 第六章：系统功能 - 字典管理

## 📖 章节说明

本章实现了数据字典管理功能，是企业级应用中常见的系统基础功能。字典用于管理系统中的各类配置数据（如：性别、状态、类型等），避免硬编码，提高系统的灵活性和可维护性。

**与前几章的关系**：
- 前五章完成了 RBAC 核心功能（用户、角色、权限、菜单）
- 本章是系统基础功能的补充
- 为最终项目的完整性提供支撑

## 🎯 学习目标

通过本章学习，你将掌握：

### 后端技能
- ✅ 字典表设计（代码、标签、值、类型）
- ✅ 字典分类管理（按类型分组）
- ✅ 字典缓存优化
- ✅ 字典数据的批量初始化

### 前端技能
- ✅ 字典选择组件
- ✅ 字典数据的缓存和复用
- ✅ 动态表单选项加载
- ✅ 字典的分类展示

### 业务思维
- ✅ 什么时候需要使用字典
- ✅ 如何设计字典编码规范
- ✅ 字典与枚举的区别

## 📁 项目结构

```
chapter-06-system/
├── backend/
│   ├── src/main/java/com/training/admin/
│   │   ├── controller/
│   │   │   └── DictController.java        # 字典管理 API
│   │   ├── service/
│   │   │   └── DictService.java           # 字典业务逻辑
│   │   ├── repository/
│   │   │   └── DictRepository.java        # 字典数据访问
│   │   ├── entity/
│   │   │   └── Dict.java                  # 字典实体
│   │   ├── dto/
│   │   │   ├── DictCreateDTO.java
│   │   │   ├── DictUpdateDTO.java
│   │   │   └── DictQueryDTO.java
│   │   └── vo/
│   │       └── DictVO.java
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── dict.js                    # 字典 API
│   │   └── views/
│   │       ├── Login.vue
│   │       └── DictList.vue               # 字典列表
│   └── package.json
├── database/
│   ├── schema.sql                         # 字典表结构
│   └── init-data.sql                      # 初始字典数据
└── README.md
```

## 💡 核心知识点

### 1. 字典表设计

```sql
CREATE TABLE sys_dict (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL UNIQUE COMMENT '字典编码（唯一标识）',
  label VARCHAR(100) NOT NULL COMMENT '字典标签（显示文本）',
  dict_value VARCHAR(100) NOT NULL COMMENT '字典值（实际存储值）',
  dict_type VARCHAR(100) COMMENT '字典类型（分类）',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  remark VARCHAR(255) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_dict_type (dict_type)
);
```

**设计要点**：
- `code`：字典编码，全局唯一（如：`gender_male`、`status_enable`）
- `label`：显示给用户看的文本（如：`男`、`启用`）
- `dict_value`：实际存储的值（如：`1`、`M`、`ACTIVE`）
- `dict_type`：字典分类（如：`gender`、`status`、`user_type`）

### 2. 字典编码规范

```
格式：类型_键值
示例：
  gender_male       - 性别：男
  gender_female     - 性别：女
  status_enable     - 状态：启用
  status_disable    - 状态：禁用
  user_type_admin   - 用户类型：管理员
  user_type_normal  - 用户类型：普通用户
```

### 3. 常见字典类型

| 字典类型 | 说明 | 示例值 |
|---------|------|--------|
| `gender` | 性别 | 男、女、未知 |
| `status` | 状态 | 启用、禁用 |
| `user_type` | 用户类型 | 管理员、普通用户、访客 |
| `order_status` | 订单状态 | 待支付、已支付、已发货、已完成 |
| `yes_no` | 是否 | 是、否 |

### 4. 字典数据示例

```sql
-- 性别字典
INSERT INTO sys_dict (code, label, dict_value, dict_type, sort) VALUES
('gender_male', '男', '1', 'gender', 1),
('gender_female', '女', '2', 'gender', 2),
('gender_unknown', '未知', '0', 'gender', 3);

-- 状态字典
INSERT INTO sys_dict (code, label, dict_value, dict_type, sort) VALUES
('status_enable', '启用', '1', 'status', 1),
('status_disable', '禁用', '0', 'status', 2);

-- 用户类型字典
INSERT INTO sys_dict (code, label, dict_value, dict_type, sort) VALUES
('user_type_admin', '管理员', 'ADMIN', 'user_type', 1),
('user_type_normal', '普通用户', 'NORMAL', 'user_type', 2),
('user_type_guest', '访客', 'GUEST', 'user_type', 3);
```

## ✨ 功能特性

### 后端功能
- ✅ 字典列表查询（分页 + 条件过滤）
- ✅ 按类型查询字典（如：查询所有性别字典）
- ✅ 新增字典（编码唯一性验证）
- ✅ 编辑字典
- ✅ 删除字典
- ✅ 启用/禁用字典

### 前端功能
- ✅ 字典列表展示（表格 + 分页）
- ✅ 关键字搜索（编码/标签/值/类型）
- ✅ 新增/编辑对话框
- ✅ 表单验证
- ✅ 字典值的友好显示

## 🚀 快速开始

### 1. 数据库准备（H2）

应用启动时自动初始化字典数据：

```java
// DataInitializer.java
// 性别字典
saveDict("gender_male", "男", "1", "gender", 1);
saveDict("gender_female", "女", "2", "gender", 2);
saveDict("gender_unknown", "未知", "0", "gender", 3);

// 状态字典
saveDict("status_enable", "启用", "1", "status", 1);
saveDict("status_disable", "禁用", "0", "status", 2);

// 用户类型字典
saveDict("user_type_admin", "管理员", "ADMIN", "user_type", 1);
saveDict("user_type_normal", "普通用户", "NORMAL", "user_type", 2);
saveDict("user_type_guest", "访客", "GUEST", "user_type", 3);
```

### 2. 启动后端

```bash
cd chapter-06-system/backend
./mvnw spring-boot:run
```

### 3. 启动前端

```bash
cd chapter-06-system/frontend
npm install
npm run dev
```

### 4. 测试功能

#### 登录
- 用户名：`admin`
- 密码：`password123`

#### 测试场景
1. **查看字典列表**：
   - 观察不同类型的字典
   - 注意排序字段的作用

2. **搜索字典**：
   - 按类型搜索：`gender`
   - 按标签搜索：`男`
   - 按编码搜索：`gender_male`

3. **新增字典**：
   ```
   编码：education_bachelor
   标签：本科
   值：BACHELOR
   类型：education
   排序：1
   ```

4. **编辑字典**：
   - 修改标签文本
   - 调整排序

5. **删除字典**：
   - 删除测试创建的字典

## 🧪 API 测试

### 1. 获取字典列表
```bash
curl -X GET "http://localhost:8080/api/dicts?page=0&size=10&keyword=gender" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. 按类型查询字典
```bash
curl -X GET "http://localhost:8080/api/dicts?type=gender" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. 新增字典
```bash
curl -X POST "http://localhost:8080/api/dicts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "code": "education_bachelor",
    "label": "本科",
    "value": "BACHELOR",
    "type": "education",
    "sort": 1,
    "status": 1
  }'
```

## 📚 核心代码解析

### 1. 字典实体（Dict.java）

```java
@Entity
@Table(name = "sys_dict")
@Data
public class Dict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String code;  // 字典编码：gender_male
    
    @Column(nullable = false, length = 100)
    private String label;  // 字典标签：男
    
    @Column(name = "dict_value", nullable = false, length = 100)
    private String value;  // 字典值：1 或 M
    
    @Column(name = "dict_type", length = 100)
    private String type;  // 字典类型：gender
    
    @Column(columnDefinition = "int default 0")
    private Integer sort;  // 排序
    
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;  // 状态
    
    @Column(length = 255)
    private String remark;  // 备注
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
```

### 2. 字典服务（DictService.java）

```java
@Service
@RequiredArgsConstructor
public class DictService {
    
    private final DictRepository dictRepository;
    
    // 分页查询
    public Page<DictVO> page(DictQueryDTO query) {
        Pageable pageable = PageRequest.of(
            query.getPage(), 
            query.getSize(), 
            Sort.by(Sort.Direction.ASC, "sort").and(Sort.by("id"))
        );
        
        Specification<Dict> spec = buildSpecification(query);
        return dictRepository.findAll(spec, pageable).map(this::toVO);
    }
    
    // 按类型查询
    public List<DictVO> listByType(String type) {
        List<Dict> dicts = dictRepository.findByTypeAndStatusOrderBySortAsc(type, 1);
        return dicts.stream().map(this::toVO).collect(Collectors.toList());
    }
    
    // 新增字典
    public DictVO create(DictCreateDTO dto) {
        if (dictRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "字典编码已存在");
        }
        
        Dict dict = new Dict();
        dict.setCode(dto.getCode());
        dict.setLabel(dto.getLabel());
        dict.setValue(dto.getValue());
        dict.setType(dto.getType());
        dict.setSort(dto.getSort() != null ? dto.getSort() : 0);
        dict.setStatus(1);
        dict.setRemark(dto.getRemark());
        dict.setCreateTime(LocalDateTime.now());
        dict.setUpdateTime(LocalDateTime.now());
        
        return toVO(dictRepository.save(dict));
    }
}
```

### 3. 前端字典选择组件（DictSelect.vue）

```vue
<template>
  <el-select
    v-model="modelValue"
    :placeholder="placeholder"
    clearable
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getDictsByType } from '@/api/dict';

const props = defineProps({
  modelValue: [String, Number],
  type: {
    type: String,
    required: true
  },
  placeholder: {
    type: String,
    default: '请选择'
  }
});

const emit = defineEmits(['update:modelValue']);

const options = ref([]);

onMounted(async () => {
  try {
    const res = await getDictsByType(props.type);
    options.value = res.data.map(d => ({
      label: d.label,
      value: d.value
    }));
  } catch (error) {
    console.error('加载字典失败', error);
  }
});
</script>

<!-- 使用示例 -->
<dict-select v-model="form.gender" type="gender" placeholder="选择性别" />
<dict-select v-model="form.userType" type="user_type" placeholder="选择用户类型" />
```

## 🎓 学习要点

### 1. 字典 vs 枚举

| 对比项 | 字典 | 枚举 |
|-------|------|------|
| **存储位置** | 数据库 | 代码中 |
| **修改方式** | 通过界面修改 | 需要改代码并重新部署 |
| **灵活性** | 高 | 低 |
| **性能** | 需要查询数据库 | 直接内存访问 |
| **适用场景** | 业务配置数据 | 系统常量 |

**使用建议**：
- ✅ 使用字典：性别、状态、类型等业务配置
- ✅ 使用枚举：HTTP 状态码、系统错误码等固定常量

### 2. 字典编码命名规范

✅ **好的命名**：
- `gender_male` - 清晰明了
- `status_enable` - 语义化
- `order_status_pending` - 层级分明

❌ **不好的命名**：
- `001` - 无意义数字
- `male` - 缺少类型前缀
- `genderMale` - 驼峰命名（推荐下划线）

### 3. 字典缓存策略

```java
@Service
public class DictService {
    
    // 使用本地缓存
    private final Map<String, List<DictVO>> dictCache = new ConcurrentHashMap<>();
    
    public List<DictVO> listByType(String type) {
        // 先从缓存获取
        List<DictVO> cached = dictCache.get(type);
        if (cached != null) {
            return cached;
        }
        
        // 查询数据库
        List<Dict> dicts = dictRepository.findByTypeAndStatusOrderBySortAsc(type, 1);
        List<DictVO> result = dicts.stream().map(this::toVO).collect(Collectors.toList());
        
        // 放入缓存
        dictCache.put(type, result);
        return result;
    }
    
    // 更新/删除字典时清除缓存
    public void update(DictUpdateDTO dto) {
        Dict dict = findById(dto.getId());
        // ... 更新逻辑
        dictCache.remove(dict.getType());  // 清除缓存
    }
}
```

### 4. 字典在表单中的应用

```vue
<template>
  <el-form :model="form">
    <!-- 性别选择 -->
    <el-form-item label="性别">
      <dict-select v-model="form.gender" type="gender" />
    </el-form-item>
    
    <!-- 状态选择 -->
    <el-form-item label="状态">
      <dict-select v-model="form.status" type="status" />
    </el-form-item>
    
    <!-- 用户类型选择 -->
    <el-form-item label="用户类型">
      <dict-select v-model="form.userType" type="user_type" />
    </el-form-item>
  </el-form>
</template>
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
第五章：菜单管理 (Menu)
    ↓
第六章：系统功能 (Dict)  ← 【当前章节】
    ↓
最终项目：完整的企业级管理系统
  • 用户管理可使用字典（性别、状态）
  • 订单管理可使用字典（订单状态）
  • 商品管理可使用字典（商品分类）
```

## 🎯 下一步

完成本章后，你已经掌握了：
- ✅ 字典表的设计
- ✅ 字典的 CRUD 操作
- ✅ 字典的分类管理
- ✅ 字典在表单中的应用

**继续学习**：
- **最终项目** - 整合所有模块，实现完整的企业级管理系统

## 📝 常见问题

### 1. 什么时候应该使用字典？
- ✅ 业务配置数据（性别、状态、类型）
- ✅ 需要灵活修改的选项
- ✅ 需要在界面上展示的下拉选项
- ❌ 不要用字典存储业务数据

### 2. 字典的 code、label、value 有什么区别？
- **code**：唯一标识，用于程序识别（如：`gender_male`）
- **label**：显示文本，用于界面展示（如：`男`）
- **value**：实际存储值，用于数据库存储（如：`1`、`M`）

### 3. 如何设计字典类型？
```
建议：
  • 使用英文小写
  • 使用下划线分隔
  • 语义化命名

示例：
  gender        - 性别
  user_type     - 用户类型
  order_status  - 订单状态
```

### 4. 字典缓存如何更新？
```java
// 方式一：手动清除
dictCache.remove(type);

// 方式二：定时刷新
@Scheduled(fixedRate = 60000)
public void refreshCache() {
    dictCache.clear();
}

// 方式三：使用 Spring Cache
@CacheEvict(value = "dictCache", key = "#type")
public void update(Dict dict) {
    // ...
}
```

## 📖 参考资料

- [数据字典设计模式](https://en.wikipedia.org/wiki/Data_dictionary)
- [Spring Cache 缓存](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Element Plus Select 组件](https://element-plus.org/zh-CN/component/select.html)

---

**🎉 恭喜！完成本章后，你已经掌握了字典管理的核心功能！现在可以进入最终项目，整合所有模块！**
