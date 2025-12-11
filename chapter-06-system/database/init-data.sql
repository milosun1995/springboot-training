-- 第六章：系统功能增强 - 初始化数据（MySQL，字典示例）
-- 密码均为：password123（BCrypt）
-- 注意：此脚本与 DataInitializer.java 保持一致

-- 清空数据（可选，用于重新初始化）
-- TRUNCATE TABLE `sys_user`;
-- TRUNCATE TABLE `sys_dict`;

-- 用户数据（与 DataInitializer.java 保持一致）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW()),
('jerry', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '杰瑞', 'jerry@example.com', 1, NOW()),
('tom', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '汤姆', 'tom@example.com', 0, NOW()),
('alice', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '爱丽丝', 'alice@example.com', 1, NOW());

-- 字典数据（与 DataInitializer.java 保持一致）
-- 注意：字段名使用 dict_value 和 dict_type（避免 SQL 关键字）
INSERT INTO `sys_dict` (`code`, `label`, `dict_value`, `dict_type`, `sort`, `status`, `remark`, `create_time`) VALUES
('GENDER_MALE', '男', 'male', 'gender', 1, 1, '性别-男', NOW()),
('GENDER_FEMALE', '女', 'female', 'gender', 2, 1, '性别-女', NOW()),
('STATUS_ENABLED', '启用', '1', 'status', 1, 1, '状态-启用', NOW()),
('STATUS_DISABLED', '禁用', '0', 'status', 2, 1, '状态-禁用', NOW()),
('USER_TYPE_ADMIN', '管理员', 'admin', 'user_type', 1, 1, '用户类型-管理员', NOW()),
('USER_TYPE_NORMAL', '普通用户', 'normal', 'user_type', 2, 1, '用户类型-普通用户', NOW()),
('USER_TYPE_GUEST', '访客', 'guest', 'user_type', 3, 1, '用户类型-访客', NOW());

-- 说明：
-- 1. 密码明文：password123
-- 2. 如需生成新密码，可参考：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String encodedPassword = encoder.encode("your_password");

