-- 第五章：菜单管理 - 初始化数据（MySQL）
-- 密码均为：password123（BCrypt）
-- 注意：此脚本与 DataInitializer.java 保持一致

-- 清空数据（可选，用于重新初始化）
-- TRUNCATE TABLE `sys_user`;
-- TRUNCATE TABLE `sys_menu`;

-- 用户数据（与 DataInitializer.java 保持一致）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW()),
('jerry', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '杰瑞', 'jerry@example.com', 1, NOW()),
('tom', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '汤姆', 'tom@example.com', 0, NOW()),
('alice', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '爱丽丝', 'alice@example.com', 1, NOW());

-- 菜单树数据（与 DataInitializer.java 保持一致）
-- 一级菜单：系统管理
INSERT INTO `sys_menu` (`parent_id`, `name`, `code`, `path`, `component`, `icon`, `sort`, `status`, `create_time`) VALUES
(NULL, '系统管理', 'MENU_SYSTEM', '/system', 'Layout', 'Setting', 1, 1, NOW());

-- 二级菜单（parent_id = 1，即 MENU_SYSTEM 的 id）
INSERT INTO `sys_menu` (`parent_id`, `name`, `code`, `path`, `component`, `icon`, `sort`, `status`, `create_time`) VALUES
(1, '用户管理', 'MENU_USER', '/users', 'UserPage', 'User', 10, 1, NOW()),
(1, '角色管理', 'MENU_ROLE', '/roles', 'RolePage', 'Avatar', 20, 1, NOW()),
(1, '权限管理', 'MENU_PERMISSION', '/permissions', 'PermissionPage', 'Lock', 30, 1, NOW()),
(1, '菜单管理', 'MENU_MENU', '/menus', 'MenuPage', 'Menu', 40, 1, NOW());

-- 说明：
-- 1. 密码明文：password123
-- 2. 如需生成新密码，可参考：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String encodedPassword = encoder.encode("your_password");

