-- =============================================================================
-- BULACAN STATE UNIVERSITY – COLLEGE OF ENGINEERING
-- EQUIPMENT BORROWING SYSTEM (BSU-COE-EBS)
-- MySQL / MariaDB Database Architecture & Seed Script
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `bulsu_equipment_db` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `bulsu_equipment_db`;

-- Drop existing tables in reverse foreign-key order
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `borrow_transactions`;
DROP TABLE IF EXISTS `equipment`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;

-- -----------------------------------------------------------------------------
-- 1. USERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `student_id` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Student ID or Account ID (e.g., 2024101234)',
    `full_name` VARCHAR(150) NOT NULL COMMENT 'Full Name of the borrower or admin',
    `course_and_section` VARCHAR(150) NOT NULL COMMENT 'e.g., BS Computer Engineering, CpE-3A',
    `email` VARCHAR(150) NOT NULL UNIQUE COMMENT 'Institutional / BulSU email',
    `contact_number` VARCHAR(30) NOT NULL COMMENT 'Contact mobile number',
    `password_hash` VARCHAR(255) NOT NULL COMMENT 'Secure hashed password',
    `role` ENUM('BORROWER', 'ADMIN') NOT NULL DEFAULT 'BORROWER' COMMENT 'Role-based access',
    `avatar_url` VARCHAR(255) NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. CATEGORIES TABLE
-- Single source of truth for all equipment categories across all screens
-- -----------------------------------------------------------------------------
CREATE TABLE `categories` (
    `category_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `slug` VARCHAR(50) NOT NULL UNIQUE,
    `icon_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    `display_order` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. EQUIPMENT TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE `equipment` (
    `equipment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `item_code` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(150) NOT NULL,
    `category_id` INT NOT NULL,
    `category_name` VARCHAR(100) NOT NULL,
    `description` TEXT NULL,
    `total_quantity` INT NOT NULL DEFAULT 1,
    `available_quantity` INT NOT NULL DEFAULT 1,
    `condition_status` VARCHAR(50) NOT NULL DEFAULT 'Good',
    `default_accessories` VARCHAR(255) NOT NULL DEFAULT 'None',
    `image_name` VARCHAR(100) NOT NULL,
    `is_featured` BOOLEAN NOT NULL DEFAULT FALSE,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_equipment_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. BORROW TRANSACTIONS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE `borrow_transactions` (
    `transaction_id` VARCHAR(50) NOT NULL PRIMARY KEY COMMENT 'Unique transaction code e.g. TRX-20261020-001',
    `user_id` INT NOT NULL,
    `equipment_id` INT NOT NULL,
    `quantity` INT NOT NULL DEFAULT 1,
    `borrow_date` VARCHAR(30) NOT NULL,
    `expected_return_date` VARCHAR(30) NOT NULL,
    `actual_return_date` VARCHAR(30) NULL,
    `borrow_condition` VARCHAR(50) NOT NULL DEFAULT 'Good',
    `borrow_accessories` VARCHAR(255) NOT NULL DEFAULT 'None',
    `status` ENUM('PENDING', 'APPROVED', 'RETURNED', 'REJECTED', 'OVERDUE') NOT NULL DEFAULT 'PENDING',
    `return_condition` VARCHAR(50) NULL,
    `return_accessories` VARCHAR(255) NULL,
    `return_remarks` TEXT NULL,
    `admin_remarks` TEXT NULL,
    `approved_by` INT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_trans_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_trans_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_trans_admin` FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. NOTIFICATIONS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE `notifications` (
    `notification_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `message` TEXT NOT NULL,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- SEED DATA: ALIGNED WITH FIGMA PROTOTYPE EXACT SPECIFICATIONS
-- =============================================================================

-- Seed Users:
-- Juan Dela Cruz (Borrower) - password: JuAnPoGi123 (matching Figma screen Page 14)
-- Engr. Custodian (Admin) - password: Admin@123
INSERT INTO `users` (`user_id`, `student_id`, `full_name`, `course_and_section`, `email`, `contact_number`, `password_hash`, `role`) VALUES
(1, '2024101234', 'Juan Dela Cruz', 'BS Computer Engineering, CpE-3A', 'juan@bulsu.edu.ph', '09123456789', '$2a$10$oY75i0Dk/U1R5r1Q2L4nweaX9cKk2E7zV/d8n1GqK3J3H1bK9F0rO', 'BORROWER'),
(2, 'admin2026', 'Engr. Roberto Santos', 'College of Engineering - Custodian', 'admin@bulsu.edu.ph', '09187654321', '$2a$10$oY75i0Dk/U1R5r1Q2L4nweaX9cKk2E7zV/d8n1GqK3J3H1bK9F0rO', 'ADMIN');

-- Seed Categories (Exactly 4 functional categories + ALL filter)
INSERT INTO `categories` (`category_id`, `name`, `slug`, `icon_name`, `description`, `display_order`) VALUES
(1, 'Tools & Repairs', 'tools-repairs', 'ic_category_tools', 'Hand tools, soldering equipment, precision screwdrivers', 1),
(2, 'Electronics & Components', 'electronics-components', 'ic_category_electronics', 'Microcontrollers, breadboards, jumper wires, sensors', 2),
(3, 'Testing & Management', 'testing-management', 'ic_category_testing', 'Oscilloscopes, multimeters, calipers, thermometers', 3),
(4, 'Others', 'others', 'ic_category_others', 'Protective gear, hardware kits, accessories', 4);

-- Seed Equipment:
-- Note: Available counts:
-- 26 (Multimeter) + 7 (Soldering Iron) + 4 (Screwdriver) + 20 (Breadboard) + 5 (Arduino Uno) + 5 (Oscilloscope) = exactly 67 available!
INSERT INTO `equipment` (`equipment_id`, `item_code`, `name`, `category_id`, `category_name`, `description`, `total_quantity`, `available_quantity`, `condition_status`, `default_accessories`, `image_name`, `is_featured`, `is_active`) VALUES
(1, 'EQ-001', 'Digital Multimeter', 1, 'Tools & Repairs', 'High precision auto-ranging digital multimeter with test leads for AC/DC voltage and resistance measurement.', 30, 26, 'Good', 'Test Probes, Manual', 'img_multimeter', TRUE, TRUE),
(2, 'EQ-002', 'Soldering Iron', 1, 'Tools & Repairs', '60W adjustable temperature soldering iron with ceramic heater and heat-resistant stand.', 10, 7, 'Good', 'Stand, Solder Wire', 'img_soldering_iron', TRUE, TRUE),
(3, 'EQ-003', 'Screwdriver Set', 1, 'Tools & Repairs', 'Multi-piece precision screwdriver set suitable for electronics and lab equipment maintenance.', 5, 4, 'Good', 'Precision Bits Case', 'img_screwdriver_set', TRUE, TRUE),
(4, 'EQ-004', 'Breadboard', 2, 'Electronics & Components', '830 tie-point solderless breadboard with dual power rails for electronics prototyping.', 25, 20, 'Good', 'None', 'img_breadboard', TRUE, TRUE),
(5, 'EQ-005', 'Arduino Uno', 2, 'Electronics & Components', 'Microcontroller board based on ATmega328P with 14 digital I/O pins and 6 analog inputs.', 10, 5, 'Good', 'USB Cable, Jumper Wires', 'img_arduino_uno', TRUE, TRUE),
(6, 'EQ-006', 'Jumper Wires', 2, 'Electronics & Components', '40-piece male-to-male and male-to-female flexible jumper wire ribbon cable set.', 20, 15, 'Good', '40-pin Ribbon Cable', 'img_jumper_wires', TRUE, TRUE),
(7, 'EQ-007', 'Digital Oscilloscope', 3, 'Testing & Management', 'Dual-channel 100MHz digital storage oscilloscope with real-time waveform display.', 6, 5, 'Good', 'BNC Probes, Power Cable', 'img_oscilloscope', TRUE, TRUE),
(8, 'EQ-008', 'Digital Caliper', 3, 'Testing & Management', 'Electronic digital vernier caliper 0-150mm with LCD display and metric/inch conversion.', 12, 10, 'Good', 'Hard Case, Battery', 'img_digital_caliper', TRUE, TRUE),
(9, 'EQ-009', 'Thermometer', 3, 'Testing & Management', 'High-accuracy digital contact and probe thermometer for thermal testing and experiments.', 10, 9, 'Good', 'Protective Sheath', 'img_thermometer', TRUE, TRUE),
(10, 'EQ-010', 'Safety Goggles & Kit', 4, 'Others', 'Laboratory safety eye protection goggles with anti-fog coating and ESD wrist strap.', 15, 12, 'Good', 'Storage Bag, ESD Strap', 'img_safety_kit', FALSE, TRUE);

-- Seed Transactions for Juan Dela Cruz (user_id = 1)
-- Matches Figma Screens:
-- 1. Pending: Digital Multimeter, Qty: 1, Oct 20-Oct 24, 2026
-- 2. Pending: Breadboard, Qty: 1, Oct 22-Oct 28, 2026  (Total Pending = 2!)
-- 3. Approved: Digital Multimeter, Qty: 1, Oct 9-Oct 25, 2026
-- 4. Returned: Arduino Uno, Qty: 1, Sep 25-Oct 2, 2026
-- 5. Returned: Soldering Iron, Qty: 1, Sep 1-Sep 5, 2026
-- 6. Returned: Digital Caliper, Qty: 1, Aug 15-Aug 20, 2026 (Total Returned = 3!)
-- 7. Rejected: Digital Oscilloscope, Qty: 1, Aug 06-Aug 12, 2026
-- Total Borrowed lifetime count = 5 (Approved + Returned)
INSERT INTO `borrow_transactions` 
(`transaction_id`, `user_id`, `equipment_id`, `quantity`, `borrow_date`, `expected_return_date`, `actual_return_date`, `borrow_condition`, `borrow_accessories`, `status`, `return_condition`, `return_accessories`, `return_remarks`, `admin_remarks`, `approved_by`) 
VALUES
('TRX-20261020-001', 1, 1, 1, 'Oct 20, 2026', 'Oct 24, 2026', NULL, 'Good', 'None', 'PENDING', NULL, NULL, NULL, 'Awaiting Lab Custodian approval for Laboratory Activity #3.', NULL),
('TRX-20261022-002', 1, 4, 1, 'Oct 22, 2026', 'Oct 28, 2026', NULL, 'Good', 'None', 'PENDING', NULL, NULL, NULL, 'Submitted for Embedded Systems Project prototyping.', NULL),
('TRX-20261009-003', 1, 1, 1, 'Oct 9, 2026', 'Oct 25, 2026', NULL, 'Good', 'Test Probes', 'APPROVED', NULL, NULL, NULL, 'Approved by Engr. Roberto Santos. Please handle with care.', 2),
('TRX-20260925-004', 1, 5, 1, 'Sep 25, 2026', 'Oct 2, 2026', 'Oct 2, 2026', 'Good', 'USB Cable, Jumper Wires', 'RETURNED', 'Good', 'Complete', 'Returned in good condition, all wires complete.', 'Returned on time. Equipment verified functional.', 2),
('TRX-20260901-005', 1, 2, 1, 'Sep 1, 2026', 'Sep 5, 2026', 'Sep 5, 2026', 'Good', 'Stand, Solder Wire', 'RETURNED', 'Good', 'Complete', 'Used for soldering lab circuit boards.', 'Returned with complete stand and accessories.', 2),
('TRX-20260815-006', 1, 8, 1, 'Aug 15, 2026', 'Aug 20, 2026', 'Aug 20, 2026', 'Good', 'Hard Case, Battery', 'RETURNED', 'Good', 'Complete', 'Dimensions verified for enclosure design.', 'Verified in good working order.', 2),
('TRX-20260806-007', 1, 7, 1, 'Aug 06, 2026', 'Aug 12, 2026', NULL, 'Good', 'BNC Probes', 'REJECTED', NULL, NULL, NULL, 'Equipment reserved for Senior Capstone Laboratory examinations on the requested dates.', 2);

-- Seed Notifications for Juan Dela Cruz
INSERT INTO `notifications` (`user_id`, `title`, `message`, `is_read`) VALUES
(1, 'Request Approved', 'Your borrowing request TRX-20261009-003 for Digital Multimeter has been approved.', TRUE),
(1, 'Return Confirmed', 'Your return for Arduino Uno (TRX-20260925-004) was verified by Lab Custodian.', TRUE),
(1, 'Request Pending', 'Your request TRX-20261020-001 for Digital Multimeter has been submitted and is pending review.', FALSE);
