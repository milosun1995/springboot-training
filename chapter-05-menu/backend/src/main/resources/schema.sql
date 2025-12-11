-- 第五章：菜单管理 - 数据库表结构（MySQL）

-- 删除表（如果存在，开发环境每次启动重建）
DROP TABLE IF EXISTS sys_menu;
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

-- 菜单表
CREATE TABLE sys_menu (
  id BIGINT AUTO_INCREMENT,
  parent_id BIGINT DEFAULT NULL,
  name VARCHAR(50) NOT NULL,
  code VARCHAR(100) NOT NULL,
  path VARCHAR(255) DEFAULT NULL,
  component VARCHAR(255) DEFAULT NULL,
  icon VARCHAR(100) DEFAULT NULL,
  sort INT DEFAULT 0,
  status INT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_menu_status ON sys_menu(status);

