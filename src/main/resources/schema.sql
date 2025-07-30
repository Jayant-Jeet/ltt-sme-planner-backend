-- Schema creation for L&TT SME Planner (MySQL)
CREATE DATABASE IF NOT EXISTS `ltt-sme-planner`; 
USE `ltt-sme-planner`; SHOW DATABASES;
-- Create Activities table
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(50) NOT NULL,
    duration_in_hours DECIMAL(5,2),
    is_variable_duration BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Users table (formerly SMEs table)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'SME',
    supervisor_id BIGINT,
    FOREIGN KEY (supervisor_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Schedules table
CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    from_time TIME NOT NULL,
    to_time TIME NOT NULL,
    activity_id BIGINT NOT NULL,
    activity_name VARCHAR(255),
    description VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create SME Activity Groups table
CREATE TABLE IF NOT EXISTS sme_activity_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sme_user_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    total_hours_allocated DECIMAL(8,2) DEFAULT 0.0,
    total_sessions INT DEFAULT 0,
    month_year VARCHAR(7) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sme_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE,
    UNIQUE KEY unique_sme_activity_month (sme_user_id, activity_id, month_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
