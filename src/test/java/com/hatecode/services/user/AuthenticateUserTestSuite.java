/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.user;

import com.hatecode.pojo.User;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.UserService;
import com.hatecode.utils.PasswordUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ADMIN
 */
public class AuthenticateUserTestSuite {
    private UserService services;
    private Connection conn;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/equipmentma2", "root", "123456");
        services = new UserServiceImpl();

        // Xóa dữ liệu cũ (chỉ dùng khi test)
        conn.createStatement().executeUpdate("DELETE FROM User WHERE username = 'testuser'");

        // Tạo user test
        String hashedPassword = PasswordUtils.hashPassword("test123");
        String insertSQL = "INSERT INTO User (first_name, last_name, username, password, email, phone, role, is_active,avatar) " +
                "VALUES ('Test', 'User', 'testuser', ?, 'test@example.com', '0123456789', 1, 1, 1)";
        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
        pstmt.setString(1, hashedPassword);
        pstmt.executeUpdate();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Dọn dẹp user test
        conn.createStatement().executeUpdate("DELETE FROM User WHERE username = 'testuser'");
        conn.close();
    }

    @Test
    void testAuthenticateUser_Success() throws SQLException {
        User user = services.authenticateUser("testuser", "test123");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testAuthenticateUser_WrongPassword() throws SQLException {
        User user = services.authenticateUser("testuser", "wrongpass");
        assertNull(user);
    }

    @Test
    void testAuthenticateUser_UsernameNotFound() throws SQLException {
        User user = services.authenticateUser("notfound", "test123");
        assertNull(user);
    }
}
