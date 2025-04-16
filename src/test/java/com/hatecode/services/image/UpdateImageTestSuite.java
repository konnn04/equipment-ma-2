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

@ExtendWith(MockitoExtension.class)
public class UpdateImageTestSuite {
    ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl();
    }

    @Test
    void testUpdateImage_Success() throws Exception {
        Image image = new Image(4, "updated.png", java.time.LocalDateTime.now(), "/images/updated.png");

        Connection mockConn = Mockito.mock(Connection.class);
        PreparedStatement mockStmt = Mockito.mock(PreparedStatement.class);

        // Khi gọi prepareStatement thì trả về mockStmt
        Mockito.when(mockConn.prepareStatement(Mockito.anyString())).thenReturn(mockStmt);

        // Khi gọi executeUpdate thì giả lập là có 1 dòng được cập nhật
        Mockito.when(mockStmt.executeUpdate()).thenReturn(1);

        // Dùng MockedStatic để giả lập phương thức static JdbcUtils.getConn()
        try (MockedStatic<JdbcUtils> mockedStatic = Mockito.mockStatic(JdbcUtils.class)) {
            mockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            // Act
            boolean result = imageService.updateImage(image);

            // Assert
            assertTrue(result);
            Mockito.verify(mockStmt).setString(1, image.getFilename());
            Mockito.verify(mockStmt).setTimestamp(2, Timestamp.valueOf(image.getCreateDate()));
            Mockito.verify(mockStmt).setString(3, image.getPath());
            Mockito.verify(mockStmt).setInt(4, image.getId());
        }
    }
    
    @Test
    void testUpdateImage_NotFound() throws Exception{
        // Arrange
        Image image = new Image(99, "nope.png", java.time.LocalDateTime.now(), "/images/nope.png");

        Connection mockConn = Mockito.mock(Connection.class);
        PreparedStatement mockStmt = Mockito.mock(PreparedStatement.class);

        Mockito.when(mockConn.prepareStatement(Mockito.anyString())).thenReturn(mockStmt);
        Mockito.when(mockStmt.executeUpdate()).thenReturn(0); // không có dòng nào được cập nhật

        try (MockedStatic<JdbcUtils> mockedStatic = Mockito.mockStatic(JdbcUtils.class)) {
            mockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            // Act
            boolean result = imageService.updateImage(image);
            // Assert
            assertFalse(result);
        }
    }

    @Test
    void testUpdateImage_NullPath_ShouldThrowIllegalArgumentException() {
        Image image = new Image(1, "file.jpg", LocalDateTime.now(), null);
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.updateImage(image);
        });
    }

    @Test
    void testUpdateImage_NullDate_ShouldThrowIllegalArgumentException() {
        Image image = new Image(1, "file.jpg", null,"error.png");
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.updateImage(image);
        });
    }
    
}
