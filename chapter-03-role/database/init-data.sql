-- 第三章：角色管理 - 初始化数据
-- 密码均为 password123（BCrypt）

TRUNCATE TABLE `sys_user`;
TRUNCATE TABLE `sys_role`;

-- 用户
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW()),
('jerry', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '杰瑞', 'jerry@example.com', 1, NOW()),
('tom', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '汤姆', 'tom@example.com', 0, NOW()),
('alice', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '爱丽丝', 'alice@example.com', 1, NOW());

-- 角色
INSERT INTO `sys_role` (`name`, `code`, `description`, `status`, `create_time`) VALUES
('管理员', 'ADMIN', '系统最高权限', 1, NOW()),
('运营', 'OPS', '运营管理角色', 1, NOW()),
('访客', 'GUEST', '只读访客', 1, NOW());

-- 说明：密码明文均为 password123

