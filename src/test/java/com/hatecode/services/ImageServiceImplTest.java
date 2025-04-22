package com.hatecode.services;

import com.hatecode.pojo.Image;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.TestDBUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImageServiceImplTest {
    private Connection conn;
    private ImageService imageService;

    @BeforeEach
    void setupTestData() throws SQLException {
        conn = TestDBUtils.createIsolatedConnection();
        imageService = new ImageServiceImpl(conn);
        
        // Initialize test data
        String sql = """
                INSERT INTO Image (id, filename, created_at, path) 
                VALUES (1, 'test_img', NOW(), 'test_img.jpg');
                """;
        
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /*=============================================================================
     * Test getImage methods
     *===========================================================================*/
    
    @Test
    public void testGetImages() throws Exception {
        List<Image> images = imageService.getImages();
        
        assertNotNull(images);
        assertFalse(images.isEmpty());
        assertTrue(images.stream().anyMatch(img -> img.getFilename().equals("test_img")));
    }
    
    @Test
    public void testGetImageById() throws Exception {
        // Test getting an existing image
        Image image = imageService.getImageById(1);
        
        assertNotNull(image);
        assertEquals("test_img", image.getFilename());
        assertEquals("test_img.jpg", image.getPath());
    }
    
    @Test
    public void testGetImageById_NotFound() throws Exception {
        // Test getting a non-existent image
        Image image = imageService.getImageById(999);
        
        assertNull(image);
    }
    
    @Test
    public void testGetImageByPath() throws Exception {
        // Test getting an image by path
        Image image = imageService.getImageByPath("test_img.jpg");
        
        assertNotNull(image);
        assertEquals("test_img", image.getFilename());
        assertEquals(1, image.getId());
    }
    
    @Test
    public void testGetImageByPath_NotFound() throws Exception {
        // Test getting a non-existent image by path
        Image image = imageService.getImageByPath("nonexistent.jpg");
        
        assertNull(image);
    }

    /*=============================================================================
     * Test addImage methods
     *===========================================================================*/
    
    @Test
    public void testAddImage_Success() throws SQLException {
        Image img = new Image("new_test_image.png", LocalDateTime.now(), "/images/new_test_image.png");
        
        boolean result = imageService.addImage(img);
        
        assertTrue(result);
        assertNotNull(imageService.getImageByPath("/images/new_test_image.png"));
    }

    @Test
    public void testAddImage_DuplicateFilename() throws SQLException {
        // First add an image
        String filename = "duplicate_image.png";
        Image img1 = new Image(filename, LocalDateTime.now(), "/images/dup1.png");
        imageService.addImage(img1);
        
        // Then try to add another with the same filename
        Image img2 = new Image(filename, LocalDateTime.now(), "/images/dup2.png");
        
        assertThrows(SQLException.class, () -> {
            imageService.addImage(img2);
        });
    }

    @Test
    public void testAddImage_NullPath() {
        Image img = new Image(0, "null_path_image.png", LocalDateTime.now(), null);
        
        assertThrows(SQLException.class, () -> {
            imageService.addImage(img);
        });
    }

    @Test
    public void testAddImage_NullFilename() {
        Image img = new Image(0, null, LocalDateTime.now(), "/images/null.png");
        
        assertThrows(SQLException.class, () -> {
            imageService.addImage(img);
        });
    }

    @Test
    public void testAddImage_NullCreatedDate() {
        Image img = new Image(0, "no_date.png", null, "/images/no_date.png");
        
        assertThrows(NullPointerException.class, () -> {
            imageService.addImage(img);
        });
    }

    /*=============================================================================
     * Test updateImage methods
     *===========================================================================*/
    
    @Test
    void testUpdateImage_Success() throws Exception {
        Image image = new Image(1, "test_updated_img", LocalDateTime.now(), "/images/updated.png");

        boolean result = imageService.updateImage(image);
        
        assertTrue(result);

        // Verify the update
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM Image WHERE id = ?");
        stm.setInt(1, 1);
        ResultSet rs = stm.executeQuery();
        
        assertTrue(rs.next());
        assertEquals("test_updated_img", rs.getString("filename"));
        assertEquals("/images/updated.png", rs.getString("path"));
    }

    @Test
    void testUpdateImage_NotFound() throws Exception {
        // Non-existent ID 999
        Image image = new Image(999, "nonexistent_img", LocalDateTime.now(), "/images/none.png");

        boolean result = imageService.updateImage(image);
        
        assertFalse(result);
    }

    @Test
    void testUpdateImage_NullPath_ShouldThrowIllegalArgumentException() {
        Image image = new Image(1, "file.jpg", LocalDateTime.now(), null);
        
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.updateImage(image);
        });
    }
    
    @Test
    void testUpdateImage_WithConnection_Success() throws Exception {
        Image image = new Image(1, "test_updated_img2", LocalDateTime.now(), "/images/updated2.png");

        boolean result = imageService.updateImage(conn, image);
        
        assertTrue(result);
        
        // Verify the update
        Image updated = imageService.getImageById(1);
        assertEquals("test_updated_img2", updated.getFilename());
        assertEquals("/images/updated2.png", updated.getPath());
    }

    /*=============================================================================
     * Test deleteImage methods
     *===========================================================================*/
    
    @Test
    void testDeleteImage_IdExists_ShouldReturnTrue() throws Exception {
        // First add an image to delete
        Image img = new Image("to_delete.png", LocalDateTime.now(), "/images/to_delete.png");
        imageService.addImage(img);
        
        // Find its ID
        Image addedImage = imageService.getImageByPath("/images/to_delete.png");
        assertNotNull(addedImage);
        
        // Delete the image
        boolean result = imageService.deleteImage(addedImage.getId());
        
        assertTrue(result);
        assertNull(imageService.getImageById(addedImage.getId()));
    }

    @Test
    void testDeleteImage_IdDoesNotExist_ShouldReturnFalse() throws Exception {
        int id = 9999;
        
        boolean result = imageService.deleteImage(id);
        
        assertFalse(result);
    }

    @Test
    void testDeleteImage_InvalidId_ShouldThrowException() throws Exception {
        int invalidId = -1;

        assertThrows(IllegalArgumentException.class, () -> {
            imageService.deleteImage(invalidId);
        });
    }
    
    @Test
    void testDeleteImage_WithConnection_IdExists_ShouldReturnTrue() throws Exception {
        // First add an image to delete
        Image img = new Image("to_delete_conn.png", LocalDateTime.now(), "/images/to_delete_conn.png");
        imageService.addImage(img);
        
        // Find its ID
        Image addedImage = imageService.getImageByPath("/images/to_delete_conn.png");
        assertNotNull(addedImage);
        
        // Delete the image using connection
        boolean result = imageService.deleteImage(conn, addedImage.getId());
        
        assertTrue(result);
        assertNull(imageService.getImageById(addedImage.getId()));
    }
}