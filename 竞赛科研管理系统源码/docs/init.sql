-- ============================================
-- 科研竞赛管理系统 - 数据库初始化脚本
-- Database: cms_db
-- ============================================

CREATE DATABASE IF NOT EXISTS cms_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cms_db;

-- 1. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_desc` VARCHAR(100) DEFAULT NULL COMMENT '角色描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO `sys_role` (`id`, `role_name`, `role_desc`) VALUES
(1, 'ADMIN', '系统管理员'),
(2, 'STUDENT', '学生/教师'),
(3, 'SECRETARY', '科研秘书'),
(4, 'LEADER', '学院领导');

-- 2. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0=禁用 1=启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认密码: admin123 (BCrypt)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_id`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1),
(2, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 2, 1),
(3, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 2, 1),
(4, 'secretary', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王秘书', 3, 1),
(5, 'leader', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈院长', 4, 1);

-- 3. 学科竞赛成果表
DROP TABLE IF EXISTS `competition_achievement`;
CREATE TABLE `competition_achievement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `competition_category` VARCHAR(10) NOT NULL COMMENT '竞赛类别 A/B/C',
    `competition_name` VARCHAR(200) NOT NULL COMMENT '竞赛名称',
    `host_unit` VARCHAR(200) DEFAULT NULL COMMENT '主办单位',
    `organizer_unit` VARCHAR(200) DEFAULT NULL COMMENT '承办单位',
    `award_unit` VARCHAR(200) DEFAULT NULL COMMENT '获奖单位',
    `award_level` VARCHAR(50) NOT NULL COMMENT '获奖级别 national/provincial/city/school/college',
    `award_grade` VARCHAR(20) NOT NULL COMMENT '获奖等级 first/second/third',
    `award_time` DATE DEFAULT NULL COMMENT '获奖时间',
    `work_name` VARCHAR(200) DEFAULT NULL COMMENT '获奖作品名称',
    `advisor` VARCHAR(50) DEFAULT NULL COMMENT '指导教师',
    `participants` TEXT DEFAULT NULL COMMENT '参赛选手',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending_review' COMMENT '状态',
    `submit_user_id` BIGINT NOT NULL COMMENT '提交用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科竞赛成果表';

-- 4. 大学生创新训练计划项目表
DROP TABLE IF EXISTS `innovation_project`;
CREATE TABLE `innovation_project` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `project_name` VARCHAR(200) NOT NULL COMMENT '项目名称',
    `project_level` VARCHAR(50) NOT NULL COMMENT '立项级别 national/provincial/school/college',
    `project_type` VARCHAR(50) NOT NULL COMMENT '立项类型 innovation/entrepreneurship/practice',
    `advisor` VARCHAR(50) DEFAULT NULL COMMENT '指导教师',
    `members` TEXT DEFAULT NULL COMMENT '立项人员',
    `start_time` DATE DEFAULT NULL COMMENT '立项时间',
    `proposal_file_id` BIGINT DEFAULT NULL COMMENT '立项申报书文件ID',
    `final_material_file_id` BIGINT DEFAULT NULL COMMENT '结题材料文件ID',
    `certificate_file_id` BIGINT DEFAULT NULL COMMENT '结题证书文件ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending_review' COMMENT '状态',
    `submit_user_id` BIGINT NOT NULL COMMENT '提交用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大学生创新训练计划项目表';

-- 5. 软件著作权表
DROP TABLE IF EXISTS `software_copyright`;
CREATE TABLE `software_copyright` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `software_name` VARCHAR(200) NOT NULL COMMENT '软著名称',
    `organization` VARCHAR(200) DEFAULT NULL COMMENT '所属单位',
    `copyright_owner` VARCHAR(100) DEFAULT NULL COMMENT '著作权人',
    `registration_number` VARCHAR(50) DEFAULT NULL COMMENT '登记号',
    `registration_date` DATE DEFAULT NULL COMMENT '登记日期',
    `certificate_file_id` BIGINT DEFAULT NULL COMMENT '证书扫描件文件ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending_review' COMMENT '状态',
    `submit_user_id` BIGINT NOT NULL COMMENT '提交用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_registration_number` (`registration_number`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='软件著作权表';

-- 6. 学术论文表
DROP TABLE IF EXISTS `academic_paper`;
CREATE TABLE `academic_paper` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(300) NOT NULL COMMENT '论文标题',
    `submission_date` DATE DEFAULT NULL COMMENT '投稿时间',
    `acceptance_date` DATE DEFAULT NULL COMMENT '录用时间',
    `journal_name` VARCHAR(200) DEFAULT NULL COMMENT '期刊名称',
    `keywords` VARCHAR(300) DEFAULT NULL COMMENT '研究方向关键词',
    `journal_level` VARCHAR(100) NOT NULL COMMENT '期刊级别',
    `authors` TEXT DEFAULT NULL COMMENT '论文作者',
    `draft_file_id` BIGINT DEFAULT NULL COMMENT '投稿初稿文件ID',
    `final_file_id` BIGINT DEFAULT NULL COMMENT '录用终稿文件ID',
    `review_comment_file_id` BIGINT DEFAULT NULL COMMENT '审稿意见及回复文件ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending_review' COMMENT '状态',
    `submit_user_id` BIGINT NOT NULL COMMENT '提交用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学术论文表';

-- 7. 审核记录表
DROP TABLE IF EXISTS `review_record`;
CREATE TABLE `review_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `achievement_type` VARCHAR(20) NOT NULL COMMENT '成果类型 competition/innovation/copyright/paper',
    `achievement_id` BIGINT NOT NULL COMMENT '成果ID',
    `reviewer_id` BIGINT NOT NULL COMMENT '审核人ID',
    `review_level` VARCHAR(20) NOT NULL COMMENT '审核级别 secretary/leader',
    `status` VARCHAR(20) NOT NULL COMMENT '审核状态 approved/rejected',
    `comment` TEXT DEFAULT NULL COMMENT '审核意见',
    `review_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_achievement` (`achievement_type`, `achievement_id`),
    KEY `idx_reviewer` (`reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- 8. 文件表
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `stored_name` VARCHAR(255) NOT NULL COMMENT '存储文件名(UUID)',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `file_type` VARCHAR(20) NOT NULL COMMENT '文件类型 image/document/archive/video',
    `file_ext` VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
    `achievement_type` VARCHAR(20) DEFAULT NULL COMMENT '关联成果类型',
    `achievement_id` BIGINT DEFAULT NULL COMMENT '关联成果ID',
    `upload_user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_achievement` (`achievement_type`, `achievement_id`),
    KEY `idx_upload_user` (`upload_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 9. 通知表
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT DEFAULT NULL COMMENT '通知内容',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读 0=未读 1=已读',
    `related_type` VARCHAR(20) DEFAULT NULL COMMENT '关联类型',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
