CREATE TABLE IF NOT EXISTS `Equipment`
(
    `id`                      int AUTO_INCREMENT NOT NULL UNIQUE,
    `code`                    varchar(20)        NOT NULL UNIQUE,
    `name`                    nvarchar(255)      NOT NULL,
    `status`                  int                NOT NULL,
    `category_id`             int                NOT NULL,
    `image_id`                int,
    `regular_maintenance_day` int                NOT NULL CHECK (regular_maintenance_day > 0),
    `last_maintenance_time`   timestamp          NOT NULL default current_timestamp,
    `description`             text,
    `created_at`              timestamp          NOT NULL default current_timestamp,
    `is_active`               boolean            NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `Category`
(
    `id`         int AUTO_INCREMENT NOT NULL UNIQUE,
    `name`       nvarchar(50)       NOT NULL UNIQUE,
    `is_active`  boolean            NOT NULL DEFAULT '1',
    `created_at` timestamp          NOT NULL default current_timestamp,
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `User`
(
    `id`         int AUTO_INCREMENT NOT NULL UNIQUE,
    `first_name` nvarchar(50)       NOT NULL,
    `last_name`  nvarchar(50)       NOT NULL,
    `username`   varchar(50)        NOT NULL UNIQUE,
    `password`   text               NOT NULL,
    `email`      varchar(50)        NOT NULL UNIQUE,
    `phone`      varchar(20)        NOT NULL UNIQUE,
    `role`       int                NOT NULL,
    `avatar_id`  int,
    `is_active`  boolean            NOT NULL DEFAULT '1',
    `created_at` timestamp          NOT NULL default current_timestamp,
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `Maintenance`
(
    `id`             int AUTO_INCREMENT NOT NULL UNIQUE,
    `title`          nvarchar(255)      NOT NULL,
    `description`    text               NOT NULL,
    `start_datetime` timestamp          NOT NULL,
    `end_datetime`   timestamp          NOT NULL,
    `is_active`      boolean            NOT NULL DEFAULT '1',
    `created_at`     timestamp          NOT NULL default current_timestamp,
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `Equipment_Maintenance`
(
    `id`              int AUTO_INCREMENT NOT NULL UNIQUE,
    `equipment_id`    int                NOT NULL,
    `maintenance_id`  int                NOT NULL,
    `technician_id`   int                NOT NULL,
    `description`     text               NOT NULL,
    `result`          int,
    `repair_name`     nvarchar(255),
    `repair_price`    int CHECK (repair_price >= 0),
    `inspection_date` timestamp,
    `is_active`       boolean            NOT NULL DEFAULT '1',
    `created_at`      timestamp          NOT NULL default current_timestamp,
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `Maintenance_Repair_Suggestion`
(
    `id`            int AUTO_INCREMENT NOT NULL UNIQUE,
    `name`          varchar(255)       NOT NULL,
    `description`   text               NOT NULL,
    `suggest_price` float              NOT NULL CHECK (suggest_price > 0),
    `is_active`     boolean            NOT NULL DEFAULT '1',
    `created_at`    timestamp          NOT NULL default current_timestamp,
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `Image`
(
    `id`         int AUTO_INCREMENT NOT NULL UNIQUE,
    `filename`   varchar(100)       NOT NULL UNIQUE,
    `path`       text               NOT NULL,
    `is_active`  boolean            NOT NULL DEFAULT '1',
    `created_at` timestamp          NOT NULL default current_timestamp,
    PRIMARY KEY (`id`)
    );

ALTER TABLE `Equipment`
    ADD CONSTRAINT `Equipment_fk3` FOREIGN KEY (`image_id`) REFERENCES `Image` (`id`);

ALTER TABLE `Equipment`
    ADD CONSTRAINT `Equipment_fk4` FOREIGN KEY (`category_id`) REFERENCES `Category` (`id`) ON DELETE CASCADE;

ALTER TABLE `User`
    ADD CONSTRAINT `User_fk10` FOREIGN KEY (`avatar_id`) REFERENCES `Image` (`id`);

ALTER TABLE `Equipment_Maintenance`
    ADD CONSTRAINT `Equipment_Maintenance_fk1` FOREIGN KEY (`equipment_id`) REFERENCES `Equipment` (`id`);

ALTER TABLE `Equipment_Maintenance`
    ADD CONSTRAINT `Equipment_Maintenance_fk5` FOREIGN KEY (`maintenance_id`) REFERENCES `Maintenance` (`id`);

ALTER TABLE `Equipment_Maintenance`
    ADD CONSTRAINT `Equipment_Maintenance_fk3` FOREIGN KEY (`technician_id`) REFERENCES `User` (`id`);

-- RUN THIS AFTER THE TABLES ARE CREATED
INSERT INTO `Image` (filename, path)
VALUES ('vector-sign-of-user-icon_zspnuk.jpg',
        'https://res.cloudinary.com/dg66aou8q/image/upload/v1743568524/vector-sign-of-user-icon_zspnuk.jpg');

INSERT INTO `Category` (name) VALUES
('Electronics'),
('Machinery'),
('Vehicles'),
('Furniture'),
('Tools');

INSERT INTO `User` (first_name, last_name, username, password, email, phone, role, avatar_id) VALUES
('Nguyen', 'Van A', 'admin', '1', 'admin1@example.com', '1234567890', 1, 1),
('Tran', 'Thi B', 'technician1', '1', 'tech1@example.com', '0987654321', 2, 1),
('Le', 'Van C', 'manager1', '1', 'manager1@example.com', '1122334455', 3, 1),
('Pham', 'Thi D', 'technician2', '1', 'tech2@example.com', '2233445566', 2, 1),
('Hoang', 'Van E', 'manager2', '1', 'manager2@example.com', '3344556677', 3, 1),
('Bui', 'Thi F', 'admin2', '1', 'admin2@example.com', '4455667788', 1, 1),
('Dang', 'Van G', 'technician3', '1', 'tech3@example.com', '5566778899', 2, 1),
('Vu', 'Thi H', 'manager3', '1', 'manager3@example.com', '6677889900', 3, 1),
('Ngo', 'Van I', 'technician4', '1', 'tech4@example.com', '7788990011', 2, 1),
('Do', 'Thi K', 'admin3', '1', 'admin3@example.com', '8899001122', 1, 1);

INSERT INTO `Equipment` (code, name, status, category_id, regular_maintenance_day, last_maintenance_time, description, image_id) VALUES
('ELEC001', 'Laptop', 2, 1, 180, '2023-11-15 10:00:00', 'Máy tính xách tay văn phòng', 1),
('MACH001', 'Máy khoan', 3, 2, 90, '2023-11-15 10:00:00', 'Máy khoan công nghiệp', 1),
('VEH001', 'Xe tải', 4, 3, 365, '2023-11-15 10:00:00', 'Xe tải giao hàng', 1),
('FURN001', 'Ghế văn phòng', 2, 4, 30, '2023-11-15 10:00:00', 'Ghế công thái học', 1),
('TOOL001', 'Bộ tua vít', 1, 5, 30, '2023-11-15 10:00:00', 'Bộ dụng cụ cơ bản', 1),
('ELEC002', 'Máy chiếu', 2, 1, 180, '2023-11-15 10:00:00', 'Máy chiếu phòng họp', 1),
('MACH002', 'Máy tiện', 2, 2, 90, '2023-11-15 10:00:00', 'Máy tiện gia công kim loại', 1),
('VEH002', 'Xe nâng', 3, 3, 180, '2023-11-15 10:00:00', 'Xe nâng kho hàng', 1),
('FURN002', 'Bàn làm việc', 2, 4, 30, '2023-11-15 10:00:00', 'Bàn văn phòng', 1),
('TOOL002', 'Bộ cờ lê', 2, 5, 30, '2023-11-15 10:00:00', 'Cờ lê điều chỉnh', 1),
('ELEC003', 'Máy in', 2, 1, 90, '2023-11-15 10:00:00', 'Máy in văn phòng', 1),
('MACH003', 'Máy CNC', 3, 2, 180, '2023-11-15 10:00:00', 'Máy gia công chính xác', 1),
('VEH003', 'Xe máy', 2, 3, 180, '2023-11-15 10:00:00', 'Xe máy giao hàng', 1),
('FURN003', 'Bàn họp', 2, 4, 330, '2023-11-15 10:00:00', 'Bàn họp lớn', 1),
('TOOL003', 'Máy khoan cầm tay', 2, 5, 90, '2023-11-15 10:00:00', 'Máy khoan không dây', 1),
('ELEC004', 'Màn hình', 4, 1, 180, '2023-11-15 10:00:00', 'Màn hình máy tính', 1),
('MACH004', 'Máy hàn', 2, 2, 90, '2023-11-15 10:00:00', 'Máy hàn công nghiệp', 1),
('VEH004', 'Xe ô tô', 5, 3, 365, '2023-11-15 10:00:00', 'Xe công ty cũ', 1),
('FURN004', 'Tủ hồ sơ', 2, 4, 330, '2023-11-15 10:00:00', 'Tủ đựng tài liệu', 1),
('TOOL004', 'Búa', 2, 5, 330, '2023-11-15 10:00:00', 'Búa tay', 1),
('ELEP001', 'dád', 2, 1, 180, '2023-11-15 10:00:00', 'Máy tính xách tay văn phòng', 1),
('ELEC005', 'Laptop ASUS', 5, 1, 330, '2023-11-15 10:00:00', 'Laptop ASUS', 1), -- LIQUID
('ELEC006', 'Laptop MSI', 5, 1, 330, '2023-11-15 10:00:00', 'Laptop MSI', 1), -- LIQUID
('ELEC007', 'Laptop ACER', 5, 1, 330, '2023-11-15 10:00:00', 'Laptop ACER', 1); -- LIQUID

INSERT INTO `Maintenance` (title, description, start_datetime, end_datetime) VALUES
('Kiểm tra điện tử hàng quý', 'Kiểm tra tất cả thiết bị điện tử', '2023-10-01 09:00:00', '2023-10-01 17:00:00'),
('Bảo trì máy móc lớn', 'Bảo trì toàn diện cho máy móc', '2023-11-15 08:00:00', '2023-11-20 18:00:00'),
('Kiểm tra xe hàng năm', 'Kiểm tra an toàn xe', '2023-12-01 10:00:00', '2023-12-01 16:00:00'),
('Kiểm tra dụng cụ hàng tháng', 'Kiểm tra tất cả dụng cụ', '2023-10-15 09:00:00', '2023-10-15 12:00:00'),
('Đánh giá nội thất', 'Kiểm tra tình trạng nội thất', '2023-11-01 10:00:00', '2023-11-01 15:00:00'),
('Bảo trì khẩn cấp', 'Xử lý sự cố đột xuất', '2023-12-10 08:00:00', '2023-12-10 14:00:00');

INSERT INTO `Equipment_Maintenance` (equipment_id, maintenance_id, technician_id, description, result, repair_name, repair_price, inspection_date) VALUES
(1, 1, 2, 'Kiểm tra laptop, hoạt động tốt', 1, 'None', 0, '2023-10-01 10:00:00'),
(2, 2, 4, 'Máy khoan cần thay động cơ', 2, 'Thay động cơ', 1500000, '2023-11-16 14:00:00'),
(3, 3, 2, 'Xe tải hỏng động cơ, không sửa được', 3, 'None', 0, '2023-12-01 11:00:00'),
(4, 1, 4, 'Ghế vẫn tốt', 1, 'None', 0, '2023-10-01 11:00:00'),
(5, 2, 2, 'Bộ tua vít chưa sử dụng', 1, 'None', 0, '2023-11-17 09:00:00'),
(6, 1, 4, 'Máy chiếu cần thay bóng đèn', 2, 'Thay bóng đèn', 500000, '2023-10-01 12:00:00'),
(7, 2, 2, 'Máy tiện hoạt động tốt', 1, 'None', 0, '2023-11-18 10:00:00'),
(8, 3, 4, 'Xe nâng cần thay lốp', 2, 'Thay lốp', 2000000, '2023-12-01 13:00:00'),
(9, 1, 2, 'Bàn làm việc chắc chắn', 1, 'None', 0, '2023-10-01 13:00:00'),
(10, 2, 4, 'Bộ cờ lê đầy đủ', 1, 'None', 0, '2023-11-19 11:00:00'),
(11, 4, 2, 'Máy in sắp hết mực', 2, 'Thay mực', 200000, '2023-10-15 10:00:00'),
(12, 5, 4, 'Máy CNC cần hiệu chỉnh', 2, 'Hiệu chỉnh', 1000000, '2023-11-01 11:00:00'),
(13, 3, 2, 'Xe máy hoạt động bình thường', 1, 'None', 0, '2023-12-01 14:00:00'),
(14, 5, 4, 'Bàn họp bị xước', 2, 'Sơn lại bề mặt', 500000, '2023-11-01 12:00:00'),
(15, 4, 2, 'Máy khoan cầm tay pin yếu', 2, 'Thay pin', 300000, '2023-10-15 11:00:00'),
(16, 6, 7, 'Màn hình không lên nguồn', 3, 'None', 0, '2023-12-10 09:00:00'),
(17, 2, 9, 'Máy hàn hoạt động tốt', 1, 'None', 0, '2023-11-18 12:00:00'),
(18, 3, 7, 'Xe ô tô đã thanh lý', 4, 'None', 0, '2023-12-01 15:00:00'),
(19, 5, 9, 'Tủ hồ sơ vẫn tốt', 1, 'None', 0, '2023-11-01 13:00:00'),
(20, 4, 7, 'Búa không bị hư hại', 1, 'None', 0, '2023-10-15 10:30:00');

INSERT INTO `Maintenance_Repair_Suggestion` (name, description, suggest_price) VALUES
 ('Thay động cơ', 'Thay động cơ cho máy khoan', 1500000),
 ('Thay bóng đèn', 'Thay bóng đèn máy chiếu', 500000),
 ('Thay lốp', 'Thay lốp cho xe nâng', 2000000),
 ('Thay mực', 'Thay mực in cho máy in', 200000),
 ('Hiệu chỉnh', 'Hiệu chỉnh máy CNC', 1000000),
 ('Sơn lại bề mặt', 'Sơn lại bề mặt bàn họp', 500000),
 ('Thay pin', 'Thay pin cho máy khoan cầm tay', 300000),
 ('Thay màn hình', 'Thay màn hình máy tính', 1000000),
 ('Bọc lại ghế', 'Bọc lại ghế văn phòng', 300000),
 ('Cập nhật phần mềm', 'Cập nhật phần mềm cho laptop', 100000);
