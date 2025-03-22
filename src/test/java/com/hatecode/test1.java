package com.hatecode;

import com.hatecode.services.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class test1 {
    @Test
    public void testAuthenticateValidUser() throws SQLException {
        // Arrange
        UserService userService = new UserServiceImpl();
        String username = "admin";
        String password = "1";

        // Act
        boolean result = userService.authenticateUser(username, password);

        // Assert
        assertTrue(result);
    }
}
