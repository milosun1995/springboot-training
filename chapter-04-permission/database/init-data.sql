-- 第四章：权限管理 - 初始化数据（MySQL）
-- 密码均为：password123（BCrypt）
-- 注意：此脚本与 DataInitializer.java 保持一致

-- 清空数据（可选，用于重新初始化）
-- TRUNCATE TABLE `sys_user`;
-- TRUNCATE TABLE `sys_permission`;

-- 用户数据（与 DataInitializer.java 保持一致）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW()),
('jerry', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '杰瑞', 'jerry@example.com', 1, NOW()),
('tom', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '汤姆', 'tom@example.com', 0, NOW()),
('alice', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '爱丽丝', 'alice@example.com', 1, NOW());

-- 权限树数据（与 DataInitializer.java 保持一致）
-- 注意：parent_id 需要根据实际插入顺序调整，或使用子查询
-- 一级权限：系统管理
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(NULL, '系统管理', 'MENU_SYSTEM', 'menu', '/system', NULL, 1, 1, NOW());

-- 二级权限：用户管理（parent_id = 1，即 MENU_SYSTEM 的 id）
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, '用户管理', 'MENU_USER', 'menu', '/users', NULL, 10, 1, NOW());

-- 三级权限：用户管理下的按钮（parent_id = 2，即 MENU_USER 的 id）
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(2, '用户查询', 'BTN_USER_QUERY', 'button', '/api/users', 'GET', 1, 1, NOW()),
(2, '用户新增', 'BTN_USER_ADD', 'button', '/api/users', 'POST', 2, 1, NOW()),
(2, '用户编辑', 'BTN_USER_EDIT', 'button', '/api/users/{id}', 'PUT', 3, 1, NOW()),
(2, '用户删除', 'BTN_USER_DELETE', 'button', '/api/users/{id}', 'DELETE', 4, 1, NOW());

-- 二级权限：角色管理（parent_id = 1，即 MENU_SYSTEM 的 id）
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, '角色管理', 'MENU_ROLE', 'menu', '/roles', NULL, 20, 1, NOW());

-- 三级权限：角色管理下的按钮（parent_id = 7，即 MENU_ROLE 的 id）
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(7, '角色查询', 'BTN_ROLE_QUERY', 'button', '/api/roles', 'GET', 1, 1, NOW()),
(7, '角色新增', 'BTN_ROLE_ADD', 'button', '/api/roles', 'POST', 2, 1, NOW()),
(7, '角色编辑', 'BTN_ROLE_EDIT', 'button', '/api/roles/{id}', 'PUT', 3, 1, NOW()),
(7, '角色删除', 'BTN_ROLE_DELETE', 'button', '/api/roles/{id}', 'DELETE', 4, 1, NOW());

-- 二级权限：权限管理（parent_id = 1，即 MENU_SYSTEM 的 id）
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(1, '权限管理', 'MENU_PERMISSION', 'menu', '/permissions', NULL, 30, 1, NOW());

-- 三级权限：权限管理下的按钮（parent_id = 12，即 MENU_PERMISSION 的 id）
INSERT INTO `sys_permission` (`parent_id`, `name`, `code`, `type`, `path`, `method`, `sort`, `status`, `create_time`) VALUES
(12, '权限查询', 'BTN_PERM_QUERY', 'button', '/api/permissions/tree', 'GET', 1, 1, NOW()),
(12, '权限新增', 'BTN_PERM_ADD', 'button', '/api/permissions', 'POST', 2, 1, NOW()),
(12, '权限编辑', 'BTN_PERM_EDIT', 'button', '/api/permissions/{id}', 'PUT', 3, 1, NOW()),
(12, '权限删除', 'BTN_PERM_DELETE', 'button', '/api/permissions/{id}', 'DELETE', 4, 1, NOW());

-- 说明：
-- 1. 密码明文：password123
-- 2. 如需生成新密码，可参考：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String encodedPassword = encoder.encode("your_password");

