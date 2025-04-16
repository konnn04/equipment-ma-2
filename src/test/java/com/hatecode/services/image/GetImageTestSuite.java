package com.hatecode.services.image;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
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
    private static Connection conn;
    private ImageServiceImpl imageService;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/equipmentma2", "root", "123456");
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        if (conn != null) conn.close();
    }

    @BeforeEach
    public void setUp() throws Exception {
        imageService = new ImageServiceImpl();

        // Dọn dẹp và chèn dữ liệu test
        String sqlDelete = "DELETE FROM Image WHERE filename LIKE 'test-image%'";
        String sqlInsert = "INSERT INTO Image (filename, created_date, path) VALUES (?, ?, ?)";
        try (PreparedStatement delStmt = conn.prepareStatement(sqlDelete);
             PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
            delStmt.executeUpdate();

            insertStmt.setString(1, "test-image-1.jpg");
            insertStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            insertStmt.setString(3, "/images/test-image-1.jpg");
            insertStmt.executeUpdate();

            insertStmt.setString(1, "test-image-2.jpg");
            insertStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            insertStmt.setString(3, "/images/test-image-2.jpg");
            insertStmt.executeUpdate();
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        String sql = "DELETE FROM Image WHERE filename LIKE 'test-image%'";
        conn.createStatement().executeUpdate(sql);
    }

    @Test
    public void testGetImages() throws Exception {
        List<Image> images = imageService.getImages();

        assertNotNull(images);
        assertTrue(images.stream().anyMatch(img -> img.getFilename().equals("test-image-1.jpg")));
        assertTrue(images.stream().anyMatch(img -> img.getFilename().equals("test-image-2.jpg")));
    }
}
