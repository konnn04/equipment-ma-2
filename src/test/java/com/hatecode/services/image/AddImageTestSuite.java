/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.image;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ADMIN
 */
public class AddImageTestSuite {
    ImageService imageService = new ImageServiceImpl();

    @BeforeEach
    public void setUp() throws SQLException {
        // Xoá dữ liệu test cũ nếu có
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Image WHERE filename LIKE 'test%' OR filename LIKE 'multi%' OR filename = 'duplicate_image.png'")) {
            stmt.executeUpdate();
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        setUp(); // Cũng dùng setUp để dọn lại sau khi test
    }

    @Test
    public void testAddImage_Success() throws SQLException {
        Image img = new Image( "test_image.png", LocalDateTime.now(), "/images/test_image.png");
        boolean result = this.imageService.addImage(img);
        assertTrue(result);
    }

    @Test
    public void testAddImage_DuplicateFilename() throws SQLException {
        String filename = "duplicate_image.png";
        Image img1 = new Image(filename, LocalDateTime.now(), "/images/dup1.png");
        Image img2 = new Image(filename, LocalDateTime.now(), "/images/dup2.png");

        // Thêm ảnh đầu tiên
        this.imageService.addImage(img1);

        // Thêm ảnh thứ hai bị trùng tên
        assertThrows(SQLException.class, () -> {
            this.imageService.addImage(img2);
        });
    }

    @Test
    public void testAddImage_NullPath() {
        Image img = new Image(0, "null_path_image.png", LocalDateTime.now(), null);
        assertThrows(SQLException.class, () -> {
            this.imageService.addImage(img);
        });
    }

    @Test
    public void testAddImage_NullFilename() {
        Image img = new Image(0, null, LocalDateTime.now(), "/images/null.png");
        assertThrows(SQLException.class, () -> {
            this.imageService.addImage(img);
        });
    }

    @Test
    public void testAddImage_NullCreatedDate() {
        Image img = new Image(0, "no_date.png", null, "/images/no_date.png");
        assertThrows(NullPointerException.class, () -> {
            this.imageService.addImage(img);
        });
    }

}
