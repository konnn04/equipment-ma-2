package com.hatecode.utils;

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


            // Image
            stmt.execute("INSERT INTO Image (filename, path, is_active) VALUES " +
                    "('vector-sign-of-user-icon_zspnuk.jpg', 'https://res.cloudinary.com/dg66aou8q/image/upload/v1743568524/vector-sign-of-user-icon_zspnuk.jpg, true')");


            // Category
            stmt.execute("INSERT INTO Category (name, is_active) VALUES " +
                    "('Electronics', true), " +
                    "('Furniture', true), " +
                    "('Vehicles', true), " +
                    "('Office Equipment', true), " +
                    "('Tools', true)");

            // User (password = '123456')
            stmt.execute("INSERT INTO \"User\" (first_name, last_name, username, password, email, phone, role) VALUES " +
                    "('Admin', 'User', 'admin', '123456', 'admin@example.com', '0123456789', 1), " +
                    "('Tech', 'User', 'technician', '123456', 'tech@example.com', '0123456790', 2)");
        }
    }


}
