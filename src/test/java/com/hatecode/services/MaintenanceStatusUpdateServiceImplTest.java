package com.hatecode.services;

import com.hatecode.config.TestDatabaseConfig;
import com.hatecode.pojo.*;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.MaintenanceStatusUpdateServiceImpl;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseConfig.class)
public class MaintenanceStatusUpdateServiceImplTest {

    private EquipmentService equipmentService;

    private MaintenanceService maintenanceService;

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
                INSERT INTO `user` (first_name, last_name, username, password, email, phone, role, avatar_id)
                VALUES ('Nguyen', 'Van A', 'technician0', '123456', 'a@gmail.com', '0123456789', 2, 1);
                INSERT INTO Category (name)
                VALUES ('Test Category');
                
                INSERT INTO equipment (code, name, status, category_id, image_id, regular_maintenance_day, description)
                VALUES ('TEST001', 'Test Equipment 1', 1, 1, 1, 30, 'Test description 1'),
                       ('TEST002', 'Test Equipment 2', 2, 1, 1, 60, 'Test description 2'),
                       ('TEST003', 'Test Equipment 3', 1, 1, 1, 90, 'Test description 3');
                       
                INSERT INTO maintenance (title, description, start_datetime, end_datetime, status)
                VALUES ('Pending Maintenance', 'Future maintenance', '2025-06-01 08:00:00', '2025-06-05 17:00:00', 1),
                       ('Current Maintenance', 'Ongoing maintenance', '2023-01-01 08:00:00', '2026-12-31 17:00:00', 2),
                       ('Completed Maintenance', 'Past maintenance', '2022-01-01 08:00:00', '2022-01-05 17:00:00', 3);
                       
                INSERT INTO equipment_maintenance (equipment_id, maintenance_id, technician_id, description, equipment_name, equipment_code)
                VALUES (1, 1, 1, 'Pending maintenance for equipment 1', 'Test Equipment 1', 'TEST001'),
                       (2, 2, 1, 'Current maintenance for equipment 2', 'Test Equipment 2', 'TEST002'),
                       (3, 3, 1, 'Completed maintenance for equipment 3', 'Test Equipment 3', 'TEST003');
                """;

        try (Connection conn = JdbcUtils.getConn();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
        
        maintenanceService = new MaintenanceServiceImpl();

        equipmentService = new EquipmentServiceImpl();
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        JdbcUtils.closeConnection();
    }
    
    @Test
    void testDetermineMaintenanceStatus_Pending() throws SQLException {
        MaintenanceStatusUpdateServiceImpl service = new MaintenanceStatusUpdateServiceImpl();
        
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(1); // Pending maintenance
        LocalDateTime now = LocalDateTime.now();
        
        // Use reflection to access private method
        java.lang.reflect.Method determineMaintenanceStatus;
        try {
            determineMaintenanceStatus = MaintenanceStatusUpdateServiceImpl.class.getDeclaredMethod(
                "determineMaintenanceStatus", Maintenance.class, LocalDateTime.class);
            determineMaintenanceStatus.setAccessible(true);
            
            // Act
            MaintenanceStatus status = (MaintenanceStatus) determineMaintenanceStatus.invoke(
                service, maintenance, now);
            
            // Assert
            assertEquals(MaintenanceStatus.PENDING, status);
        } catch (Exception e) {
            fail("Exception occurred while testing: " + e.getMessage());
        }
    }
    
    @Test
    void testDetermineMaintenanceStatus_InProgress() throws SQLException {
        MaintenanceStatusUpdateServiceImpl service = new MaintenanceStatusUpdateServiceImpl();
        
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(2); // Ongoing maintenance
        LocalDateTime now = LocalDateTime.now();
        
        // Use reflection to access private method
        java.lang.reflect.Method determineMaintenanceStatus;
        try {
            determineMaintenanceStatus = MaintenanceStatusUpdateServiceImpl.class.getDeclaredMethod(
                "determineMaintenanceStatus", Maintenance.class, LocalDateTime.class);
            determineMaintenanceStatus.setAccessible(true);
            
            // Act
            MaintenanceStatus status = (MaintenanceStatus) determineMaintenanceStatus.invoke(
                service, maintenance, now);
            
            // Assert
            assertEquals(MaintenanceStatus.IN_PROGRESS, status);
        } catch (Exception e) {
            fail("Exception occurred while testing: " + e.getMessage());
        }
    }
    
    @Test
    void testDetermineMaintenanceStatus_Completed() throws SQLException {
        MaintenanceStatusUpdateServiceImpl service = new MaintenanceStatusUpdateServiceImpl();
        
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(3); // Completed maintenance
        LocalDateTime now = LocalDateTime.now();
        
        // Use reflection to access private method
        java.lang.reflect.Method determineMaintenanceStatus;
        try {
            determineMaintenanceStatus = MaintenanceStatusUpdateServiceImpl.class.getDeclaredMethod(
                "determineMaintenanceStatus", Maintenance.class, LocalDateTime.class);
            determineMaintenanceStatus.setAccessible(true);
            
            // Act
            MaintenanceStatus status = (MaintenanceStatus) determineMaintenanceStatus.invoke(
                service, maintenance, now);
            
            // Assert
            assertEquals(MaintenanceStatus.COMPLETED, status);
        } catch (Exception e) {
            fail("Exception occurred while testing: " + e.getMessage());
        }
    }
    
    @Test
    void testUpdateMaintenanceStatus() throws SQLException {
        MaintenanceStatusUpdateServiceImpl service = new MaintenanceStatusUpdateServiceImpl();
        
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        maintenance.setMaintenanceStatus(MaintenanceStatus.IN_PROGRESS);
        
        // Use reflection to access private method
        java.lang.reflect.Method updateMaintenanceStatus;
        try {
            updateMaintenanceStatus = MaintenanceStatusUpdateServiceImpl.class.getDeclaredMethod(
                "updateMaintenanceStatus", Maintenance.class, MaintenanceStatus.class);
            updateMaintenanceStatus.setAccessible(true);
            
            // Act
            updateMaintenanceStatus.invoke(service, maintenance, MaintenanceStatus.COMPLETED);
            
            // Assert - Verify database was updated
            Maintenance updated = maintenanceService.getMaintenanceById(1);
            assertEquals(MaintenanceStatus.COMPLETED, updated.getMaintenanceStatus());
        } catch (Exception e) {
            fail("Exception occurred while testing: " + e.getMessage());
        }
    }
}