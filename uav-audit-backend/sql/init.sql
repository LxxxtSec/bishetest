-- 无人机交通巡检系统操作安全审计平台 - 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS uav_audit DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE uav_audit;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'operator' COMMENT '角色: admin/operator/auditor',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    operation_object VARCHAR(200) COMMENT '操作对象',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(200) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_status INT COMMENT '响应状态码',
    hash_value VARCHAR(64) COMMENT '哈希值(防篡改)',
    prev_hash VARCHAR(64) COMMENT '前一条日志哈希',
    log_time DATETIME NOT NULL COMMENT '操作时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_operation_type (operation_type),
    INDEX idx_log_time (log_time),
    INDEX idx_hash (hash_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 安全告警表
CREATE TABLE IF NOT EXISTS security_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    alert_type VARCHAR(50) NOT NULL COMMENT '告警类型',
    alert_level VARCHAR(20) NOT NULL COMMENT '告警级别: low/medium/high/critical',
    alert_message VARCHAR(500) NOT NULL COMMENT '告警消息',
    related_user_id BIGINT COMMENT '关联用户ID',
    related_log_id BIGINT COMMENT '关联日志ID',
    is_resolved TINYINT DEFAULT 0 COMMENT '是否解决: 0否 1是',
    resolve_time DATETIME COMMENT '解决时间',
    resolve_by VARCHAR(50) COMMENT '处理人',
    resolve_comment VARCHAR(500) COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_alert_type (alert_type),
    INDEX idx_alert_level (alert_level),
    INDEX idx_is_resolved (is_resolved),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全告警表';

-- 初始化管理员用户 (密码: admin123，使用BCrypt加密)
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 1),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '操作员', 'operator', 1),
('auditor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '审计员', 'auditor', 1);

-- 设置自增起始值
ALTER TABLE sys_user AUTO_INCREMENT = 1000;
ALTER TABLE audit_log AUTO_INCREMENT = 10000;
ALTER TABLE security_alert AUTO_INCREMENT = 1000;
