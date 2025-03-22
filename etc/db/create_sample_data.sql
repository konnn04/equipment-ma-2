USE equipmentma2;

INSERT INTO Category (name) 
VALUES ('Computers'), ('Printers'), ('Furniture'), ('Networking'), ('Peripherals');

INSERT INTO Status (name, description) 
VALUES 
('Đang hoạt động', 'Thiết bị đang hoạt động bình thường'),
('Hỏng hóc', 'Thiết bị bị hỏng và cần sửa chữa'),
('Đang sửa chữa', 'Thiết bị đang trong quá trình sửa chữa'),
('Đã thanh lý', 'Thiết bị đã được thanh lý và không còn sử dụng');

INSERT INTO Role (name, description) 
VALUES 
('Admin', 'Quản trị viên hệ thống'),
('Employee', 'Nhân viên thông thường');

INSERT INTO Maintenance_Type (name, description, suggest_price) 
VALUES 
('Vệ sinh', 'Làm sạch thiết bị', 50.0),
('Sửa chữa', 'Sửa chữa các hỏng hóc', 100.0),
('Bảo dưỡng định kỳ', 'Bảo dưỡng theo lịch trình', 80.0),
('Kiểm tra', 'Kiểm tra tình trạng thiết bị', 30.0);

INSERT INTO User (first_name, last_name, username, password, email, phone, role, is_active) 
VALUES 
('Admin', 'User', 'admin', '1', 'admin@example.com', '123456789', 1, 1),
('John', 'Doe', 'employee1', 'password1', 'john@example.com', '987654321', 2, 1),
('Jane', 'Smith', 'employee2', 'password2', 'jane@example.com', '555555555', 2, 1);

INSERT INTO Equipment (code, name, status, category, import_date) 
VALUES
('EQ001', 'Laptop Dell', 1, 1, '2022-01-01 00:00:00'),
('EQ002', 'Desktop HP', 1, 1, '2022-02-01 00:00:00'),
('EQ003', 'Printer Canon', 2, 2, '2022-03-01 00:00:00'),
('EQ004', 'Scanner Epson', 1, 2, '2022-04-01 00:00:00'),
('EQ005', 'Office Chair', 1, 3, '2022-05-01 00:00:00'),
('EQ006', 'Desk', 4, 3, '2022-06-01 00:00:00'),
('EQ007', 'Router Cisco', 1, 4, '2022-07-01 00:00:00'),
('EQ008', 'Switch TP-Link', 3, 4, '2022-08-01 00:00:00'),
('EQ009', 'Monitor Samsung', 1, 5, '2022-09-01 00:00:00'),
('EQ010', 'Keyboard Logitech', 2, 5, '2022-10-01 00:00:00');

INSERT INTO Maintenance (title, description, start_datetime, end_datetime, quantity) 
VALUES
('Bảo trì tháng 1', 'Bảo trì định kỳ tháng 1', '2023-01-10 08:00:00', '2023-01-11 17:00:00', 1),
('Bảo trì tháng 2', 'Bảo trì định kỳ tháng 2', '2023-02-15 08:00:00', '2023-02-16 17:00:00', 1),
('Bảo trì tháng 3', 'Bảo trì định kỳ tháng 3', '2023-03-20 08:00:00', '2023-03-21 17:00:00', 1);

INSERT INTO Equipment_Maintenance (equipment_id, description, maintenance_type_id, price, maintenance_id) 
VALUES
(1, 'Vệ sinh laptop', 1, 50.0, 1),
(3, 'Sửa chữa máy in', 2, 100.0, 1),
(5, 'Kiểm tra ghế văn phòng', 4, 30.0, 2),
(7, 'Bảo dưỡng router', 3, 80.0, 2),
(9, 'Vệ sinh màn hình', 1, 50.0, 3),
(10, 'Sửa chữa bàn phím', 2, 100.0, 3);

INSERT INTO User_Maintenance (user_id, maintenance_id) 
VALUES
(2, 1),
(3, 2),
(2, 3),
(3, 3);