-- ============================================
-- 科研竞赛管理系统 - 自动建表脚本
-- Spring Boot 启动时自动执行
-- ============================================

-- 1. 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_desc` VARCHAR(100) DEFAULT NULL COMMENT '角色描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
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

-- 3. 学科竞赛成果表
CREATE TABLE IF NOT EXISTS `competition_achievement` (
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
    `is_pinned` TINYINT DEFAULT 0 COMMENT '是否置顶 0=否 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科竞赛成果表';

-- 4. 大学生创新训练计划项目表
CREATE TABLE IF NOT EXISTS `innovation_project` (
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
    `is_pinned` TINYINT DEFAULT 0 COMMENT '是否置顶 0=否 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大学生创新训练计划项目表';

-- 5. 软件著作权表
CREATE TABLE IF NOT EXISTS `software_copyright` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `software_name` VARCHAR(200) NOT NULL COMMENT '软著名称',
    `organization` VARCHAR(200) DEFAULT NULL COMMENT '所属单位',
    `copyright_owner` VARCHAR(100) DEFAULT NULL COMMENT '著作权人',
    `registration_number` VARCHAR(50) DEFAULT NULL COMMENT '登记号',
    `registration_date` DATE DEFAULT NULL COMMENT '登记日期',
    `certificate_file_id` BIGINT DEFAULT NULL COMMENT '证书扫描件文件ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending_review' COMMENT '状态',
    `submit_user_id` BIGINT NOT NULL COMMENT '提交用户ID',
    `is_pinned` TINYINT DEFAULT 0 COMMENT '是否置顶 0=否 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='软件著作权表';

-- 6. 学术论文表
CREATE TABLE IF NOT EXISTS `academic_paper` (
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
    `is_pinned` TINYINT DEFAULT 0 COMMENT '是否置顶 0=否 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_submit_user` (`submit_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学术论文表';

-- 7. 审核记录表
CREATE TABLE IF NOT EXISTS `review_record` (
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
CREATE TABLE IF NOT EXISTS `sys_file` (
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
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT DEFAULT NULL COMMENT '通知内容',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读 0=未读 1=已读',
    `related_type` VARCHAR(20) DEFAULT NULL COMMENT '关联类型',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联ID',
    `notification_type` VARCHAR(20) DEFAULT 'system' COMMENT '通知类型 system/review/reminder',
    `deadline` DATE DEFAULT NULL COMMENT '截止日期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 9.1 兼容旧库：notification 表若已存在且缺少 phase2 新增列，则补列
--     (continue-on-error=true 时重复添加列的报错会被忽略，保证幂等)
ALTER TABLE `notification` ADD COLUMN `notification_type` VARCHAR(20) DEFAULT 'system' COMMENT '通知类型 system/review/reminder';
ALTER TABLE `notification` ADD COLUMN `deadline` DATE DEFAULT NULL COMMENT '截止日期';

-- 10. 公告表 (init.sql 遗漏，根据 Announcement 实体补充)
CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT DEFAULT NULL COMMENT '公告内容',
    `publisher` VARCHAR(50) DEFAULT NULL COMMENT '发布人',
    `publish_time` DATE DEFAULT NULL COMMENT '发布时间',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶 0=否 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 11. 系统操作日志表
CREATE TABLE IF NOT EXISTS `system_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `operation` VARCHAR(200) DEFAULT NULL COMMENT '操作描述',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `action` VARCHAR(20) DEFAULT NULL COMMENT '操作类型 LOGIN/SUBMIT/UPDATE/DELETE/REVIEW/QUERY/UPLOAD/EXPORT/WITHDRAW',
    `params` VARCHAR(500) DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '请求IP',
    `status` VARCHAR(10) DEFAULT NULL COMMENT '执行状态 OK/FAIL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- 12. 成果审核时间线表 (phase2 引入，被 TimelineService 写入；缺失会导致成果提交 500)
CREATE TABLE IF NOT EXISTS `achievement_timeline` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `achievement_type` VARCHAR(20) NOT NULL COMMENT '成果类型 competition/innovation/copyright/paper',
    `achievement_id` BIGINT NOT NULL COMMENT '成果ID',
    `node` VARCHAR(50) NOT NULL COMMENT '节点 submitted/secretary_review/leader_review/archived/returned',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `action` VARCHAR(20) NOT NULL COMMENT '动作 submit/approve/reject/edit',
    `comment` TEXT DEFAULT NULL COMMENT '审核意见/备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_achievement` (`achievement_type`, `achievement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成果审核时间线';

-- 13. 成果修改日志表 (phase2 引入，被 ModifyLogService 写入)
CREATE TABLE IF NOT EXISTS `achievement_modify_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `achievement_type` VARCHAR(20) NOT NULL COMMENT '成果类型',
    `achievement_id` BIGINT NOT NULL COMMENT '成果ID',
    `modify_type` VARCHAR(20) NOT NULL COMMENT '修改类型 create/update/delete',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `before_data` TEXT DEFAULT NULL COMMENT '修改前数据JSON',
    `after_data` TEXT DEFAULT NULL COMMENT '修改后数据JSON',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_achievement` (`achievement_type`, `achievement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成果修改日志';

-- 14. CCF推荐国际学术会议和期刊目录
CREATE TABLE IF NOT EXISTS `ccf_venue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `venue_type` VARCHAR(20) NOT NULL COMMENT '类型: journal/conference',
    `area` VARCHAR(100) NOT NULL COMMENT '研究方向',
    `level` CHAR(1) NOT NULL COMMENT 'CCF等级: A/B/C',
    `abbreviation` VARCHAR(100) NOT NULL COMMENT '简称',
    `full_name` VARCHAR(300) NOT NULL COMMENT '全称',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `url` VARCHAR(500) DEFAULT NULL COMMENT '网址',
    PRIMARY KEY (`id`),
    KEY `idx_area_level` (`area`, `level`),
    KEY `idx_type` (`venue_type`),
    KEY `idx_abbreviation` (`abbreviation`),
    FULLTEXT KEY `ft_full_name` (`full_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CCF推荐学术会议和期刊目录';
