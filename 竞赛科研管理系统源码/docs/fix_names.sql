SET NAMES utf8mb4;
UPDATE sys_user SET real_name = '系统管理员' WHERE username = 'admin';
UPDATE sys_user SET real_name = '张伟' WHERE username = 'teacher1';
UPDATE sys_user SET real_name = '王秘书' WHERE username = 'secretary';
UPDATE sys_user SET real_name = '陈院长' WHERE username = 'leader';
