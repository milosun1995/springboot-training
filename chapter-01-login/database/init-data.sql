-- 第一章：登录功能 - 初始化数据脚本
-- 注意：密码已使用 BCrypt 加密，原始密码为 admin123

-- 插入默认管理员用户
-- 密码：admin123 (已使用 BCrypt 加密)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8p6wO', '系统管理员', 'admin@example.com', 1, NOW());

-- 说明：
-- 1. 用户名：admin
-- 2. 密码：admin123
-- 3. 密码已使用 BCrypt 加密存储
-- 4. 如果需要生成新的加密密码，可以使用以下 Java 代码：
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String encodedPassword = encoder.encode("your_password");

