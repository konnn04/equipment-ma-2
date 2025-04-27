package com.hatecode.services;

import com.hatecode.config.TestDatabaseConfig;
import com.hatecode.pojo.*;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseConfig.class)
public class MaintenanceServiceImplTest {
// Reset database to clean state
    @BeforeEach
    void setupTestData() throws SQLException {
        JdbcUtils.resetDatabase();
        // Khởi tạo dữ liệu mẫu
        /* Với 6 bản ghi trong bảng Maintenance
         * 1. Maintenance #1 - Đã hoàn thành
         * 2. Maintenance #2 - Đã hoàn thành
         * 3. Maintenance #3 - Đang thực hiện
         * 4. Maintenance #4 - Đang thực hiện
         * 5. Maintenance #5 - Đang chờ
         * 6. Maintenance #6 - Đang chờ
         */
        String sql = """
                INSERT INTO `user` (first_name, last_name, username, password, email, phone, role, avatar_id)
                VALUES ('Nguyen', 'Van A', 'technician0', '123456', 'a@gmail.com', '0123456789', 2, 1),
                       ('Nguyen', 'Van B', 'technician1', '123456', 'b@gmail.com', '0123456788', 2, 1),
                       ('Nguyen', 'Van C', 'technician2', '123456', 'c@gmail.com', '0123456787', 2, 1),
                       ('Nguyen', 'Van D', 'technician3', '123456', 'd@gmail.com', '0123456786', 2, 1),
                       ('Nguyen', 'Van E', 'technician4', '123456', 'e@gmail.com', '0123456785', 2, 1),
                       ('Nguyen', 'Van F', 'technician5', '123456', 'f@gmail.com', '0123456784', 2, 1);
                INSERT INTO `category` (name)
                VALUES ('Category 1'),
                       ('Category 2'),
                       ('Category 3'),
                       ('Category 4'),
                       ('Category 5');
                INSERT INTO `equipment` (code, name, status, category_id, image_id, regular_maintenance_day, description)
                VALUES ('C1E1', 'E1', 1, 1, 1, 30, 'Description 1'),
                       ('C1E2', 'E2', 1, 1, 1, 30, 'Description 2'),
                       ('C1E3', 'E3', 1, 1, 1, 30, 'Description 3'),
                       ('C2E1', 'E4', 1, 2, 1, 30, 'Description 4'),
                       ('C2E2', 'E5', 1, 2, 1, 30, 'Description 5'),
                       ('C3E1', 'E6', 1, 3, 1, 30, 'Description 6');
                INSERT INTO `maintenance` (title, description, start_datetime, end_datetime, status)
                VALUES ('Maintenance #1 - Regular', 'Kiểm tra tất cả thiết bị điện tử', '2024-10-01 09:00:00', '2024-10-08 17:00:00', 1),
                       ('Maintenance #2 - Emergency', 'Bảo trì toàn diện cho máy móc', '2024-11-01 09:00:0', '2024-11-08 17:00:00', 1),
                      ('Maintenance #3 - Regular', 'Bảo trì định kỳ cho máy móc', '2025-04-01 08:00:00', '2025-09-20 18:00:00', 1),
                      ('Maintenance #4 - Emergency', 'Bảo trì định kỳ cho máy móc', '2025-04-01 08:00:00', '2025-09-20 18:00:00', 1),
                      ('Maintenance #5 - Regular', 'Bảo trì định kỳ cho máy móc', '2025-08-01 08:00:00', '2025-08-03 18:00:00', 1),
                      ('Maintenance #6 - Regular', 'Bảo trì định kỳ cho máy móc', '2026-08-05 08:00:00', '2026-09-20 18:00:00', 1);
                INSERT INTO `equipment_maintenance` (equipment_id, maintenance_id, technician_id, description, equipment_name, equipment_code)
                VALUES (1, 1, 1, 'Bảo trì thiết bị điện tử', 'E1', 'C1E1'),
                       (2, 2, 2, 'Bảo trì thiết bị điện tử', 'E2', 'C1E2'),
                       (3, 3, 3, 'Bảo trì thiết bị điện tử', 'E3', 'C1E3'),
                       (4, 4, 4, 'Bảo trì thiết bị điện tử', 'E4', 'C2E1'),
                       (5, 5, 5, 'Bảo trì thiết bị điện tử', 'E5', 'C2E2'),
                       (6, 6, 6, 'Bảo trì thiết bị điện tử', 'E6', 'C3E1');
                """;

        try (Connection conn = JdbcUtils.getConn(); // Use getConn() instead of getConnection()
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        // Close the test connection
        JdbcUtils.closeConnection();
    }

    /* =============================================================================
     * Test getMaintenances
     * ========================================================================== */
    @Test
    void testGetMaintenances_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        List<Maintenance> maintenances = maintenanceService.getMaintenances();
        // Assert
        assertEquals(6, maintenances.size());
        assertEquals("Maintenance #1 - Regular", maintenances.getFirst().getTitle());
    }

    @Test
    void testGetMaintenancesByQuery_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        List<Maintenance> maintenances = maintenanceService.getMaintenances("Emergency");
        // Assert
        assertEquals(2, maintenances.size());
    }

    @Test
    void testGetMaintenanceByQueryAndFilter_ByCompleted() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        List<Maintenance> maintenances1 = maintenanceService.getMaintenances("Maintenance", MaintenanceStatus.COMPLETED);
        List<Maintenance> maintenances2 = maintenanceService.getMaintenances("Emergency", MaintenanceStatus.COMPLETED);
        // Assert
        assertEquals(2, maintenances1.size());
        assertEquals(1, maintenances2.size());
        assertEquals("Maintenance #1 - Regular", maintenances1.getFirst().getTitle());
        assertEquals("Maintenance #2 - Emergency", maintenances2.getFirst().getTitle());
    }

    @Test
    void testGetMaintenanceByQueryAndFilter_ByInProgress() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        List<Maintenance> maintenances1 = maintenanceService.getMaintenances("Maintenance", MaintenanceStatus.IN_PROGRESS);
        List<Maintenance> maintenances2 = maintenanceService.getMaintenances("Emergency", MaintenanceStatus.IN_PROGRESS);
        // Assert
        assertEquals(2, maintenances1.size());
        assertEquals(1, maintenances2.size());
        assertEquals("Maintenance #3 - Regular", maintenances1.getFirst().getTitle());
        assertEquals("Maintenance #4 - Emergency", maintenances2.getFirst().getTitle());
    }

    @Test
    void testGetMaintenanceByQueryAndFilter_ByPending() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        List<Maintenance> maintenances1 = maintenanceService.getMaintenances("Maintenance", MaintenanceStatus.PENDING);
        List<Maintenance> maintenances2 = maintenanceService.getMaintenances("Emergency", MaintenanceStatus.PENDING);
        // Assert
        assertEquals(2, maintenances1.size());
        assertEquals(0, maintenances2.size());
        assertEquals("Maintenance #5 - Regular", maintenances1.getFirst().getTitle());
    }

    @Test
    void testGetMaintenanceByQueryAndFilter_ByInvalidStatus() {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.getMaintenances("Maintenance", MaintenanceStatus.valueOf("INVALID_STATUS"));
        });
    }

    /* =============================================================================
     * Test getMaintenanceById
     * ========================================================================== */
    @Test
    void testGetMaintenanceById_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        // Assert
        assertNotNull(maintenance);
        assertEquals("Maintenance #1 - Regular", maintenance.getTitle());
    }

    @Test
    void testGetMaintenanceById_NotFound() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        Maintenance maintenance = maintenanceService.getMaintenanceById(999);
        // Assert
        assertNull(maintenance);
    }
    /* =============================================================================
     * Test addMaintenance
     * ========================================================================== */
    @Test
    void testAddMaintenance_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = new Maintenance(
                "Maintenance #7 - Regular",
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 12, 01, 8, 0),
                LocalDateTime.of(2025, 12, 20, 18, 0),
                MaintenanceStatus.PENDING
        );
        // Act
        boolean result = maintenanceService.addMaintenance(maintenance);
        // Assert
        assertTrue(result, "Failed to add maintenance");
    }

    @Test
    void testAddMaintenance_InvalidStartDate() {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = new Maintenance(
                "Maintenance #8 - Regular",
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 9, 20, 18, 0),
                LocalDateTime.of(2025, 8, 01, 8, 0),
                MaintenanceStatus.PENDING
        );
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.addMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_START_DATE_INVALID, e.getMessage(), "Start date must be before end date");
    }

    @Test
    void testAddMaintenance_EmptyTitle() {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = new Maintenance(
                "",
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 8, 01, 8, 0),
                LocalDateTime.of(2025, 9, 20, 18, 0),
                MaintenanceStatus.PENDING
        );
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.addMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_NAME_EMPTY, e.getMessage(), "Title cannot be empty");
    }

    /* =============================================================================
     * Test updateMaintenance
     * ========================================================================== */
    @Test
    void testUpdateMaintenance_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange - use maintenance #6 which has a future end date
        String newTitle = "Maintenance #6 - Updated";
        Maintenance maintenance = maintenanceService.getMaintenanceById(6);
        maintenance.setTitle(newTitle);
        // Act
        boolean result = maintenanceService.updateMaintenance(maintenance);
        // Assert
        assertTrue(result, "Failed to update maintenance");
        assertEquals(newTitle, maintenanceService.getMaintenanceById(6).getTitle(), "Message: Title not updated");
    }

    @Test
    void testUpdateMaintenance_InvalidStartDate() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        maintenance.setStartDateTime(LocalDateTime.of(2025, 9, 20, 18, 0));
        maintenance.setEndDateTime(LocalDateTime.of(2025, 8, 1, 8, 0));
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.updateMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_START_DATE_INVALID, e.getMessage(), "Start date must be before end date");
    }

    @Test
    void testUpdateMaintenance_EmptyTitle() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        maintenance.setTitle("");
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.updateMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_NAME_EMPTY, e.getMessage(), "Title cannot be empty");
    }

    @Test
    void testUpdateMaintenance_NullId() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = new Maintenance(
                null,
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 8, 1, 8, 0),
                LocalDateTime.of(2025, 9, 20, 18, 0),
                MaintenanceStatus.PENDING
        );
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.updateMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_ID_NULL, e.getMessage(), "Title already exists");
    }

    @Test
    void testUpdateMaintenance_EndDateInPast() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange - use maintenance #1 which has end date in the past
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        maintenance.setTitle("Updated Title");
        // Act & Assert
//        Exception e = assertThrows(IllegalArgumentException.class, () -> {
//            maintenanceService.updateMaintenance(maintenance);
//        });
//        assertEquals(ExceptionMessage.MAINTENANCE_CANNOT_UPDATE_COMPLETED, e.getMessage());
        boolean r = maintenanceService.updateMaintenance(maintenance);
        assertTrue(r, "Failed to update maintenance");
    }

    /* =============================================================================
     * Test deleteMaintenance
     * ========================================================================== */
    @Test
    void testDeleteMaintenance_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        // Act
        boolean result = maintenanceService.deleteMaintenance(maintenance);
        // Assert
        assertTrue(result, "Failed to delete maintenance");
        assertNull(maintenanceService.getMaintenanceById(1), "Maintenance should be deleted");
    }

    @Test
    void testDeleteMaintenanceById_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        boolean result = maintenanceService.deleteMaintenanceById(1);
        // Assert
        assertTrue(result, "Failed to delete maintenance");
        assertNull(maintenanceService.getMaintenanceById(1), "Maintenance should be deleted");
    }

    @Test
    void testDeleteMaintenanceById_NotFound() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act
        boolean result = maintenanceService.deleteMaintenanceById(999);
        // Assert
        assertFalse(result, "Maintenance should not be found");
    }

    /* =============================================================================
     * Test getEquipmentMaintenancesByMaintenance
     * ========================================================================== */
    @Test
    void testGetEquipmentMaintenancesByMaintenance_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Setup test data first - need to add equipment maintenance records linked to maintenance ID 1
        
        // Act
        List<EquipmentMaintenance> result = maintenanceService.getEquipmentMaintenancesByMaintenance(1);
        
        // Assert
        assertNotNull(result);
        // More detailed assertions based on test data
    }

    @Test
    void testGetEquipmentMaintenancesByMaintenance_InvalidId() {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.getEquipmentMaintenancesByMaintenance(-1);
        });
    }

    /* =============================================================================
     * Test addMaintenance with equipment
     * ========================================================================== */
    @Test
    void testAddMaintenanceWithEquipment_Success() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        
        // Arrange
        Maintenance maintenance = new Maintenance(
            "Maintenance with Equipment",
            "Testing equipment maintenance",
            LocalDateTime.now().plusMonths(10),
            LocalDateTime.now().plusMonths(11),
            MaintenanceStatus.PENDING
        );

        List<EquipmentMaintenance> equipments = new ArrayList<>();
        EquipmentMaintenance em = new EquipmentMaintenance();
        em.setEquipmentId(1); // Existing equipment ID
        em.setTechnicianId(1); // Existing technician ID
        em.setDescription("Test equipment maintenance");
        em.setEquipmentName("Equipment 1");
        em.setEquipmentCode("EQ001");
        equipments.add(em);
        
        // Act
        boolean result = maintenanceService.addMaintenance(maintenance, equipments);
        
        // Assert
        assertTrue(result);
        assertNotEquals(0, maintenance.getId()); // ID should be set after successful add
    }

    /* =============================================================================
     * Test past start date validation
     * ========================================================================== */
    @Test
    void testAddMaintenance_PastStartDate() {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        
        // Arrange - create maintenance with start date in the past
        Maintenance maintenance = new Maintenance(
            "Past Maintenance",
            "Should fail validation",
            LocalDateTime.now().minusDays(1), // Past date
            LocalDateTime.now().plusDays(1),
            MaintenanceStatus.PENDING
        );
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.addMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_START_DATE_IN_FUTURE, e.getMessage());
    }

    /* =============================================================================
     * Test start date modification restrictions
     * ========================================================================== */
    @Test
    void testUpdateMaintenance_ChangeStartDateForOngoingMaintenance() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        
        // First add a maintenance with start date in the past (need to bypass validation)
        // This requires direct SQL insertion or mocking
        
        // Then attempt to modify its start date
        Maintenance maintenance = maintenanceService.getMaintenanceById(3); // ID 3 is ongoing
        maintenance.setStartDateTime(LocalDateTime.now().plusDays(1));
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.updateMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_CANNOT_CHANGE_START_DATE, e.getMessage());
    }

    /* =============================================================================
     * Test overlapping maintenance detection
     * ========================================================================== */
    @Test
    void testAddMaintenance_OverlappingSchedule() {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        
        // Arrange - create maintenance with schedule that overlaps existing one
        Maintenance maintenance = new Maintenance(
            "Overlapping Maintenance",
            "Should detect overlap",
            LocalDateTime.of(2025, 8, 5, 8, 0), // Overlaps with #5 and #6
            LocalDateTime.of(2025, 8, 15, 18, 0),
            MaintenanceStatus.PENDING
        );
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.addMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_OVERLAP, e.getMessage());
    }

    @Test
    void testUpdateMaintenance_InPast() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        
        // Arrange - get existing maintenance and update to overlap with another
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        maintenance.setStartDateTime(LocalDateTime.of(2026, 8, 5, 8, 0));
        maintenance.setEndDateTime(LocalDateTime.of(2026, 8, 15, 18, 0));
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.updateMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_CANNOT_CHANGE_START_DATE, e.getMessage());
    }

    /* =============================================================================
     * Test equipment-specific maintenance conflicts
     * ========================================================================== */
    @Test
    void testAddMaintenanceWithEquipment_OverlappingEquipment() throws SQLException {
        MaintenanceService maintenanceService = new MaintenanceServiceImpl();
        
        // Need to first ensure there's equipment assigned to existing maintenance
        
        // Then try to add new maintenance with same equipment during overlapping time
        Maintenance maintenance = new Maintenance(
            "Maintenance with Conflicting Equipment",
            "Should detect equipment conflict",
            LocalDateTime.of(2025, 8, 10, 8, 0),
            LocalDateTime.of(2025, 8, 20, 18, 0),
            MaintenanceStatus.PENDING
        );
        
        List<EquipmentMaintenance> equipments = new ArrayList<>();
        EquipmentMaintenance em = new EquipmentMaintenance();
        em.setEquipmentId(1); // Same equipment that's already scheduled
        em.setTechnicianId(1);
        equipments.add(em);
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.addMaintenance(maintenance, equipments);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_OVERLAP, e.getMessage());
    }
}
