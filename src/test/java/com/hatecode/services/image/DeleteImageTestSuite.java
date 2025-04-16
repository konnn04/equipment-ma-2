/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.image;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 *
 * @author ADMIN
 */
@ExtendWith(MockitoExtension.class)
public class DeleteImageTestSuite {
    ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl();
    }

    @Test
    void testDeleteImage_IdExists_ShouldReturnTrue() throws Exception {
        int id = 1;

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(1); // 1 dòng bị xóa

        try (MockedStatic<JdbcUtils> mocked = mockStatic(JdbcUtils.class)) {
            mocked.when(JdbcUtils::getConn).thenReturn(mockConn);

            ImageServiceImpl service = new ImageServiceImpl();
            boolean result = service.deleteImage(id);

            assertTrue(result);
            verify(mockStmt).setInt(1, id);
        }
    }

    @Test
    void testDeleteImage_IdDoesNotExist_ShouldReturnFalse() throws Exception {
        int id = 99;

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(0); // Không dòng nào bị xóa

        try (MockedStatic<JdbcUtils> mocked = mockStatic(JdbcUtils.class)) {
            mocked.when(JdbcUtils::getConn).thenReturn(mockConn);

            ImageServiceImpl service = new ImageServiceImpl();
            boolean result = service.deleteImage(id);

            assertFalse(result);
            verify(mockStmt).setInt(1, id);
        }
    }

    @Test
    void testDeleteImage_InvalidId_ShouldReturnFalseOrThrow() throws Exception {
        int invalidId = -1;

        // Có thể bạn muốn xử lý ném lỗi IllegalArgumentException
        ImageServiceImpl service = new ImageServiceImpl();

        assertThrows(IllegalArgumentException.class, () -> {
            service.deleteImage(invalidId);
        });
    }
}
