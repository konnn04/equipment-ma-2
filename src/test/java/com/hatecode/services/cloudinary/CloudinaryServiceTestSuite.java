package com.hatecode.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import com.hatecode.services.impl.CloundinaryServicesImpl;
import com.hatecode.services.interfaces.CloundinaryServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CloudinaryServiceTestSuite {

    private Cloudinary mockCloudinary;
    private Uploader mockUploader;
    private CloundinaryServices service;

    @BeforeEach
    void setUp() throws Exception {
        mockCloudinary = mock(Cloudinary.class);
        mockUploader = mock(Uploader.class);

        when(mockCloudinary.uploader()).thenReturn(mockUploader);

        service = new CloundinaryServicesImpl(mockCloudinary); // constructor mới hỗ trợ test
    }

    @Test
    void testUploadImage_SuccessfulUpload_ReturnsSecureUrl() throws Exception {
        File mockFile = mock(File.class);
        Map<String, Object> mockResponse = Map.of("secure_url", "https://res.cloudinary.com/demo/image/upload/sample.jpg");

        when(mockUploader.upload(eq(mockFile), anyMap())).thenReturn(mockResponse);

        String result = service.uploadImage(mockFile);

        assertEquals("https://res.cloudinary.com/demo/image/upload/sample.jpg", result);
    }

    @Test
    void testUploadImage_Exception_ReturnsNull() throws Exception {
        File mockFile = mock(File.class);
        when(mockUploader.upload(eq(mockFile), anyMap())).thenThrow(new RuntimeException("Simulated error"));

        String result = service.uploadImage(mockFile);

        assertNull(result);
    }

    @Test
    void testDeleteImage_Successful_ReturnsTrue() throws Exception {
        String fakeUrl = "https://res.cloudinary.com/demo/image/upload/v1234567/public_id.jpg";
        Map<String, Object> mockResponse = Map.of("result", "ok");

        when(mockUploader.destroy(eq("public_id"), anyMap())).thenReturn(mockResponse);

        boolean result = service.deleteImage(fakeUrl);

        assertTrue(result);
    }

    @Test
    void testDeleteImage_Failed_ReturnsFalse() throws Exception {
        String fakeUrl = "https://res.cloudinary.com/demo/image/upload/v1234567/public_id.jpg";
        Map<String, Object> mockResponse = Map.of("result", "not_found");

        when(mockUploader.destroy(eq("public_id"), anyMap())).thenReturn(mockResponse);

        boolean result = service.deleteImage(fakeUrl);

        assertFalse(result);
    }

    @Test
    void testExtractPublicIdFromUrl() {
        String url = "https://res.cloudinary.com/demo/image/upload/v1234567/somefolder/my_image.png";
        String extracted = CloundinaryServicesImpl.extractPublicIdFromUrl(url);

        assertEquals("my_image", extracted);
    }

    @Test
    void testGetImageUrl_GeneratesUrlCorrectly() throws SQLException {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "demo"); // Bắt buộc
        config.put("api_key", "fake_key");
        config.put("api_secret", "fake_secret");

        Cloudinary spyCloudinary = spy(new Cloudinary(config));
        CloundinaryServices service = new CloundinaryServicesImpl(spyCloudinary);

        String publicId = "test_image";
        String url = service.getImageUrl(publicId);

        assertNotNull(url);
        assertTrue(url.contains(publicId));
    }

    @Test
    void testUploadImage_MissingApiKey_ThrowsExceptionOrReturnsNull() {
        Map<String, String> badConfig = new HashMap<>();
        badConfig.put("cloud_name", "demo");
        // Không có api_key và api_secret

        Cloudinary brokenCloud = new Cloudinary(badConfig);
        CloundinaryServices service = new CloundinaryServicesImpl(brokenCloud);

        File file = new File("test.jpg"); // giả định là có file

        String result = null;
        try {
            result = service.uploadImage(file);
        } catch (SQLException e) {
            fail("Should not throw SQLException, should handle error gracefully");
        }

        assertNull(result); // vì không upload được => null
    }

}
