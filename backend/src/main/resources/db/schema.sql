-- 强制删除旧数据库并重新创建
DROP DATABASE IF EXISTS `root`;
CREATE DATABASE `root` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `root`;

-- 套餐表
CREATE TABLE IF NOT EXISTS `plan` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT '套餐名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '套餐编码',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格（元）',
    `original_price` DECIMAL(10,2) COMMENT '原价',
    `credits` INT NOT NULL COMMENT 'Credits数量',
    `duration` INT NOT NULL DEFAULT 30 COMMENT '有效期（天）',
    `features` TEXT COMMENT '功能特性（JSON）',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `plan_id` BIGINT NOT NULL COMMENT '套餐ID',
    `plan_name` VARCHAR(50) NOT NULL COMMENT '套餐名称',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额（元）',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待支付 1-已支付 2-已取消',
    `pay_type` VARCHAR(20) COMMENT '支付方式：WECHAT_NATIVE',
    `transaction_id` VARCHAR(64) COMMENT '微信支付订单号',
    `pay_time` DATETIME COMMENT '支付时间',
    `expire_time` DATETIME COMMENT '订单过期时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
