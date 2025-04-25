USE equipmentma2;

CREATE TABLE IF NOT EXISTS `Equipment` (
                                           `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                           `code` varchar(20) NOT NULL UNIQUE,
                                           `name` nvarchar(255) NOT NULL,
                                           `status` int NOT NULL,
                                           `category_id` int NOT NULL,
                                           `image_id` int,
                                           `regular_maintenance_day` int NOT NULL CHECK (regular_maintenance_day > 0),
                                           `last_maintenance_time` timestamp NOT NULL default current_timestamp,
                                           `description` text,
                                           `created_at` timestamp NOT NULL default current_timestamp,
                                           `is_active` boolean NOT NULL DEFAULT '1',
                                           PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Category` (
                                          `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                          `name` nvarchar(50) NOT NULL UNIQUE,
                                          `is_active` boolean NOT NULL DEFAULT '1',
                                          `created_at` timestamp NOT NULL default current_timestamp,
                                          PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `User` (
                                      `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                      `first_name` nvarchar(50) NOT NULL,
                                      `last_name` nvarchar(50) NOT NULL,
                                      `username` varchar(50) NOT NULL UNIQUE,
                                      `password` text NOT NULL,
                                      `email` varchar(50) NOT NULL UNIQUE,
                                      `phone` varchar(20) NOT NULL UNIQUE,
                                      `role` int NOT NULL,
                                      `avatar_id` int,
                                      `is_active` boolean NOT NULL DEFAULT '1',
                                      `created_at` timestamp NOT NULL default current_timestamp,
                                      PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Maintenance` (
                                             `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                             `title` nvarchar(255) NOT NULL,
                                             `description` text NOT NULL,
                                             `start_datetime` timestamp NOT NULL,
                                             `end_datetime` timestamp NOT NULL,
                                             `is_active` boolean NOT NULL DEFAULT '1',
                                             `created_at` timestamp NOT NULL default current_timestamp,
                                             PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Equipment_Maintenance` (
                                                       `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                                       `equipment_id` int NOT NULL,
                                                       `maintenance_id` int NOT NULL,
                                                       `technician_id` int NOT NULL,
                                                       `description` text,
                                                       `result` int,
                                                       `repair_price` int CHECK (repair_price >= 0),
                                                       `inspection_date` timestamp,
                                                       `is_active` boolean NOT NULL DEFAULT '1',
                                                       `created_at` timestamp NOT NULL default current_timestamp,
                                                       PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Maintenance_Repair_Suggestion` (
                                                               `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                                               `name` varchar(255) NOT NULL,
                                                               `description` text NOT NULL,
                                                               `suggest_price` float NOT NULL CHECK (suggest_price > 0),
                                                               `is_active` boolean NOT NULL DEFAULT '1',
                                                               `created_at` timestamp NOT NULL default current_timestamp,
                                                               PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Image` (
                                       `id` int AUTO_INCREMENT NOT NULL UNIQUE,
                                       `filename` varchar(100) NOT NULL UNIQUE,
                                       `path` text NOT NULL,
                                       `is_active` boolean NOT NULL DEFAULT '1',
                                       `created_at` timestamp NOT NULL default current_timestamp,
                                       PRIMARY KEY (`id`)
);

ALTER TABLE `Equipment` ADD CONSTRAINT `Equipment_fk3` FOREIGN KEY (`image_id`) REFERENCES `Image`(`id`);

ALTER TABLE `Equipment` ADD CONSTRAINT `Equipment_fk4` FOREIGN KEY (`category_id`) REFERENCES `Category`(`id`)  ON DELETE CASCADE;

ALTER TABLE `User` ADD CONSTRAINT `User_fk10` FOREIGN KEY (`avatar_id`) REFERENCES `Image`(`id`);

ALTER TABLE `Equipment_Maintenance` ADD CONSTRAINT `Equipment_Maintenance_fk1` FOREIGN KEY (`equipment_id`) REFERENCES `Equipment`(`id`);

ALTER TABLE `Equipment_Maintenance` ADD CONSTRAINT `Equipment_Maintenance_fk5` FOREIGN KEY (`maintenance_id`) REFERENCES `Maintenance`(`id`)  ON DELETE CASCADE;

ALTER TABLE `Equipment_Maintenance` ADD CONSTRAINT `Equipment_Maintenance_fk3` FOREIGN KEY (`technician_id`) REFERENCES `User`(`id`);


-- Tạo chỉ mục
CREATE INDEX idx_equipment_code ON Equipment (code);
CREATE INDEX idx_equipment_status ON Equipment (status);
CREATE INDEX idx_equipment_category ON Equipment (category_id);
CREATE INDEX idx_user_username ON User (username);
CREATE INDEX idx_user_email ON User (email);
CREATE INDEX idx_user_role ON User (role);