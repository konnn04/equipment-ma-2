package com.hatecode.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.hatecode.config.AppConfig;
import com.hatecode.services.CloundinaryService;
import com.hatecode.services.impl.CloudinaryServiceImpl;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CloudinaryServiceTestSuite {
    private CloundinaryService cloundinaryService;
    private String uploadedUrl;
    @BeforeEach
    void setUp() {
        // Config Cloudinary for testing
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", AppConfig.CLOUD_NAME);
        config.put("api_key", AppConfig.API_KEY);
        config.put("api_secret", AppConfig.API_SECRET);
        Cloudinary cloudinary = new Cloudinary(config);
        cloundinaryService = new CloudinaryServiceImpl(cloudinary);
        File file = new File("src/test/resources/com/hatecode/services/cloudinary/sample.png");
        try{
            uploadedUrl = cloundinaryService.uploadImage(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown(){
        try{
            if(!uploadedUrl.isEmpty() && uploadedUrl != null){
                boolean result = cloundinaryService.deleteImage(uploadedUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testUploadImage_Success() throws SQLException {
        File file = new File("src/test/resources/com/hatecode/services/cloudinary/sample_2.png"); // file ảnh test có sẵn
        String url = cloundinaryService.uploadImage(file);
        assertNotNull(url);
        assertTrue(url.contains("res.cloudinary.com"));
    }

    @Test
    void testDeleteImage_Success() throws SQLException {
        File file = new File("src/test/resources/com/hatecode/services/cloudinary/sample.png");
        String uploadedUrl = cloundinaryService.uploadImage(file);
        assertNotNull(uploadedUrl);
        boolean result = cloundinaryService.deleteImage(uploadedUrl);
        assertTrue(result);
    }

    @Test
    void testDeleteImage_InvalidPublicId_ReturnsFalse() throws SQLException {
        String fakeUrl = "https://res.cloudinary.com/demo/image/upload/v1234567/invalid_id.jpg";

        boolean result = cloundinaryService.deleteImage(fakeUrl);

        assertFalse(result);
    }

    @Test
    void testExtractPublicIdFromUrl() {
        String url = "https://res.cloudinary.com/demo/image/upload/v1234567/somefolder/my_image.png";
        String extracted = CloudinaryServiceImpl.extractPublicIdFromUrl(url);
        assertEquals("my_image", extracted);
    }

    @Test
    void testGetImageUrl() throws SQLException {
        String publicId = "sample";
        String url = cloundinaryService.getImageUrl(publicId);
        assertNotNull(url);
        assertTrue(url.contains(publicId));
    }
}
