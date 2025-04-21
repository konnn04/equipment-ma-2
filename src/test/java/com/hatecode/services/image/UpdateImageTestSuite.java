package com.hatecode.services.image;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.TestDBUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateImageTestSuite {

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
    void testUpdateImage_Success() throws Exception {
        Image image = new Image(1, "test_updated_img", LocalDateTime.now(), "/images/updated.png");

        boolean result = imageService.updateImage(this.conn,image);
        assertTrue(result);

        PreparedStatement stm = conn.prepareStatement("SELECT * FROM Image WHERE id = ?");
        stm.setInt(1, 1);
        ResultSet rs = stm.executeQuery();
        assertTrue(rs.next());
        assertEquals("test_updated_img", rs.getString("filename"));
        assertEquals("/images/updated.png", rs.getString("path"));
    }

    @Test
    void testUpdateImage_NotFound() throws Exception {
        // Không tồn tại ID 999
        Image image = new Image(999, "nonexistent_img", LocalDateTime.now(), "/images/none.png");

        boolean result = imageService.updateImage(this.conn,image);
        assertFalse(result);
    }

    @Test
    void testUpdateImage_NullPath_ShouldThrowIllegalArgumentException() {
        Image image = new Image(1, "file.jpg", LocalDateTime.now(), null);
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.updateImage(this.conn,image);
        });
    }
}
