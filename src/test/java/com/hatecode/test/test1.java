package com.hatecode.test;

import com.hatecode.services.interfaces.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class test1 {
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        userService = new UserServiceImpl();
//    }
//
//    @Test
//    public void shouldAuthenticateValidUser() throws SQLException {
//        // Tạo đối tượng UserService trong phương thức test
//        // vì @BeforeEach có thể không được gọi đúng cách
//        UserService userService = new UserServiceImpl();
//
//        boolean result = userService.authenticateUser("admin", "1");
//        assertTrue(result, "Người dùng hợp lệ phải đăng nhập được");
//    }
//
//    @Test
//    public void shouldRejectInvalidUser() throws SQLException {
//        // Tạo đối tượng UserService trong phương thức test
//        UserService userService = new UserServiceImpl();
//
//        boolean result = userService.authenticateUser("admin", "wrongpassword");
//        assertFalse(result, "Người dùng với mật khẩu sai không được đăng nhập");
//    }
}