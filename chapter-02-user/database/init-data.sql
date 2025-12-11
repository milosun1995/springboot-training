-- 第二章：用户管理 - 初始化数据
-- 密码均为：password123（BCrypt）

TRUNCATE TABLE `sys_user`;

INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW()),
('jerry', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '杰瑞', 'jerry@example.com', 1, NOW()),
('tom', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '汤姆', 'tom@example.com', 0, NOW()),
('alice', '$2a$10$4kNy0xvpEof1rp1z4Y1XYeQ.7kQMyQnQRF2O85gA3IqQeemOSL4um', '爱丽丝', 'alice@example.com', 1, NOW());

-- 说明：
-- 1. 密码明文：password123
-- 2. 如需生成新密码，可参考：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String encodedPassword = encoder.encode("your_password");

