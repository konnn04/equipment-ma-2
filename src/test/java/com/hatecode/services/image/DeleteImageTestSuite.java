/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.image;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.TestDBUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class DeleteImageTestSuite {
    private Connection conn;
    private ImageService imageService;

    @BeforeEach
    void setUp() throws SQLException {
        conn = TestDBUtils.createIsolatedConnection();
        imageService = new ImageServiceImpl();

        // Insert sample image
        String sql = "INSERT INTO Image (filename, path) VALUES ('test_img', 'test_img.jpg')";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            JdbcUtils.resetTestConnection();
            conn.close();
        }
    }

    @Test
    void testDeleteImage_IdExists_ShouldReturnTrue() throws Exception {
        int id = 2;
        boolean result = imageService.deleteImage(conn,id);
        assertTrue(result);
    }

    @Test
    void testDeleteImage_IdDoesNotExist_ShouldReturnFalse() throws Exception {
        int id = 99;
        boolean result = imageService.deleteImage(conn,id);
        assertFalse(result);

    }

    @Test
    void testDeleteImage_InvalidId_ShouldReturnFalseOrThrow() throws Exception {
        int invalidId = -1;

        assertThrows(IllegalArgumentException.class, () -> {
            imageService.deleteImage(conn,invalidId);
        });
    }
}
