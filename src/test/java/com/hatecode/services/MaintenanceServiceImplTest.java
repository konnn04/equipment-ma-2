package com.hatecode.services;

import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.MaintenanceStatus;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.TestDBUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceServiceImplTest {
    private static Connection conn;
    private MaintenanceService maintenanceService;

    @BeforeEach
    void setupTestData() throws SQLException {
        conn = TestDBUtils.createIsolatedConnection();
        maintenanceService = new MaintenanceServiceImpl(conn);
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
                INSERT INTO Maintenance (title, description, start_datetime, end_datetime)
                VALUES ('Maintenance #1 - Regular', 'Kiểm tra tất cả thiết bị điện tử', '2024-10-01 09:00:00', '2024-10-01 17:00:00'),
                       ('Maintenance #2 - Emergency', 'Bảo trì toàn diện cho máy móc', '2024-10-01 09:00:0', '2024-10-01 17:00:00'),
                      ('Maintenance #3 - Regular', 'Bảo trì định kỳ cho máy móc', '2025-04-01 08:00:00', '2025-09-20 18:00:00'),
                      ('Maintenance #4 - Emergency', 'Bảo trì định kỳ cho máy móc', '2025-04-01 08:00:00', '2025-09-20 18:00:00'),
                      ('Maintenance #5 - Regular', 'Bảo trì định kỳ cho máy móc', '2025-08-01 08:00:00', '2025-09-20 18:00:00'),
                      ('Maintenance #6 - Regular', 'Bảo trì định kỳ cho máy móc', '2025-08-01 08:00:00', '2025-09-20 18:00:00');
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

    /* =============================================================================
     * Test getMaintenances
     * ========================================================================== */
    @Test
    void testGetMaintenances_Success() throws SQLException {
        // Act
        List<Maintenance> maintenances = maintenanceService.getMaintenances();
        // Assert
        assertEquals(6, maintenances.size());
        assertEquals("Maintenance #1 - Regular", maintenances.getFirst().getTitle());
    }

    @Test
    void testGetMaintenancesByQuery_Success() throws SQLException {
        // Act
        List<Maintenance> maintenances = maintenanceService.getMaintenances("Emergency");
        // Assert
        assertEquals(2, maintenances.size());
    }

    @Test
    void testGetMaintenanceByQueryAndFilter_ByCompleted() throws SQLException {
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
        // Act
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        // Assert
        assertNotNull(maintenance);
        assertEquals("Maintenance #1 - Regular", maintenance.getTitle());
    }

    @Test
    void testGetMaintenanceById_NotFound() throws SQLException {
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
        // Arrange
        Maintenance maintenance = new Maintenance(
                "Maintenance #7 - Regular",
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 8, 01, 8, 0),
                LocalDateTime.of(2025, 9, 20, 18, 0)
        );
        // Act
        boolean result = maintenanceService.addMaintenance(maintenance);
        // Assert
        assertTrue(result, "Failed to add maintenance");
    }

    @Test
    void testAddMaintenance_InvalidStartDate() {
        // Arrange
        Maintenance maintenance = new Maintenance(
                "Maintenance #8 - Regular",
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 9, 20, 18, 0),
                LocalDateTime.of(2025, 8, 01, 8, 0)
        );
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.addMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_START_DATE_INVALID, e.getMessage(), "Start date must be before end date");
    }

    @Test
    void testAddMaintenance_EmptyTitle() {
        // Arrange
        Maintenance maintenance = new Maintenance(
                "",
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 8, 01, 8, 0),
                LocalDateTime.of(2025, 9, 20, 18, 0)
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
        // Arrange
        String newTitle = "Maintenance #1 - Updated";
        Maintenance maintenance = maintenanceService.getMaintenanceById(1);
        maintenance.setTitle(newTitle);
        // Act
        boolean result = maintenanceService.updateMaintenance(maintenance);
        // Assert
        assertTrue(result, "Failed to update maintenance");
        assertEquals(newTitle, maintenanceService.getMaintenanceById(1).getTitle(), "Message: Title not updated");
    }

    @Test
    void testUpdateMaintenance_InvalidStartDate() throws SQLException {
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
        // Arrange
        Maintenance maintenance = new Maintenance(
                null,
                "Bảo trì định kỳ cho máy móc",
                LocalDateTime.of(2025, 8, 1, 8, 0),
                LocalDateTime.of(2025, 9, 20, 18, 0)
        );
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            maintenanceService.updateMaintenance(maintenance);
        });
        assertEquals(ExceptionMessage.MAINTENANCE_ID_NULL, e.getMessage(), "Title already exists");
    }

    /* =============================================================================
     * Test deleteMaintenance
     * ========================================================================== */
    @Test
    void testDeleteMaintenance_Success() throws SQLException {
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
        // Act
        boolean result = maintenanceService.deleteMaintenanceById(1);
        // Assert
        assertTrue(result, "Failed to delete maintenance");
        assertNull(maintenanceService.getMaintenanceById(1), "Maintenance should be deleted");
    }

    @Test
    void testDeleteMaintenanceById_NotFound() throws SQLException {
        // Act
        boolean result = maintenanceService.deleteMaintenanceById(999);
        // Assert
        assertFalse(result, "Maintenance should not be found");
    }
}
