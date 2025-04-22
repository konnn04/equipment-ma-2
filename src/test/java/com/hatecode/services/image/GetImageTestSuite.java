package com.hatecode.services.image;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.TestDBUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetImageTestSuite {
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
    public void testGetImages() throws Exception {
        List<Image> images = imageService.getImages(conn);
        assertNotNull(images);
        assertTrue(images.stream().anyMatch(img -> img.getFilename().equals("test_img")));
    }
}
