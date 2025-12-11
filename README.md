# SpringBoot 后台管理系统 - 分章节练习项目（最终版学习指南）

## 项目简介
渐进式实战项目，按章节完成从登录到完整 RBAC，再到最终可集群部署的后台管理系统。全部章节已切换 MySQL，数据默认由 `DataInitializer` 代码方式初始化；最终项目使用 Redis 分布式缓存。

## 技术栈
- 后端：Spring Boot 3、Spring Security、Spring Data JPA、JWT、Spring Cache（final-project: Redis）
- 数据库：MySQL 8.0+
- 前端：Vue 3 + Vite + Element Plus + Pinia + Axios
- 构建：Maven（含 mvnw）、npm/yarn

## 章节导航与学习要点
| 章节 | 核心内容 | 要点 |
|------|----------|------|
| chapter-01-login | 登录与 JWT | 登录接口、JWT 过滤链、全局异常、跨域 |
| chapter-02-user | 用户管理 | 分页查询、用户 CRUD、表单校验、统一返回 |
| chapter-03-role | 角色管理 | 角色 CRUD、用户-角色关联、规格查询 |
| chapter-04-permission | 权限管理 | 权限树（menu/button/api）、唯一编码、树型 CRUD |
| chapter-05-menu | 菜单管理 | 菜单树、排序、前端路由映射、树勾选 |
| chapter-06-system | 字典管理 | 字典表设计（dict_value/dict_type）、分页与状态 |
| final-project | 完整 RBAC & 集群 | 用户/角色/权限/菜单/字典整合，Redis 缓存，MySQL，JWT 无状态 |

> 各章节 README 已提供详细学习目标、运行步骤、功能清单与练习建议。

## 运行与环境
- 要求：JDK 17+，Node.js 16+。
- 数据库：创建 `springboot_admin`；如需手工，执行对应章节 `database/schema.sql`（数据由 `DataInitializer` 自动生成，final-project 亦有对齐的 `database/init-data.sql` 供手动导入）。
- 后端：`cd chapter-xx/backend && ./mvnw spring-boot:run`（Win 用 `mvnw.cmd`）。
- 前端：`cd chapter-xx/frontend && npm install && npm run dev`。
- final-project：需配置 Redis 与 MySQL，支持多节点共享缓存。

## 学习建议
- 按章节递进，跑通后再看代码细节。
- 结合 README 的练习与扩展项动手补充。
- 关注安全与一致性：密码哈希、异常处理、权限校验、缓存失效。

## 贡献
欢迎提交 Issue / PR，共同完善。  
许可证：MIT License
