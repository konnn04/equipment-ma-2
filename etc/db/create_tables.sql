USE equipmentma2;

CREATE TABLE IF NOT EXISTS `Equipment` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`code` varchar(20) NOT NULL,
	`name` nvarchar(255) NOT NULL,
	`status` int NOT NULL,
	`category` int NOT NULL,
	`import_date` datetime NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Category` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`name` nvarchar(50) NOT NULL UNIQUE,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Status` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`name` nvarchar(255) NOT NULL UNIQUE,
	`description` text NOT NULL,
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
	`is_active` boolean NOT NULL DEFAULT '1',
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Role` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`name` nvarchar(50) NOT NULL UNIQUE,
	`description` text NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Maintenance` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`title` nvarchar(255) NOT NULL DEFAULT '100',
	`description` text NOT NULL,
	`start_datetime` datetime NOT NULL,
	`end_datetime` datetime NOT NULL,
	`quantity` int NOT NULL DEFAULT '1',
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Equipment_Maintenance` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`equipment_id` int NOT NULL,
	`description` text NOT NULL,
	`maintenance_type_id` int NOT NULL,
	`price` float,
	`maintenance_id` int NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `User_Maintenance` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`user_id` int NOT NULL,
	`maintenance_id` int NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Maintenance_Type` (
	`id` int AUTO_INCREMENT NOT NULL UNIQUE,
	`name` varchar(255) NOT NULL,
	`description` text NOT NULL,
	`suggest_price` float NOT NULL,
	PRIMARY KEY (`id`)
);

ALTER TABLE `Equipment` ADD CONSTRAINT `Equipment_fk3` FOREIGN KEY (`status`) REFERENCES `Status`(`id`);

ALTER TABLE `Equipment` ADD CONSTRAINT `Equipment_fk4` FOREIGN KEY (`category`) REFERENCES `Category`(`id`);


ALTER TABLE `User` ADD CONSTRAINT `User_fk7` FOREIGN KEY (`role`) REFERENCES `Role`(`id`);


ALTER TABLE `Equipment_Maintenance` ADD CONSTRAINT `Equipment_Maintenance_fk1` FOREIGN KEY (`equipment_id`) REFERENCES `Equipment`(`id`);

ALTER TABLE `Equipment_Maintenance` ADD CONSTRAINT `Equipment_Maintenance_fk3` FOREIGN KEY (`maintenance_type_id`) REFERENCES `Maintenance_Type`(`id`);

ALTER TABLE `Equipment_Maintenance` ADD CONSTRAINT `Equipment_Maintenance_fk5` FOREIGN KEY (`maintenance_id`) REFERENCES `Maintenance`(`id`);
ALTER TABLE `User_Maintenance` ADD CONSTRAINT `User_Maintenance_fk1` FOREIGN KEY (`user_id`) REFERENCES `User`(`id`);

ALTER TABLE `User_Maintenance` ADD CONSTRAINT `User_Maintenance_fk2` FOREIGN KEY (`maintenance_id`) REFERENCES `Maintenance`(`id`);

