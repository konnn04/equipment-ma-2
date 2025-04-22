package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBUtils {
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public  static Connection getConnection()  {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tạo bảng Image
            stmt.execute("CREATE TABLE IF NOT EXISTS Image (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "filename VARCHAR(100) NOT NULL UNIQUE, " +
                    "path TEXT NOT NULL, " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id)" +
                    ")");

            // Tạo bảng Category
            stmt.execute("CREATE TABLE IF NOT EXISTS Category (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "name VARCHAR(50) NOT NULL UNIQUE, " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id)" +
                    ")");

            // Tạo bảng User
            stmt.execute("CREATE TABLE IF NOT EXISTS \"User\" (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "email VARCHAR(50) NOT NULL UNIQUE, " +
                    "phone VARCHAR(20) NOT NULL UNIQUE, " +
                    "role INT NOT NULL, " +
                    "avatar_id INT, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (avatar_id) REFERENCES Image(id)" +
                    ")");

            // Tạo bảng Equipment
            stmt.execute("CREATE TABLE IF NOT EXISTS Equipment (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "code VARCHAR(20) NOT NULL UNIQUE, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "status INT NOT NULL, " +
                    "category_id INT NOT NULL, " +
                    "image_id INT, " +
                    "regular_maintenance_day INT NOT NULL CHECK (regular_maintenance_day > 0), " +
                    "last_maintenance_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "description TEXT, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (image_id) REFERENCES Image(id), " +
                    "FOREIGN KEY (category_id) REFERENCES Category(id)" +
                    ")");

            // Tạo bảng Maintenance
            stmt.execute("CREATE TABLE IF NOT EXISTS Maintenance (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "start_datetime TIMESTAMP NOT NULL, " +
                    "end_datetime TIMESTAMP NOT NULL, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id)" +
                    ")");

            // Tạo bảng Equipment_Maintenance
            stmt.execute("CREATE TABLE IF NOT EXISTS Equipment_Maintenance (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "equipment_id INT NOT NULL, " +
                    "maintenance_id INT NOT NULL, " +
                    "technician_id INT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "result INT, " +
                    "repair_name VARCHAR(255), " +
                    "repair_price INT NOT NULL CHECK (repair_price >= 0), " +
                    "inspection_date TIMESTAMP, " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (equipment_id) REFERENCES Equipment(id), " +
                    "FOREIGN KEY (maintenance_id) REFERENCES Maintenance(id), " +
                    "FOREIGN KEY (technician_id) REFERENCES \"User\"(id)" +
                    ")");

            // Tạo bảng Maintenance_Repair_Suggestion
            stmt.execute("CREATE TABLE IF NOT EXISTS Maintenance_Repair_Suggestion (" +
                    "id INT AUTO_INCREMENT NOT NULL UNIQUE, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "suggest_price FLOAT NOT NULL CHECK (suggest_price > 0), " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id)" +
                    ")");

            stmt.execute("INSERT INTO Image (filename, path) VALUES " +
                    "('vector-sign-of-user-icon_zspnuk.jpg', 'https://res.cloudinary.com/dg66aou8q/image/upload/v1743568524/vector-sign-of-user-icon_zspnuk.jpg')");

            stmt.execute("INSERT INTO Category (name) VALUES " +
                    "('Electronics'), " +
                    "('Machinery'), " +
                    "('Vehicles'), " +
                    "('Furniture'), " +
                    "('Tools')");

            stmt.execute("INSERT INTO \"User\" (first_name, last_name, username, password, email, phone, role, avatar_id) VALUES " +
                    "('Nguyen', 'Van A', 'admin', '1', 'admin1@example.com', '1234567890', 1, 1), " +  // ADMIN
                    "('Tran', 'Thi B', 'technician1', '1', 'tech1@example.com', '0987654321', 2, 1), " +  // TECHNICIAN
                    "('Le', 'Van C', 'manager1', '1', 'manager1@example.com', '1122334455', 3, 1), " +  // MANAGER
                    "('Pham', 'Thi D', 'technician2', '1', 'tech2@example.com', '2233445566', 2, 1), " +  // TECHNICIAN
                    "('Hoang', 'Van E', 'manager2', '1', 'manager2@example.com', '3344556677', 3, 1), " +  // MANAGER
                    "('Bui', 'Thi F', 'admin2', '1', 'admin2@example.com', '4455667788', 1, 1), " +  // ADMIN
                    "('Dang', 'Van G', 'technician3', '1', 'tech3@example.com', '5566778899', 2, 1), " +  // TECHNICIAN
                    "('Vu', 'Thi H', 'manager3', '1', 'manager3@example.com', '6677889900', 3, 1), " +  // MANAGER
                    "('Ngo', 'Van I', 'technician4', '1', 'tech4@example.com', '7788990011', 2, 1), " +  // TECHNICIAN
                    "('Do', 'Thi K', 'admin3', '1', 'admin3@example.com', '8899001122', 1, 1)");  // ADMIN

            stmt.execute("INSERT INTO Equipment (code, name, status, category_id, regular_maintenance_day, last_maintenance_time, description, image_id) VALUES " +
                    "('ELEC001', 'Laptop', 2, 1, 180, CURRENT_TIMESTAMP, 'Máy tính xách tay văn phòng', 1), " +
                    "('MACH001', 'Máy khoan', 3, 2, 90, CURRENT_TIMESTAMP, 'Máy khoan công nghiệp', 1), " +
                    "('VEH001', 'Xe tải', 4, 3, 365, CURRENT_TIMESTAMP, 'Xe tải giao hàng', 1), " +
                    "('FURN001', 'Ghế văn phòng', 2, 4, 30, CURRENT_TIMESTAMP, 'Ghế công thái học', 1), " +
                    "('TOOL001', 'Bộ tua vít', 1, 5, 30, CURRENT_TIMESTAMP, 'Bộ dụng cụ cơ bản', 1), " +
                    "('ELEC002', 'Máy chiếu', 2, 1, 180, CURRENT_TIMESTAMP, 'Máy chiếu phòng họp', 1), " +
                    "('MACH002', 'Máy tiện', 2, 2, 90, CURRENT_TIMESTAMP, 'Máy tiện gia công kim loại', 1), " +
                    "('VEH002', 'Xe nâng', 3, 3, 180, CURRENT_TIMESTAMP, 'Xe nâng kho hàng', 1), " +
                    "('FURN002', 'Bàn làm việc', 2, 4, 30, CURRENT_TIMESTAMP, 'Bàn văn phòng', 1), " +
                    "('TOOL002', 'Bộ cờ lê', 2, 5, 30, CURRENT_TIMESTAMP, 'Cờ lê điều chỉnh', 1), " +
                    "('ELEC003', 'Máy in', 2, 1, 90, CURRENT_TIMESTAMP, 'Máy in văn phòng', 1), " +
                    "('MACH003', 'Máy CNC', 3, 2, 180, CURRENT_TIMESTAMP, 'Máy gia công chính xác', 1), " +
                    "('VEH003', 'Xe máy', 2, 3, 180, CURRENT_TIMESTAMP, 'Xe máy giao hàng', 1), " +
                    "('FURN003', 'Bàn họp', 2, 4, 330, CURRENT_TIMESTAMP, 'Bàn họp lớn', 1), " +
                    "('TOOL003', 'Máy khoan cầm tay', 2, 5, 90, CURRENT_TIMESTAMP, 'Máy khoan không dây', 1), " +
                    "('ELEC004', 'Màn hình', 4, 1, 180, CURRENT_TIMESTAMP, 'Màn hình máy tính', 1), " +
                    "('MACH004', 'Máy hàn', 2, 2, 90, CURRENT_TIMESTAMP, 'Máy hàn công nghiệp', 1), " +
                    "('VEH004', 'Xe ô tô', 5, 3, 365, CURRENT_TIMESTAMP, 'Xe công ty cũ', 1), " +
                    "('FURN004', 'Tủ hồ sơ', 2, 4, 330, CURRENT_TIMESTAMP, 'Tủ đựng tài liệu', 1), " +
                    "('TOOL004', 'Búa', 2, 5, 330, CURRENT_TIMESTAMP, 'Búa tay', 1), " +
                    "('ELEP001', 'dád', 2, 1, 180, CURRENT_TIMESTAMP, 'Máy tính xách tay văn phòng', 1)");

            stmt.execute("INSERT INTO Maintenance (title, description, start_datetime, end_datetime) VALUES " +
                    "('Kiểm tra điện tử hàng quý', 'Kiểm tra tất cả thiết bị điện tử', '2023-10-01 09:00:00', '2023-10-01 17:00:00'), " +
                    "('Bảo trì máy móc lớn', 'Bảo trì toàn diện cho máy móc', '2023-11-15 08:00:00', '2023-11-20 18:00:00'), " +
                    "('Kiểm tra xe hàng năm', 'Kiểm tra an toàn xe', '2023-12-01 10:00:00', '2023-12-01 16:00:00'), " +
                    "('Kiểm tra dụng cụ hàng tháng', 'Kiểm tra tất cả dụng cụ', '2023-10-15 09:00:00', '2023-10-15 12:00:00'), " +
                    "('Đánh giá nội thất', 'Kiểm tra tình trạng nội thất', '2023-11-01 10:00:00', '2023-11-01 15:00:00'), " +
                    "('Bảo trì khẩn cấp', 'Xử lý sự cố đột xuất', '2023-12-10 08:00:00', '2023-12-10 14:00:00')");

            stmt.execute("INSERT INTO Equipment_Maintenance (equipment_id, maintenance_id, technician_id, description, result, repair_name, repair_price, inspection_date) VALUES " +
                    "(1, 1, 2, 'Kiểm tra laptop, hoạt động tốt', 1, 'None', 0, '2023-10-01 10:00:00'), " +
                    "(2, 2, 4, 'Máy khoan cần thay động cơ', 2, 'Thay động cơ', 1500000, '2023-11-16 14:00:00'), " +
                    "(3, 3, 2, 'Xe tải hỏng động cơ, không sửa được', 3, 'None', 0, '2023-12-01 11:00:00'), " +
                    "(4, 1, 4, 'Ghế vẫn tốt', 1, 'None', 0, '2023-10-01 11:00:00'), " +
                    "(5, 2, 2, 'Bộ tua vít chưa sử dụng', 1, 'None', 0, '2023-11-17 09:00:00'), " +
                    "(6, 1, 4, 'Máy chiếu cần thay bóng đèn', 2, 'Thay bóng đèn', 500000, '2023-10-01 12:00:00'), " +
                    "(7, 2, 2, 'Máy tiện hoạt động tốt', 1, 'None', 0, '2023-11-18 10:00:00'), " +
                    "(8, 3, 4, 'Xe nâng cần thay lốp', 2, 'Thay lốp', 2000000, '2023-12-01 13:00:00'), " +
                    "(9, 1, 2, 'Bàn làm việc chắc chắn', 1, 'None', 0, '2023-10-01 13:00:00'), " +
                    "(10, 2, 4, 'Bộ cờ lê đầy đủ', 1, 'None', 0, '2023-11-19 11:00:00'), " +
                    "(11, 4, 2, 'Máy in sắp hết mực', 2, 'Thay mực', 200000, '2023-10-15 10:00:00'), " +
                    "(12, 5, 4, 'Máy CNC cần hiệu chỉnh', 2, 'Hiệu chỉnh', 1000000, '2023-11-01 11:00:00'), " +
                    "(13, 3, 2, 'Xe máy hoạt động bình thường', 1, 'None', 0, '2023-12-01 14:00:00'), " +
                    "(14, 5, 4, 'Bàn họp bị xước', 2, 'Sơn lại bề mặt', 500000, '2023-11-01 12:00:00'), " +
                    "(15, 4, 2, 'Máy khoan cầm tay pin yếu', 2, 'Thay pin', 300000, '2023-10-15 11:00:00'), " +
                    "(16, 6, 7, 'Màn hình không lên nguồn', 3, 'None', 0, '2023-12-10 09:00:00'), " +
                    "(17, 2, 9, 'Máy hàn hoạt động tốt', 1, 'None', 0, '2023-11-18 12:00:00'), " +
                    "(18, 3, 7, 'Xe ô tô đã thanh lý', 4, 'None', 0, '2023-12-01 15:00:00'), " +
                    "(19, 5, 9, 'Tủ hồ sơ vẫn tốt', 1, 'None', 0, '2023-11-01 13:00:00'), " +
                    "(20, 4, 7, 'Búa không bị hư hại', 1, 'None', 0, '2023-10-15 10:30:00')");

            stmt.execute("INSERT INTO Maintenance_Repair_Suggestion (name, description, suggest_price) VALUES " +
                    "('Thay động cơ', 'Thay động cơ cho máy khoan', 1500000), " +
                    "('Thay bóng đèn', 'Thay bóng đèn máy chiếu', 500000), " +
                    "('Thay lốp', 'Thay lốp cho xe nâng', 2000000), " +
                    "('Thay mực', 'Thay mực in cho máy in', 200000), " +
                    "('Hiệu chỉnh', 'Hiệu chỉnh máy CNC', 1000000), " +
                    "('Sơn lại bề mặt', 'Sơn lại bề mặt bàn họp', 500000), " +
                    "('Thay pin', 'Thay pin cho máy khoan cầm tay', 300000), " +
                    "('Thay màn hình', 'Thay màn hình máy tính', 1000000), " +
                    "('Bọc lại ghế', 'Bọc lại ghế văn phòng', 300000), " +
                    "('Cập nhật phần mềm', 'Cập nhật phần mềm cho laptop', 100000)");
        }
    }
}
