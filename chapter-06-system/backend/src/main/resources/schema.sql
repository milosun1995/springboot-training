-- 第六章：系统功能增强 - 数据库表结构（MySQL，字典示例）

-- 删除表（如果存在，开发环境每次启动重建）
DROP TABLE IF EXISTS sys_dict;
DROP TABLE IF EXISTS sys_user;

-- 用户表
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(50),
  email VARCHAR(100),
  phone VARCHAR(20),
  avatar VARCHAR(255),
  status INT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_user_status ON sys_user(status);

-- 字典表 (避免使用SQL关键字 value 和 type)
CREATE TABLE sys_dict (
  id BIGINT AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL,
  label VARCHAR(100) NOT NULL,
  dict_value VARCHAR(100) NOT NULL,
  dict_type VARCHAR(100) DEFAULT NULL,
  sort INT DEFAULT 0,
  status INT DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code),
  KEY idx_dict_type (dict_type),
  KEY idx_dict_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

