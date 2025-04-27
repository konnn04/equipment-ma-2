package com.hatecode.services;

import com.hatecode.config.TestDatabaseConfig;
import com.hatecode.pojo.Notification;
import com.hatecode.services.impl.NotificationServiceImpl;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseConfig.class)
public class NotificationServiceImplTest {
    @BeforeAll
    static void setupDatabase() throws SQLException {
        JdbcUtils.fileName = "db";
    }
    @BeforeEach
    void setupTestData() throws SQLException {

        // Reset database to clean state
        JdbcUtils.resetDatabase();
        
        // Khởi tạo dữ liệu mẫu
        String sql = """
                INSERT INTO category (name)
                VALUES ('Test Category');
                INSERT INTO equipment (code, name, status, category_id, image_id, regular_maintenance_day, description)
                VALUES ('TEST001', 'Test Equipment 1', 1, 1, 1, 30, 'Test description 1'),
                       ('TEST002', 'Test Equipment 2', 2, 1, 1, 60, 'Test description 2'),
                       ('TEST003', 'Test Equipment 3', 1, 1, 1, 90, 'Test description 3'),
                          ('TEST004', 'Test Equipment 4', 1, 1, 1, 90, 'Test description 4');
                INSERT INTO notifications (equipment_id, equipment_name, equipment_code, maintenance_due_date, type, is_read)
                VALUES (1, 'Test Equipment 1', 'EQ001', '2025-06-01 10:00:00', 1, false),
                       (2, 'Test Equipment 2', 'EQ002', '2025-07-15 09:00:00', 2, true),
                       (3, 'Test Equipment 3', 'EQ003', '2025-08-30 14:00:00', 1, false);
                """;

        try (Connection conn = JdbcUtils.getConn();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        JdbcUtils.closeConnection();
    }

    @Test
    void testGetAllNotifications() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        List<Notification> notifications = notificationService.getAllNotifications();
        
        // Assert
        assertNotNull(notifications);
        assertEquals(3, notifications.size());
    }

    @Test
    void testGetUnreadNotifications() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications();
        
        // Assert
        assertNotNull(unreadNotifications);
        assertEquals(2, unreadNotifications.size());
        assertTrue(unreadNotifications.stream().allMatch(n -> !n.isRead()));
    }
    
    @Test
    void testGetNotificationById() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        Notification notification = notificationService.getNotificationById(1);
        
        // Assert
        assertNotNull(notification);
        assertEquals(1, notification.getId());
        assertEquals("Test Equipment 1", notification.getEquipmentName());
        assertEquals("EQ001", notification.getEquipmentCode());
    }
    
    @Test
    void testGetNotificationById_NotFound() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        Notification notification = notificationService.getNotificationById(999);
        
        // Assert
        assertNull(notification);
    }
    
    @Test
    void testAddNotification() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Arrange
        Notification notification = new Notification(
            0, // ID will be assigned by database
            4, // Equipment ID
            "New Test Equipment",
            "EQ004",
            LocalDateTime.now().plusDays(30),
            LocalDateTime.now(),
            false,
            Notification.NotificationType.MAINTENANCE_DUE
        );
        
        // Act
        boolean result = notificationService.addNotification(notification);
        
        // Assert
        assertTrue(result);
        
        // Verify it was added
        List<Notification> allNotifications = notificationService.getAllNotifications();
        assertEquals(4, allNotifications.size());
        
        // Find the newly added notification
        Notification addedNotification = allNotifications.stream()
            .filter(n -> n.getEquipmentCode().equals("EQ004"))
            .findFirst()
            .orElse(null);
            
        assertNotNull(addedNotification);
        assertEquals("New Test Equipment", addedNotification.getEquipmentName());
    }
    
    @Test
    void testMarkAsRead() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Verify notification 1 is unread
        Notification beforeUpdate = notificationService.getNotificationById(1);
        assertFalse(beforeUpdate.isRead());
        
        // Act
        boolean result = notificationService.markAsRead(1);
        
        // Assert
        assertTrue(result);
        
        // Verify it was updated
        Notification afterUpdate = notificationService.getNotificationById(1);
        assertTrue(afterUpdate.isRead());
    }
    
    @Test
    void testMarkAsRead_NotFound() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        boolean result = notificationService.markAsRead(999);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testMarkAllAsRead() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Verify we have unread notifications
        List<Notification> beforeUpdate = notificationService.getUnreadNotifications();
        assertFalse(beforeUpdate.isEmpty());
        
        // Act
        boolean result = notificationService.markAllAsRead();
        
        // Assert
        assertTrue(result);
        
        // Verify all are now read
        List<Notification> afterUpdate = notificationService.getUnreadNotifications();
        assertTrue(afterUpdate.isEmpty());
    }
    
    @Test
    void testDeleteNotification() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Verify notification 3 exists
        Notification beforeDelete = notificationService.getNotificationById(3);
        assertNotNull(beforeDelete);
        
        // Act
        boolean result = notificationService.deleteNotification(3);
        
        // Assert
        assertTrue(result);
        
        // Verify it was deleted
        Notification afterDelete = notificationService.getNotificationById(3);
        assertNull(afterDelete);
    }
    
    @Test
    void testDeleteNotification_NotFound() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        boolean result = notificationService.deleteNotification(999);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testCountUnreadNotifications() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Act
        int count = notificationService.countUnreadNotifications();
        
        // Assert
        assertEquals(2, count);
    }

    // Bổ sung thêm các trường hợp kiểm thử sau đây

    @Test
    void testAddNotification_WithNullValues() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Arrange
        Notification notification = new Notification(
            0,
            4, 
            null, // Null equipment name
            "EQ004",
            LocalDateTime.now().plusDays(30),
            LocalDateTime.now(),
            false,
            Notification.NotificationType.MAINTENANCE_DUE
        );
        
        // Act & Assert - Kiểm tra xử lý null
        assertThrows(SQLException.class, () -> notificationService.addNotification(notification));
    }

    @Test
    void testDeleteAllNotificationsForEquipment() throws SQLException {
        NotificationService notificationService = new NotificationServiceImpl();
        
        // Arrange - Thêm thông báo cho thiết bị
        int equipmentId = 4;
        Notification notification1 = new Notification(
            0,
            equipmentId,
            "Equipment Test",
            "EQ004",
            LocalDateTime.now().plusDays(30),
            LocalDateTime.now(),
            false,
            Notification.NotificationType.MAINTENANCE_DUE
        );
        
        Notification notification2 = new Notification(
            0,
            equipmentId,
            "Equipment Test",
            "EQ004",
            LocalDateTime.now().plusDays(60),
            LocalDateTime.now(),
            false,
            Notification.NotificationType.MAINTENANCE_DUE
        );
        
        notificationService.addNotification(notification1);
        notificationService.addNotification(notification2);
        
        // Act
        String sql = "DELETE FROM notifications WHERE equipment_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipmentId);
            stmt.executeUpdate();
        }
        
        // Assert
        List<Notification> notifications = notificationService.getAllNotifications();
        boolean hasEquipmentNotifications = false;
        for (Notification n : notifications) {
            if (n.getEquipmentId() == equipmentId) {
                hasEquipmentNotifications = true;
                break;
            }
        }
        assertFalse(hasEquipmentNotifications);
    }
}