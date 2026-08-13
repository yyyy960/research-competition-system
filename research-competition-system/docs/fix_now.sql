-- Reset all passwords to admin123 (known working BCrypt hash)
UPDATE sys_user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' WHERE username IN ('admin','student1','teacher1','secretary','leader');
-- Fix secretary role
UPDATE sys_user SET role_id = 3 WHERE username = 'secretary';
-- Fix leader role
UPDATE sys_user SET role_id = 4 WHERE username = 'leader';
