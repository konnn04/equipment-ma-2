USE equipmentma2;

INSERT INTO Image (filename, path)
VALUES
('vector-sign-of-user-icon_zspnuk.jpg','https://res.cloudinary.com/dg66aou8q/image/upload/v1743568524/vector-sign-of-user-icon_zspnuk.jpg'),
('laptop.png', 'https://png.pngtree.com/png-vector/20191026/ourmid/pngtree-laptop-icon-png-image_1871608.jpg'),
('maykhoan.png','https://cuahangbosch.com/wp-content/uploads/2017/10/may-khoan-bua-bosch-gbh-2-24-RE.jpg'),
('xetai.png','https://xetai-isuzu.vn/wp-content/uploads/2019/06/isuzu-15-t%E1%BA%A5n-gg.jpg');

INSERT INTO Category (name)
VALUES ('Electronics'),
       ('Machinery'),
       ('Vehicles'),
       ('Furniture'),
       ('Tools');
# -- ADMIN
INSERT INTO User (first_name, last_name, username, password, email, phone, role, avatar_id)
VALUES ('Nguyen', 'Van A', 'adminfake', '1', 'admin1@example.com', '1234567890', 1, 1),
('Phi', 'Minh Quang', 'quang', '1', '2251012121quang@ou.edu.vn', '0987654321', 2, 1),  -- TECHNICIAN
('Hoang', 'Anh Duy', 'duy', '1', '2251012046duy@ou.edu.vn', '0122334455', 2, 2),    -- MANAGER
('Nguyen', 'Thanh Trieu', 'trieu', '1', '2251052127trieu@ou.edu.vn', '0233445566', 2, 1),  -- TECHNICIAN
('Hoang', 'Van E', 'manager2', '1', 'manager2@example.com', '3344556677', 3, 1), -- MANAGER
('Bui', 'Thi F', 'admin2', '1', 'admin2@example.com', '4455667788', 1, 1),       -- ADMIN
('Dang', 'Van G', 'technician3', '1', 'tech3@example.com', '5566778899', 2, 1),  -- TECHNICIAN
('Vu', 'Thi H', 'manager3', '1', 'manager3@example.com', '6677889900', 3, 1),    -- MANAGER
('Ngo', 'Van I', 'technician4', '1', 'tech4@example.com', '7788990011', 2, 1),   -- TECHNICIAN
('Do', 'Thi K', 'admin3', '1', 'admin3@example.com', '8899001122', 1, 1); -- ADMIN

INSERT INTO Equipment (code, name, status, category_id, regular_maintenance_day, last_maintenance_time, description,
                       image_id)
VALUES ('ELEC001', 'Laptop', 2, 1, 180, CURRENT_TIMESTAMP, 'Máy tính xách tay văn phòng', 2),   -- ACTIVE
       ('MACH001', 'Máy khoan', 3, 2, 90, CURRENT_TIMESTAMP, 'Máy khoan công nghiệp', 3),       -- UNDER_MAINTENANCE
       ('VEH001', 'Xe tải', 4, 3, 365, CURRENT_TIMESTAMP, 'Xe tải giao hàng', 4),               -- BROKEN
       ('FURN001', 'Ghế văn phòng', 2, 4, 30, CURRENT_TIMESTAMP, 'Ghế công thái học', 1),       -- ACTIVE
       ('TOOL001', 'Bộ tua vít', 1, 5, 30, CURRENT_TIMESTAMP, 'Bộ dụng cụ cơ bản', 1),          -- INACTIVE
       ('ELEC002', 'Máy chiếu', 2, 1, 180, CURRENT_TIMESTAMP, 'Máy chiếu phòng họp', 1),        -- ACTIVE
       ('MACH002', 'Máy tiện', 2, 2, 90, CURRENT_TIMESTAMP, 'Máy tiện gia công kim loại', 1),   -- ACTIVE
       ('VEH002', 'Xe nâng', 3, 3, 180, CURRENT_TIMESTAMP, 'Xe nâng kho hàng', 1),              -- UNDER_MAINTENANCE
       ('FURN002', 'Bàn làm việc', 2, 4, 30, CURRENT_TIMESTAMP, 'Bàn văn phòng', 1),            -- ACTIVE
       ('TOOL002', 'Bộ cờ lê', 2, 5, 30, CURRENT_TIMESTAMP, 'Cờ lê điều chỉnh', 1),             -- ACTIVE
       ('ELEC003', 'Máy in', 2, 1, 90, CURRENT_TIMESTAMP, 'Máy in văn phòng', 1),               -- ACTIVE
       ('MACH003', 'Máy CNC', 3, 2, 180, CURRENT_TIMESTAMP, 'Máy gia công chính xác', 1),       -- UNDER_MAINTENANCE
       ('VEH003', 'Xe máy', 2, 3, 180, CURRENT_TIMESTAMP, 'Xe máy giao hàng', 1),               -- ACTIVE
       ('FURN003', 'Bàn họp', 2, 4, 330, CURRENT_TIMESTAMP, 'Bàn họp lớn', 1),                  -- ACTIVE
       ('TOOL003', 'Máy khoan cầm tay', 2, 5, 90, CURRENT_TIMESTAMP, 'Máy khoan không dây', 1), -- ACTIVE
       ('ELEC004', 'Màn hình', 1, 1, 180, CURRENT_TIMESTAMP, 'Màn hình máy tính', 1),           -- BROKEN
       ('MACH004', 'Máy hàn', 2, 2, 90, CURRENT_TIMESTAMP, 'Máy hàn công nghiệp', 1),           -- ACTIVE
       ('VEH004', 'Xe ô tô', 1, 3, 365, CURRENT_TIMESTAMP, 'Xe công ty cũ', 1),                 -- ACTIVE
       ('FURN004', 'Tủ hồ sơ', 2, 4, 330, CURRENT_TIMESTAMP, 'Tủ đựng tài liệu', 1),            -- ACTIVE
       ('TOOL004', 'Búa', 2, 5, 330, CURRENT_TIMESTAMP, 'Búa tay', 1), -- ACTIVE
       ('ELEC005', 'Laptop ASUS', 5, 1, 330, CURRENT_TIMESTAMP, 'Laptop ASUS', 1), -- LIQUID
        ('ELEC006', 'Laptop MSI', 5, 1, 330, CURRENT_TIMESTAMP, 'Laptop MSI', 1), -- LIQUID
        ('ELEC007', 'Laptop ACER', 5, 1, 330, CURRENT_TIMESTAMP, 'Laptop ACER', 1); -- LIQUID

INSERT INTO Maintenance (title, description, start_datetime, end_datetime)
VALUES ('Kiểm tra điện tử hàng quý', 'Kiểm tra tất cả thiết bị điện tử', '2023-10-01 09:00:00', '2023-10-01 17:00:00'),
       ('Bảo trì máy móc lớn', 'Bảo trì toàn diện cho máy móc', '2023-11-15 08:00:00', '2023-11-20 18:00:00'),
       ('Kiểm tra xe hàng năm', 'Kiểm tra an toàn xe', '2023-12-01 10:00:00', '2023-12-01 16:00:00'),
       ('Kiểm tra dụng cụ hàng tháng', 'Kiểm tra tất cả dụng cụ', '2023-10-15 09:00:00', '2023-10-15 12:00:00'),
       ('Đánh giá nội thất', 'Kiểm tra tình trạng nội thất', '2023-11-01 10:00:00', '2023-11-01 15:00:00'),
       ('Bảo trì khẩn cấp', 'Xử lý sự cố đột xuất', '2023-12-10 08:00:00', '2023-12-10 14:00:00');

INSERT INTO Equipment_Maintenance (equipment_id, maintenance_id, technician_id, description, result,
                                   repair_price, inspection_date, equipment_name, equipment_code)
VALUES (1, 1, 2, 'Kiểm tra laptop, hoạt động tốt', 1, 0, '2023-10-01 10:00:00', 'Laptop', 'ELEC001'),          -- NORMALLY
       (2, 2, 4, 'Máy khoan cần thay động cơ', 2, 1500000, '2023-11-16 14:00:00', 'Máy khoan','MACH001'),  -- NEEDS_REPAIR
       (3, 3, 2, 'Xe tải hỏng động cơ, không sửa được', 3, 0, '2023-12-01 11:00:00', 'Xe tải', 'VEH001'),  -- BROKEN
       (4, 1, 4, 'Ghế vẫn tốt', 1, 0, '2023-10-01 11:00:00', 'Ghế văn phòng', 'FURN001'),                -- NORMALLY
       (5, 2, 2, 'Bộ tua vít chưa sử dụng', 1, 0, '2023-11-17 09:00:00', 'Bộ tua vít', 'TOOL001'),        -- NORMALLY
       (6, 1, 4, 'Máy chiếu cần thay bóng đèn', 2, 500000, '2023-10-01 12:00:00', 'Máy chiếu', 'ELEC002'), -- NEEDS_REPAIR
       (7, 2, 2, 'Máy tiện hoạt động tốt', 1, 0, '2023-11-18 10:00:00', 'Máy tiện', 'MACH002'),           -- NORMALLY
       (8, 3, 4, 'Xe nâng cần thay lốp', 2, 2000000, '2023-12-01 13:00:00', 'Xe nâng', 'VEH002'),         -- NEEDS_REPAIR
       (9, 1, 2, 'Bàn làm việc chắc chắn', 1, 0, '2023-10-01 13:00:00', 'Bàn làm việc', 'FURN002'),       -- NORMALLY
       (10, 2, 4, 'Bộ cờ lê đầy đủ', 1, 0, '2023-11-19 11:00:00', 'Bộ cờ lê', 'TOOL002'),                -- NORMALLY
       (11, 4, 2, 'Máy in sắp hết mực', 2, 200000, '2023-10-15 10:00:00', 'Máy in', 'ELEC003'),          -- NEEDS_REPAIR
       (12, 5, 4, 'Máy CNC cần hiệu chỉnh', 2, 1000000, '2023-11-01 11:00:00', 'Máy CNC', 'MACH003'),    -- NEEDS_REPAIR
       (13, 3, 2, 'Xe máy hoạt động bình thường', 1, 0, '2023-12-01 14:00:00', 'Xe máy', 'VEH003'),      -- NORMALLY
       (14, 5, 4, 'Bàn họp bị xước', 2, 500000, '2023-11-01 12:00:00', 'Bàn họp', 'FURN003'),            -- NEEDS_REPAIR
       (15, 4, 2, 'Máy khoan cầm tay pin yếu', 2, 300000, '2023-10-15 11:00:00', 'Máy khoan cầm tay', 'TOOL003'), -- NEEDS_REPAIR
       (16, 6, 7, 'Màn hình không lên nguồn', 3, 0, '2023-12-10 09:00:00', 'Màn hình', 'ELEC004'),        -- BROKEN
       (17, 2, 9, 'Máy hàn hoạt động tốt', 1, 0, '2023-11-18 12:00:00', 'Máy hàn', 'MACH004'),           -- NORMALLY
       (18, 3, 7, 'Xe ô tô đã thanh lý', 4, 0, '2023-12-01 15:00:00', 'Xe ô tô', 'VEH004'),              -- NEEDS_DISPOSAL
       (19, 5, 9, 'Tủ hồ sơ vẫn tốt', 1, 0, '2023-11-01 13:00:00', 'Tủ hồ sơ', 'FURN004'),              -- NORMALLY
       (20, 4, 7, 'Búa không bị hư hại', 1, 0, '2023-10-15 10:30:00', 'Búa', 'TOOL004'); -- NORMALLY

INSERT INTO Maintenance_Repair_Suggestion (name, description, suggest_price)
VALUES ('Thay động cơ', 'Thay động cơ cho máy khoan', 1500000),
       ('Thay bóng đèn', 'Thay bóng đèn máy chiếu', 500000),
       ('Thay lốp', 'Thay lốp cho xe nâng', 2000000),
       ('Thay mực', 'Thay mực in cho máy in', 200000),
       ('Hiệu chỉnh', 'Hiệu chỉnh máy CNC', 1000000),
       ('Sơn lại bề mặt', 'Sơn lại bề mặt bàn họp', 500000),
       ('Thay pin', 'Thay pin cho máy khoan cầm tay', 300000),
       ('Thay màn hình', 'Thay màn hình máy tính', 1000000),
       ('Bọc lại ghế', 'Bọc lại ghế văn phòng', 300000),
       ('Cập nhật phần mềm', 'Cập nhật phần mềm cho laptop', 100000);