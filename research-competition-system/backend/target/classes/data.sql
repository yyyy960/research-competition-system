-- ============================================
-- 科研竞赛管理系统 - 初始数据脚本
-- Spring Boot 启动时自动执行
-- 使用 INSERT IGNORE 确保重复执行不会报错
-- ============================================

-- ======== 角色数据 ========
INSERT IGNORE INTO `sys_role` (`id`, `role_name`, `role_desc`) VALUES
(1, 'ADMIN', '系统管理员'),
(2, 'STUDENT', '学生/教师'),
(3, 'SECRETARY', '科研秘书'),
(4, 'LEADER', '学院领导');

-- ======== 用户数据 (默认密码均为 admin123) ========
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_id`, `status`) VALUES
(1, 'admin', '$2a$10$L65YNTTCJSlo8bikxJ52D.kJkAXjpU.DdgARVEGAOCTWLw8tm2P6.', '系统管理员', 1, 1),
(2, 'student1', '$2a$10$L65YNTTCJSlo8bikxJ52D.kJkAXjpU.DdgARVEGAOCTWLw8tm2P6.', '张三', 2, 1),
(3, 'teacher1', '$2a$10$L65YNTTCJSlo8bikxJ52D.kJkAXjpU.DdgARVEGAOCTWLw8tm2P6.', '李四', 2, 1),
(4, 'secretary', '$2a$10$L65YNTTCJSlo8bikxJ52D.kJkAXjpU.DdgARVEGAOCTWLw8tm2P6.', '王秘书', 3, 1),
(5, 'leader', '$2a$10$L65YNTTCJSlo8bikxJ52D.kJkAXjpU.DdgARVEGAOCTWLw8tm2P6.', '陈院长', 4, 1);

-- ======== 学科竞赛成果 (12条) ========
INSERT IGNORE INTO competition_achievement (id, competition_category, competition_name, host_unit, organizer_unit, award_unit, award_level, award_grade, award_time, work_name, advisor, participants, status, submit_user_id, is_pinned, create_time, update_time) VALUES
(1, 'A', 'ACM-ICPC国际大学生程序设计竞赛亚洲区域赛', 'ACM', '上海交通大学', '怀化学院', 'national', 'first', '2024-11-20', '基于深度学习的智能路径规划算法', '张伟', '李明,王芳,赵强', 'archived', 2, 0, '2024-12-01 10:00:00', '2024-12-15 14:00:00'),
(2, 'A', '蓝桥杯全国软件和信息技术专业人才大赛', '工业和信息化部人才交流中心', '蓝桥杯组委会', '怀化学院', 'national', 'second', '2024-06-15', '分布式微服务架构的校园管理系统', '刘洋', '陈静,周磊', 'archived', 2, 0, '2024-06-20 09:00:00', '2024-07-05 11:00:00'),
(3, 'A', '全国大学生数学建模竞赛', '中国工业与应用数学学会', '全国大学生数学建模竞赛组委会', '怀化学院', 'national', 'first', '2024-09-12', '基于机器学习的城市交通流预测模型', '王丽', '黄晓明,李娜,张伟', 'archived', 3, 0, '2024-09-20 08:30:00', '2024-10-10 16:00:00'),
(4, 'B', '中国大学生计算机设计大赛', '教育部高等学校计算机类专业教学指导委员会', '中国大学生计算机设计大赛组委会', '怀化学院', 'national', 'first', '2024-07-28', '智慧校园一站式服务平台', '赵斌', '刘思雨,吴凡', 'under_review', 2, 0, '2024-08-01 14:00:00', '2024-08-10 10:00:00'),
(5, 'B', '全国大学生电子设计竞赛', '教育部高等教育司', '全国大学生电子设计竞赛组委会', '怀化学院', 'provincial', 'first', '2024-08-05', '基于STM32的智能环境监测系统', '陈明', '孙悦,周强,林峰', 'archived', 3, 0, '2024-08-10 11:00:00', '2024-08-25 09:00:00'),
(6, 'B', '挑战杯全国大学生课外学术科技作品竞赛', '共青团中央', '挑战杯竞赛组委会', '怀化学院', 'provincial', 'second', '2024-05-15', '面向乡村振兴的农产品电商平台', '李华', '王婷,赵磊,张敏', 'archived', 2, 0, '2024-05-20 15:00:00', '2024-06-05 13:00:00'),
(7, 'C', '全国大学生信息安全竞赛', '教育部高等学校网络空间安全专业教学指导委员会', '全国大学生信息安全竞赛组委会', '怀化学院', 'national', 'third', '2024-10-08', '基于区块链的隐私数据保护系统', '周杰', '李思,王浩,刘洋', 'archived', 3, 0, '2024-10-15 10:30:00', '2024-10-30 08:00:00'),
(8, 'C', '湖南省大学生程序设计竞赛', '湖南省教育厅', '湖南省大学生程序设计竞赛组委会', '怀化学院', 'provincial', 'first', '2024-11-05', '高性能并发计算框架的设计与实现', '张伟', '陈明,赵丽,黄磊', 'pending_review', 2, 0, '2024-11-10 09:00:00', '2024-11-10 09:00:00'),
(9, 'C', '怀化学院大学生创新创业大赛', '怀化学院', '怀化学院教务处', '计算机与人工智能学院', 'school', 'second', '2024-12-01', '校园二手书交易小程序', '刘洋', '吴思,周婷', 'pending_review', 2, 0, '2024-12-05 16:00:00', '2024-12-05 16:00:00'),
(10, 'A', '全国大学生机器人大赛RoboMaster', '共青团中央', '大疆创新科技有限公司', '怀化学院', 'national', 'second', '2024-07-10', '全向移动机器人控制系统', '王丽', '张强,李伟,赵芳,陈磊', 'archived', 3, 0, '2024-07-15 10:00:00', '2024-08-01 14:00:00'),
(11, 'B', '全国大学生物联网设计竞赛', '教育部高等学校计算机类专业教学指导委员会', '全国大学生物联网设计竞赛组委会', '怀化学院', 'national', 'first', '2024-08-22', '基于NB-IoT的智慧农业环境监控系统', '赵斌', '刘洋,孙悦', 'archived', 2, 0, '2024-08-28 09:00:00', '2024-09-10 11:00:00'),
(12, 'C', '中国高校计算机大赛-团体程序设计天梯赛', '教育部高等学校计算机类专业教学指导委员会', '中国高校计算机大赛组委会', '怀化学院', 'national', 'third', '2024-04-20', '团体程序设计天梯赛参赛作品', '陈明', '李明,王芳,赵强,周磊,陈静', 'archived', 3, 0, '2024-04-25 08:00:00', '2024-05-10 10:00:00');

-- ======== 大创项目 (5条) ========
INSERT IGNORE INTO innovation_project (id, project_name, project_level, project_type, advisor, members, start_time, status, submit_user_id, is_pinned, create_time, update_time) VALUES
(1, '基于深度学习的智慧校园安防系统', 'national', 'innovation', '张伟', '李明,王芳,赵强', '2024-03-01', 'archived', 2, 0, '2024-03-15 09:00:00', '2024-09-20 14:00:00'),
(2, '面向乡村振兴的农产品溯源区块链平台', 'provincial', 'entrepreneurship', '刘洋', '陈静,周磊,吴思', '2024-04-15', 'under_review', 2, 1, '2024-04-20 10:00:00', '2024-08-10 11:00:00'),
(3, '基于物联网的智能农业大棚监控系统', 'national', 'practice', '王丽', '黄晓明,李娜,孙悦', '2024-05-10', 'archived', 3, 0, '2024-05-15 08:30:00', '2024-10-05 16:00:00'),
(4, 'AI辅助的个性化学习路径推荐平台', 'school', 'innovation', '赵斌', '刘思雨,吴凡', '2024-09-01', 'pending_review', 2, 0, '2024-09-05 14:00:00', '2024-09-05 14:00:00'),
(5, '基于大数据的校园舆情分析系统', 'provincial', 'practice', '陈明', '周强,林峰,赵磊', '2024-06-20', 'returned', 3, 0, '2024-06-25 11:00:00', '2024-08-15 09:00:00');

-- ======== 软件著作权 (5条) ========
INSERT IGNORE INTO software_copyright (id, software_name, organization, copyright_owner, registration_number, registration_date, status, submit_user_id, is_pinned, create_time, update_time) VALUES
(1, '智能路径规划算法软件V1.0', '计算机与人工智能学院', '李明', '2024SR0880001', '2024-06-15', 'archived', 2, 0, '2024-06-20 10:00:00', '2024-07-05 14:00:00'),
(2, '校园二手交易平台系统V2.0', '计算机与人工智能学院', '陈静', '2024SR0880002', '2024-08-20', 'archived', 2, 1, '2024-08-25 09:00:00', '2024-09-10 11:00:00'),
(3, '智慧农业环境监控系统V1.0', '计算机与人工智能学院', '黄晓明', '2024SR0880003', '2024-09-10', 'under_review', 3, 0, '2024-09-15 08:30:00', '2024-09-20 10:00:00'),
(4, '校园舆情数据分析平台V1.0', '计算机与人工智能学院', '周强', '2024SR0880004', '2024-10-05', 'pending_review', 3, 0, '2024-10-10 14:00:00', '2024-10-10 14:00:00'),
(5, '区块链隐私数据保护系统V1.0', '计算机与人工智能学院', '李思', '2024SR0880005', '2024-11-01', 'pending_review', 2, 0, '2024-11-05 16:00:00', '2024-11-05 16:00:00');

-- ======== 学术论文 (5条) ========
INSERT IGNORE INTO academic_paper (id, title, submission_date, acceptance_date, journal_name, keywords, journal_level, authors, status, submit_user_id, is_pinned, create_time, update_time) VALUES
(1, 'Deep Reinforcement Learning for Intelligent Traffic Flow Prediction', '2024-01-15', '2024-03-20', 'IEEE Transactions on Intelligent Transportation Systems', '深度学习,交通预测,强化学习', 'CCF-A', '李明,张伟,王芳', 'archived', 2, 0, '2024-01-20 10:00:00', '2024-04-01 14:00:00'),
(2, 'Blockchain-based Privacy-Preserving Data Sharing in IoT', '2024-02-10', '2024-04-15', 'IEEE Internet of Things Journal', '区块链,隐私保护,物联网', 'CCF-B', '李思,王浩,刘洋', 'archived', 3, 0, '2024-02-15 09:00:00', '2024-05-01 11:00:00'),
(3, 'A Novel Approach to Program Synthesis using Large Language Models', '2024-05-20', '2024-07-10', 'ACM SIGPLAN Notices', '程序合成,大语言模型,软件工程', 'CCF-A', '赵强,周磊,陈静', 'under_review', 2, 1, '2024-05-25 08:30:00', '2024-07-15 10:00:00'),
(4, 'Lightweight Neural Networks for Edge Computing Applications', '2024-08-01', null, 'Journal of Systems Architecture', '边缘计算,轻量级神经网络,嵌入式系统', 'CCF-B', '黄晓明,李娜', 'pending_review', 3, 0, '2024-08-05 14:00:00', '2024-08-05 14:00:00'),
(5, 'Survey on Multi-Agent Reinforcement Learning for Resource Allocation', '2024-07-15', '2024-09-01', 'ACM Computing Surveys', '多智能体强化学习,资源分配,综述', 'CCF-A', '孙悦,林峰,赵磊', 'archived', 3, 0, '2024-07-20 10:00:00', '2024-09-10 16:00:00');

-- ======== 系统公告 (3条) ========
INSERT IGNORE INTO announcement (id, title, content, publisher, publish_time, is_top, create_time) VALUES
(1, '关于开展2024年度科研成果申报工作的通知', '各二级学院、各部门：\n\n根据学校科研管理工作安排，现启动2024年度科研成果申报工作。本次申报涵盖学科竞赛、大创项目、软件著作权、学术论文四个类别。\n\n请各位老师在2024年12月31日前完成成果录入和材料提交。\n\n特此通知。', '系统管理员', '2024-11-01', 1, '2024-11-01 09:00:00'),
(2, '关于科研成果审核流程的说明', '各位老师、同学：\n\n为进一步规范科研成果管理，现将审核流程说明如下：\n\n1. 提交人录入成果并提交审核\n2. 科研秘书进行初审（待审核→审核中）\n3. 学院领导进行终审（审核中→已归档）\n\n如审核不通过，成果状态将变为"已退回"，提交人可修改后重新提交。', '系统管理员', '2024-10-15', 0, '2024-10-15 14:00:00'),
(3, '2024年度科研统计工作即将开始', '各位老师：\n\n2024年度科研统计工作将于12月中旬开始，请各位老师提前准备好相关材料，确保成果数据的完整性和准确性。', '系统管理员', '2024-12-01', 0, '2024-12-01 10:00:00');
