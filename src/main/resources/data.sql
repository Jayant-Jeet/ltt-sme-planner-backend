-- Insert predefined activities with categories and durations based on the provided list
INSERT INTO activities (name, description, category, duration_in_hours, is_variable_duration) VALUES 
('Lateral Calendar Training Full Day', 'Full day lateral calendar training session', 'CALENDAR_TRAINING', 9.0, FALSE),
('Lateral Calendar Training Half Day', 'Half day lateral calendar training session', 'CALENDAR_TRAINING', 4.5, FALSE),
('Blended Learning', 'Blended learning sessions combining online and offline methods', 'BLENDED', 2.0, FALSE),
('Adhoc Training', 'Adhoc training sessions as per requirements', 'ADHOC_TRAINING', 4.5, FALSE),
('Byte sized', 'Short byte-sized learning sessions', 'BYTE_SIZED', 2.0, FALSE),
('Content Development', 'Development of training content and materials', 'CONTENT_DEVELOPMENT', NULL, TRUE),
('Evaluation', 'Evaluation and assessment activities', 'EVALUATION', NULL, TRUE),
('Skill Upgrade', 'Skills upgrade and enhancement programs', 'SKILL_UPGRADE', NULL, TRUE),
('Program and Session Planning', 'Planning and organizing training programs and sessions', 'MANAGEMENT', NULL, TRUE),
('Public Holiday', 'Public holiday time off', 'TIME_OFF', NULL, TRUE),
('Leave', 'Personal leave and time off', 'TIME_OFF', NULL, TRUE),
('Location Holiday', 'Location-specific holiday time off', 'TIME_OFF', NULL, TRUE),
('Optional Holiday', 'Optional holiday time off', 'TIME_OFF', NULL, TRUE),
('Others', 'Miscellaneous activities not covered by other categories', 'MISCELLANEOUS', NULL, TRUE);

-- Insert sample users with different roles (password is 'password' encrypted with BCrypt)
-- First, delete all existing users to ensure clean state
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;

-- First insert users with no supervisor (supervisor_id = NULL)
INSERT INTO users (name, email, password, role, supervisor_id) VALUES ('Jane Smith', 'jane.smith@edulearnorg.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SUPERVISOR', NULL);
INSERT INTO users (name, email, password, role, supervisor_id) VALUES ('Mike Johnson', 'mike.johnson@edulearnorg.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'LEAD', NULL);
INSERT INTO users (name, email, password, role, supervisor_id) VALUES ('Robert Brown', 'robert.brown@edulearnorg.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SUPERVISOR', NULL);
INSERT INTO users (name, email, password, role, supervisor_id) VALUES ('Emma Davis', 'emma.davis@edulearnorg.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'LEAD', NULL);
-- Then insert users who reference supervisors (Jane Smith has id=1, so supervisor_id=1)
INSERT INTO users (name, email, password, role, supervisor_id) VALUES ('John Doe', 'john.doe@edulearnorg.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SME', 1);
INSERT INTO users (name, email, password, role, supervisor_id) VALUES ('Sarah Wilson', 'sarah.wilson@edulearnorg.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SME', 1);

-- Insert sample schedules using the new predefined activities
INSERT INTO schedules (user_id, from_date, to_date, from_time, to_time, activity_id, activity_name, description, created_at, updated_at) VALUES 
(5, '2025-07-15', '2025-07-15', '09:00:00', '18:00:00', 1, 'Lateral Calendar Training Full Day', 'Full day Java Spring Boot Training', NOW(), NOW()),
(5, '2025-07-16', '2025-07-16', '14:00:00', '16:00:00', 3, 'Blended Learning', 'Blended learning session on microservices', NOW(), NOW()),
(1, '2025-07-15', '2025-07-15', '10:00:00', '14:30:00', 2, 'Lateral Calendar Training Half Day', 'React.js Half Day Training', NOW(), NOW()),
(6, '2025-07-17', '2025-07-17', '13:00:00', '15:00:00', 5, 'Byte sized', 'Quick byte-sized session on Docker', NOW(), NOW()),
(5, '2025-07-18', '2025-07-18', '10:00:00', '14:30:00', 4, 'Adhoc Training', 'Adhoc training on database optimization', NOW(), NOW());

-- Insert sample SME activity groups for testing monthly effort details
INSERT INTO sme_activity_groups (sme_user_id, activity_id, category, total_hours_allocated, total_sessions, month_year) VALUES 
-- John Doe (SME ID: 5) activities for July 2025
(5, 1, 'CALENDAR_TRAINING', 9.0, 1, '2025-07'),
(5, 3, 'BLENDED', 2.0, 1, '2025-07'),
(5, 4, 'ADHOC_TRAINING', 4.5, 1, '2025-07'),
-- Sarah Wilson (SME ID: 6) activities for July 2025
(6, 5, 'BYTE_SIZED', 2.0, 1, '2025-07'),
-- Jane Smith (Supervisor ID: 1) activities for July 2025 (for testing)
(1, 2, 'CALENDAR_TRAINING', 4.5, 1, '2025-07'),
-- Additional activities for better testing
(5, 5, 'BYTE_SIZED', 4.0, 2, '2025-07'),
(6, 1, 'CALENDAR_TRAINING', 9.0, 1, '2025-07'),
(6, 3, 'BLENDED', 6.0, 3, '2025-07');
