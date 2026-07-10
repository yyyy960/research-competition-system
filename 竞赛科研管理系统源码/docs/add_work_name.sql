SET NAMES utf8mb4;

-- ============================================
-- Migration: Add work_name column to competition_achievement
-- Run this against existing databases to add the 获奖作品名称 field
-- ============================================

ALTER TABLE competition_achievement ADD COLUMN work_name VARCHAR(200) DEFAULT NULL COMMENT '获奖作品名称' AFTER award_time;
