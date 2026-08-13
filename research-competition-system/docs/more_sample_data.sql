SET NAMES utf8mb4;

-- ============================================
-- 大创项目数据
-- ============================================
INSERT INTO innovation_project (project_name, project_level, project_type, advisor, members, start_time, status, submit_user_id, create_time, update_time) VALUES
('基于深度学习的智能农业病虫害检测系统', 'national', 'innovation', '张伟', '李明,王芳,赵强', '2024-03-15', 'archived', 2, '2024-03-20 09:00:00', '2024-09-15 10:00:00'),
('区块链技术在农产品溯源中的应用研究', 'national', 'innovation', '刘洋', '陈静,周磊,吴凡', '2024-03-10', 'archived', 3, '2024-03-15 10:00:00', '2024-10-01 14:00:00'),
('基于边缘计算的智慧校园安全监控平台', 'provincial', 'entrepreneurship', '赵斌', '刘思雨,孙悦,林峰', '2024-04-20', 'archived', 2, '2024-04-25 09:00:00', '2024-11-20 16:00:00'),
('面向老年群体的智能语音助手应用开发', 'provincial', 'innovation', '王丽', '黄晓明,李娜', '2024-05-10', 'under_review', 3, '2024-05-15 10:00:00', '2024-05-15 10:00:00'),
('基于NLP的高校图书馆智能问答机器人', 'school', 'innovation', '陈明', '张强,李伟,赵芳', '2024-06-01', 'pending_review', 2, '2024-06-05 08:00:00', '2024-06-05 08:00:00'),
('大数据驱动的校园舆情监测与分析系统', 'school', 'practice', '周杰', '吴思,周婷,陈磊', '2024-06-15', 'pending_review', 3, '2024-06-20 09:00:00', '2024-06-20 09:00:00'),
('基于联邦学习的隐私保护推荐系统研究', 'national', 'innovation', '李华', '王婷,赵磊,张敏,黄磊', '2024-01-20', 'archived', 2, '2024-01-25 10:00:00', '2024-08-30 11:00:00'),
('基于知识图谱的在线教育个性化推荐平台', 'provincial', 'entrepreneurship', '张伟', '李思,王浩,刘洋', '2024-04-01', 'archived', 3, '2024-04-05 14:00:00', '2024-10-20 09:00:00');

-- ============================================
-- 软件著作权数据
-- ============================================
INSERT INTO software_copyright (software_name, organization, copyright_owner, registration_number, registration_date, status, submit_user_id, create_time, update_time) VALUES
('智慧农业病虫害智能识别系统V1.0', '怀化学院', '李明', '2024SR0187698', '2024-06-15', 'archived', 2, '2024-06-20 10:00:00', '2024-07-01 14:00:00'),
('校园安全监控边缘计算平台V1.0', '怀化学院', '赵斌', '2024SR0278934', '2024-08-20', 'archived', 3, '2024-08-25 09:00:00', '2024-09-10 11:00:00'),
('基于深度学习的智能语音助手软件V1.0', '怀化学院', '王丽', '2024SR0356120', '2024-10-10', 'archived', 2, '2024-10-15 08:30:00', '2024-11-01 16:00:00'),
('高校图书馆智能问答系统V1.0', '怀化学院', '陈明', '2025SR0123456', '2025-01-20', 'pending_review', 2, '2025-01-25 10:00:00', '2025-01-25 10:00:00'),
('校园舆情监测与分析系统V1.0', '怀化学院', '周杰', '2025SR0187654', '2025-02-15', 'pending_review', 3, '2025-02-20 09:00:00', '2025-02-20 09:00:00'),
('隐私保护推荐系统V1.0', '怀化学院', '李华', '2024SR0421567', '2024-12-01', 'archived', 2, '2024-12-05 14:00:00', '2024-12-20 10:00:00'),
('在线教育个性化推荐平台V1.0', '怀化学院', '张伟', '2024SR0398745', '2024-11-15', 'archived', 3, '2024-11-20 10:00:00', '2024-12-10 09:00:00');

-- ============================================
-- 学术论文数据
-- ============================================
INSERT INTO academic_paper (title, submission_date, acceptance_date, journal_name, keywords, journal_level, authors, status, submit_user_id, create_time, update_time) VALUES
('A Novel Deep Learning Approach for Agricultural Pest Detection Using Edge Computing', '2024-01-10', '2024-03-20', 'IEEE Access', '深度学习,病虫害检测,边缘计算,智慧农业', 'SCI二区', '李明,张伟,王芳', 'archived', 2, '2024-03-25 10:00:00', '2024-04-15 14:00:00'),
('Blockchain-Based Traceability Framework for Agricultural Supply Chain Management', '2024-02-15', '2024-05-10', 'Computers and Electronics in Agriculture', '区块链,农产品溯源,供应链管理', 'SCI一区', '陈静,刘洋,周磊', 'archived', 3, '2024-05-15 09:00:00', '2024-06-01 11:00:00'),
('Edge Computing Empowered Smart Campus Security Surveillance System', '2024-03-01', '2024-06-15', 'IEEE Internet of Things Journal', '边缘计算,智慧校园,安防监控,IoT', 'CCF C类期刊', '赵斌,刘思雨,孙悦', 'archived', 2, '2024-06-20 08:30:00', '2024-07-10 10:00:00'),
('Federated Learning for Privacy-Preserving Recommendation Systems: A Comprehensive Survey', '2024-04-10', '2024-07-25', 'Knowledge-Based Systems', '联邦学习,隐私保护,推荐系统', 'CCF C类期刊', '李华,王婷,赵磊', 'archived', 2, '2024-08-01 14:00:00', '2024-08-20 09:00:00'),
('Knowledge Graph Enhanced Personalized Recommendation for Online Education Platforms', '2024-05-20', '2024-08-30', 'Expert Systems with Applications', '知识图谱,个性化推荐,在线教育', 'CCF C类期刊', '张伟,李思,王浩', 'archived', 3, '2024-09-05 10:00:00', '2024-09-25 16:00:00'),
('Lightweight Neural Network Architecture for Real-Time Object Detection on Mobile Devices', '2024-06-01', '2024-09-10', 'Neurocomputing', '轻量级神经网络,目标检测,移动端', 'CCF C类期刊', '李明,赵强,陈磊', 'under_review', 2, '2024-09-15 09:00:00', '2024-09-15 09:00:00'),
('A Survey on Large Language Models for Code Generation: Techniques, Challenges and Opportunities', '2024-07-15', '2024-10-20', 'IEEE Transactions on Software Engineering', '大语言模型,代码生成,软件工程', 'CCF A类期刊', '刘洋,吴凡,黄晓明', 'pending_review', 3, '2024-10-25 10:00:00', '2024-10-25 10:00:00'),
('Multi-Modal Sentiment Analysis Based on Cross-Attention Fusion Mechanism', '2024-08-01', NULL, 'Information Fusion', '多模态,情感分析,注意力机制', 'SCI一区', '王芳,李娜,周婷', 'pending_review', 2, '2024-08-05 09:00:00', '2024-08-05 09:00:00');
