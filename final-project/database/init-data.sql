-- Final Project - 初始化数据脚本（MySQL）
-- 与 DataInitializer.java 完全一致
-- 密码均为：password123（BCrypt）
-- 注意：此脚本用于手动初始化数据库，应用启动时使用 DataInitializer.java 自动初始化

-- 清空数据（可选，用于重新初始化）
-- TRUNCATE TABLE `sys_user_role`;
-- TRUNCATE TABLE `sys_role_permission`;
-- TRUNCATE TABLE `sys_role_menu`;
-- TRUNCATE TABLE `sys_user`;
-- TRUNCATE TABLE `sys_role`;
-- TRUNCATE TABLE `sys_permission`;
-- TRUNCATE TABLE `sys_menu`;
-- TRUNCATE TABLE `sys_dict`;

-- ============================================
-- 1. 用户数据（4个用户）
-- ============================================
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW()),
('manager', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '经理', 'manager@example.com', 1, NOW()),
('jerry', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '杰瑞', 'jerry@example.com', 1, NOW()),
('tom', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '汤姆', 'tom@example.com', 1, NOW());

-- ============================================
-- 2. 角色数据（3个角色）
-- ============================================
INSERT INTO `sys_role` (`code`, `name`, `description`, `status`, `create_time`) VALUES
('ADMIN', '管理员', '拥有所有权限', 1, NOW()),
('OPS', '运维人员', '拥有部分权限', 1, NOW()),
('GUEST', '访客', '只读权限', 1, NOW());

-- ============================================
-- 3. 权限数据（树形结构，使用新编码格式）
-- ============================================
-- 注意：parent_id 需要根据实际插入顺序调整
-- 一级权限：系统管理
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(NULL, 'sys:manage', '系统管理', 'menu', NULL, NULL, 1, 1, NOW());

-- 二级权限：用户管理（parent_id = 1，即 sys:manage 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, 'sys:user', '用户管理', 'menu', '/users', NULL, 10, 1, NOW());

-- 三级权限：用户管理下的按钮和 API（parent_id = 2，即 sys:user 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(2, 'sys:user:view', '查看用户', 'button', NULL, NULL, 11, 1, NOW()),
(2, 'sys:user:create', '新增用户', 'button', NULL, NULL, 12, 1, NOW()),
(2, 'sys:user:update', '编辑用户', 'button', NULL, NULL, 13, 1, NOW()),
(2, 'sys:user:delete', '删除用户', 'button', NULL, NULL, 14, 1, NOW()),
(2, 'sys:user:toggle', '启用/禁用', 'button', NULL, NULL, 15, 1, NOW()),
(2, 'sys:user:list', '用户列表', 'api', '/api/users', 'GET', 16, 1, NOW()),
(2, 'sys:user:add', '添加用户', 'api', '/api/users', 'POST', 17, 1, NOW()),
(2, 'sys:user:edit', '修改用户', 'api', '/api/users/*', 'PUT', 18, 1, NOW()),
(2, 'sys:user:del', '删除用户', 'api', '/api/users/*', 'DELETE', 19, 1, NOW()),
(2, 'sys:user:status', '用户状态', 'api', '/api/users/*/toggle', 'PUT', 20, 1, NOW());

-- 二级权限：角色管理（parent_id = 1，即 sys:manage 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, 'sys:role', '角色管理', 'menu', '/roles', NULL, 30, 1, NOW());

-- 三级权限：角色管理下的按钮和 API（parent_id = 7，即 sys:role 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(7, 'sys:role:view', '查看角色', 'button', NULL, NULL, 31, 1, NOW()),
(7, 'sys:role:create', '新增角色', 'button', NULL, NULL, 32, 1, NOW()),
(7, 'sys:role:update', '编辑角色', 'button', NULL, NULL, 33, 1, NOW()),
(7, 'sys:role:delete', '删除角色', 'button', NULL, NULL, 34, 1, NOW()),
(7, 'sys:role:toggle', '启用/禁用', 'button', NULL, NULL, 35, 1, NOW()),
(7, 'sys:role:assign', '分配权限', 'button', NULL, NULL, 36, 1, NOW()),
(7, 'sys:role:list', '角色列表', 'api', '/api/roles', 'GET', 37, 1, NOW()),
(7, 'sys:role:add', '添加角色', 'api', '/api/roles', 'POST', 38, 1, NOW()),
(7, 'sys:role:edit', '修改角色', 'api', '/api/roles/*', 'PUT', 39, 1, NOW()),
(7, 'sys:role:del', '删除角色', 'api', '/api/roles/*', 'DELETE', 40, 1, NOW()),
(7, 'sys:role:status', '角色状态', 'api', '/api/roles/*/toggle', 'PUT', 41, 1, NOW()),
(7, 'sys:role:perm', '角色权限', 'api', '/api/roles/*/permissions', 'PUT', 42, 1, NOW()),
(7, 'sys:role:menu', '角色菜单', 'api', '/api/roles/*/menus', 'PUT', 43, 1, NOW());

-- 二级权限：权限管理（parent_id = 1，即 sys:manage 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, 'sys:permission', '权限管理', 'menu', '/permissions', NULL, 50, 1, NOW());

-- 三级权限：权限管理下的按钮和 API（parent_id = 18，即 sys:permission 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(18, 'sys:permission:view', '查看权限', 'button', NULL, NULL, 51, 1, NOW()),
(18, 'sys:permission:create', '新增权限', 'button', NULL, NULL, 52, 1, NOW()),
(18, 'sys:permission:update', '编辑权限', 'button', NULL, NULL, 53, 1, NOW()),
(18, 'sys:permission:delete', '删除权限', 'button', NULL, NULL, 54, 1, NOW()),
(18, 'sys:permission:toggle', '启用/禁用', 'button', NULL, NULL, 55, 1, NOW()),
(18, 'sys:permission:list', '权限列表', 'api', '/api/permissions', 'GET', 56, 1, NOW()),
(18, 'sys:permission:add', '添加权限', 'api', '/api/permissions', 'POST', 57, 1, NOW()),
(18, 'sys:permission:edit', '修改权限', 'api', '/api/permissions/*', 'PUT', 58, 1, NOW()),
(18, 'sys:permission:del', '删除权限', 'api', '/api/permissions/*', 'DELETE', 59, 1, NOW()),
(18, 'sys:permission:status', '权限状态', 'api', '/api/permissions/*/toggle', 'PUT', 60, 1, NOW());

-- 二级权限：菜单管理（parent_id = 1，即 sys:manage 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, 'sys:menu', '菜单管理', 'menu', '/menus', NULL, 70, 1, NOW());

-- 三级权限：菜单管理下的按钮和 API（parent_id = 28，即 sys:menu 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(28, 'sys:menu:view', '查看菜单', 'button', NULL, NULL, 71, 1, NOW()),
(28, 'sys:menu:create', '新增菜单', 'button', NULL, NULL, 72, 1, NOW()),
(28, 'sys:menu:update', '编辑菜单', 'button', NULL, NULL, 73, 1, NOW()),
(28, 'sys:menu:delete', '删除菜单', 'button', NULL, NULL, 74, 1, NOW()),
(28, 'sys:menu:toggle', '启用/禁用', 'button', NULL, NULL, 75, 1, NOW()),
(28, 'sys:menu:list', '菜单列表', 'api', '/api/menus', 'GET', 76, 1, NOW()),
(28, 'sys:menu:add', '添加菜单', 'api', '/api/menus', 'POST', 77, 1, NOW()),
(28, 'sys:menu:edit', '修改菜单', 'api', '/api/menus/*', 'PUT', 78, 1, NOW()),
(28, 'sys:menu:del', '删除菜单', 'api', '/api/menus/*', 'DELETE', 79, 1, NOW()),
(28, 'sys:menu:status', '菜单状态', 'api', '/api/menus/*/toggle', 'PUT', 80, 1, NOW());

-- 二级权限：字典管理（parent_id = 1，即 sys:manage 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, 'sys:dict', '字典管理', 'menu', '/dicts', NULL, 90, 1, NOW());

-- 三级权限：字典管理下的按钮和 API（parent_id = 38，即 sys:dict 的 id）
INSERT INTO `sys_permission` (`parent_id`, `code`, `name`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(38, 'sys:dict:view', '查看字典', 'button', NULL, NULL, 91, 1, NOW()),
(38, 'sys:dict:create', '新增字典', 'button', NULL, NULL, 92, 1, NOW()),
(38, 'sys:dict:update', '编辑字典', 'button', NULL, NULL, 93, 1, NOW()),
(38, 'sys:dict:delete', '删除字典', 'button', NULL, NULL, 94, 1, NOW()),
(38, 'sys:dict:toggle', '启用/禁用', 'button', NULL, NULL, 95, 1, NOW()),
(38, 'sys:dict:list', '字典列表', 'api', '/api/dicts', 'GET', 96, 1, NOW()),
(38, 'sys:dict:add', '添加字典', 'api', '/api/dicts', 'POST', 97, 1, NOW()),
(38, 'sys:dict:edit', '修改字典', 'api', '/api/dicts/*', 'PUT', 98, 1, NOW()),
(38, 'sys:dict:del', '删除字典', 'api', '/api/dicts/*', 'DELETE', 99, 1, NOW()),
(38, 'sys:dict:status', '字典状态', 'api', '/api/dicts/*/toggle', 'PUT', 100, 1, NOW());

-- ============================================
-- 4. 菜单数据（树形结构）
-- ============================================
-- 一级菜单：系统管理
INSERT INTO `sys_menu` (`parent_id`, `name`, `code`, `path`, `component`, `icon`, `sort`, `status`, `create_time`) VALUES
(NULL, '系统管理', 'system', NULL, NULL, 'Setting', 1, 1, NOW());

-- 二级菜单（parent_id = 1，即 system 的 id）
INSERT INTO `sys_menu` (`parent_id`, `name`, `code`, `path`, `component`, `icon`, `sort`, `status`, `create_time`) VALUES
(1, '用户管理', 'user', '/users', 'UserList', 'User', 2, 1, NOW()),
(1, '角色管理', 'role', '/roles', 'RoleList', 'UserFilled', 3, 1, NOW()),
(1, '权限管理', 'permission', '/permissions', 'PermissionList', 'Key', 4, 1, NOW()),
(1, '菜单管理', 'menu', '/menus', 'MenuList', 'Menu', 5, 1, NOW()),
(1, '字典管理', 'dict', '/dicts', 'DictList', 'Document', 6, 1, NOW());

-- ============================================
-- 5. 字典数据（使用 dict_value 和 dict_type 字段）
-- ============================================
INSERT INTO `sys_dict` (`code`, `label`, `dict_value`, `dict_type`, `sort`, `status`, `remark`, `create_time`) VALUES
('gender_male', '男', 'male', 'gender', 1, 1, '性别-男', NOW()),
('gender_female', '女', 'female', 'gender', 2, 1, '性别-女', NOW()),
('status_enable', '启用', '1', 'status', 1, 1, '状态-启用', NOW()),
('status_disable', '禁用', '0', 'status', 2, 1, '状态-禁用', NOW()),
('user_type_admin', '管理员', 'admin', 'user_type', 1, 1, '用户类型-管理员', NOW()),
('user_type_normal', '普通用户', 'normal', 'user_type', 2, 1, '用户类型-普通用户', NOW());

-- ============================================
-- 6. 用户-角色关联
-- ============================================
-- admin -> ADMIN (user_id=1, role_id=1)
-- manager -> ADMIN (user_id=2, role_id=1)
-- jerry -> OPS (user_id=3, role_id=2)
-- tom -> GUEST (user_id=4, role_id=3)
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin -> ADMIN
(2, 1), -- manager -> ADMIN
(3, 2), -- jerry -> OPS
(4, 3); -- tom -> GUEST

-- ============================================
-- 7. 角色-权限关联（使用子查询确保准确性）
-- ============================================
-- ADMIN：拥有所有权限
-- OPS：用户和角色的查看和部分操作（不包含 delete/del）
-- GUEST：只有查看权限（view/list 和 sys:manage）

-- ADMIN 角色：所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

-- OPS 角色：用户和角色的查看和部分操作（不包含 delete/del）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `sys_permission`
WHERE code = 'sys:manage'
   OR (code LIKE 'sys:user:%' AND code NOT LIKE '%delete%' AND code NOT LIKE '%del%')
   OR (code LIKE 'sys:role:%' AND code NOT LIKE '%delete%' AND code NOT LIKE '%del%');

-- GUEST 角色：只有查看权限（view/list 和 sys:manage）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `sys_permission`
WHERE code = 'sys:manage'
   OR code LIKE '%:view'
   OR code LIKE '%:list';

-- ============================================
-- 8. 角色-菜单关联（使用子查询确保准确性）
-- ============================================
-- ADMIN：所有菜单
-- OPS：系统管理、用户管理、角色管理
-- GUEST：系统管理、用户管理

-- ADMIN 角色：所有菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`;

-- OPS 角色：系统管理、用户管理、角色管理
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 2, id FROM `sys_menu`
WHERE code IS NULL OR code = 'system' OR code = 'user' OR code = 'role';

-- GUEST 角色：系统管理、用户管理
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 3, id FROM `sys_menu`
WHERE code IS NULL OR code = 'system' OR code = 'user';

-- ============================================
-- 说明
-- ============================================
-- 1. 密码明文：password123
-- 2. 权限和菜单的 parent_id 已根据插入顺序设置（需要确保插入顺序正确）
-- 3. 角色-权限和角色-菜单的关联关系使用子查询，确保准确性
-- 4. 如需生成新密码，可参考：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String encodedPassword = encoder.encode("your_password");
-- 5. 此脚本与 DataInitializer.java 完全一致
-- 6. 建议使用 DataInitializer.java 进行自动初始化，此脚本用于手动初始化或参考
-- 7. 执行顺序：用户 -> 角色 -> 权限 -> 菜单 -> 字典 -> 关联关系
