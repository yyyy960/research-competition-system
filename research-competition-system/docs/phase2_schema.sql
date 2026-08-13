SET NAMES utf8mb4;

-- ============================================
-- Module 1: Notification & Progress Tracking
-- ============================================

-- 通知表增加类型和截止日期
ALTER TABLE notification ADD COLUMN notification_type VARCHAR(20) DEFAULT 'system' COMMENT 'system/review/reminder';
ALTER TABLE notification ADD COLUMN deadline DATE DEFAULT NULL COMMENT '截止日期';

-- 成果时间线表
DROP TABLE IF EXISTS achievement_timeline;
CREATE TABLE achievement_timeline (
    id BIGINT NOT NULL AUTO_INCREMENT,
    achievement_type VARCHAR(20) NOT NULL COMMENT 'competition/innovation/copyright/paper',
    achievement_id BIGINT NOT NULL,
    node VARCHAR(50) NOT NULL COMMENT 'submitted/secretary_review/leader_review/archived/returned',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    action VARCHAR(20) NOT NULL COMMENT 'submit/approve/reject/edit',
    comment TEXT COMMENT '审核意见/备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_achievement (achievement_type, achievement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成果审核时间线';

-- ============================================
-- Module 2: Personal Achievement Center
-- ============================================

-- 各成果表增加置顶字段
ALTER TABLE competition_achievement ADD COLUMN is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶';
ALTER TABLE innovation_project ADD COLUMN is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶';
ALTER TABLE software_copyright ADD COLUMN is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶';
ALTER TABLE academic_paper ADD COLUMN is_pinned TINYINT DEFAULT 0 COMMENT '是否置顶';

-- ============================================
-- Module 3: Modify Log
-- ============================================

DROP TABLE IF EXISTS achievement_modify_log;
CREATE TABLE achievement_modify_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    achievement_type VARCHAR(20) NOT NULL,
    achievement_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(50),
    modify_type VARCHAR(20) NOT NULL COMMENT 'create/update/delete',
    before_data TEXT COMMENT '修改前数据JSON',
    after_data TEXT COMMENT '修改后数据JSON',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_achievement (achievement_type, achievement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成果修改日志';
