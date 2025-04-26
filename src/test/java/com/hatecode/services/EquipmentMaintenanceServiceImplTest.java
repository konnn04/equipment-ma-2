package com.hatecode.services;

import com.hatecode.config.TestDatabaseConfig;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.Result;
import com.hatecode.services.impl.EquipmentMaintenanceServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseConfig.class)
public class EquipmentMaintenanceServiceImplTest {
    @BeforeEach
    void setupTestData() throws SQLException {
        // Reset database to clean state
        JdbcUtils.resetDatabase();
        // Khởi tạo dữ liệu mẫu
        String sql = """
                INSERT INTO Category (id, name, is_active)
                VALUES (1, 'Category 1', true),
                    (2, 'Category 2', true),
                    (3, 'Category 3', true);
                INSERT INTO Equipment (id, code, name, status, category_id, image_id, regular_maintenance_day, description)
                VALUES (1, 'EQ001', 'Equipment 1', 1, 1, 1, 30, 'Test Equipment 1'),
                    (2, 'EQ002', 'Equipment 2', 1, 1, 1, 60, 'Test Equipment 2'),
                    (3, 'EQ003', 'Equipment 3', 1, 2, 1, 45, 'Test Equipment 3');
                    
                INSERT INTO Maintenance (id, title, description, start_datetime, end_datetime)
                VALUES (1, 'Maintenance 1', 'Regular maintenance', '2025-01-01 09:00:00', '2025-01-01 17:00:00'),
                    (2, 'Maintenance 2', 'Emergency maintenance', '2025-02-01 08:00:00', '2025-02-02 18:00:00'),
                    (3, 'Maintenance 3', 'Special maintenance', '2025-03-01 10:00:00', '2025-03-05 15:00:00');
                    
                INSERT INTO "USER" (id, username, password, first_name, last_name, phone, email, role, avatar_id)
                VALUES (1, 'tech1', 'password1', 'John', 'Doe', '1234567890', 'john@example.com', 2, 1),
                    (2, 'tech2', 'password2', 'Jane', 'Smith', '0987654321', 'jane@example.com', 2, 1);
                    
                INSERT INTO equipment_maintenance (id, equipment_id, maintenance_id, technician_id, description, result, repair_price, inspection_date, is_active)
                VALUES (1, 1, 1, 1, 'Regular check for Equipment 1', 1, 50.0, '2025-01-01 10:00:00', true),
                    (2, 2, 1, 2, 'Regular check for Equipment 2', 2, 30.0, '2025-01-01 11:00:00', true),
                    (3, 3, 2, 1, 'Emergency repair', 1, 500.0, '2025-02-01 09:00:00', true),
                    (4, 1, 3, 2, 'Special maintenance', 0, 100.0, '2025-03-01 12:00:00', true),
                    (5, 2, 2, 1, 'Inactive record', 1, 75.0, '2025-02-02 14:00:00', false);
                -- Reset sequences to avoid ID conflicts
                ALTER TABLE equipment_maintenance ALTER COLUMN id RESTART WITH 10;
            """;

        try (Connection conn = JdbcUtils.getConn(); // Use getConn() instead of getConnection()
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        JdbcUtils.closeConnection();
    }

    /* =============================================================================
     * Test getEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testGetEquipmentMaintenance_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentMaintenance();
        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void testGetEquipmentMaintenanceByQuery_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        List<EquipmentMaintenance> result1 = equipmentMaintenanceService.getEquipmentMaintenance("Regular");
        List<EquipmentMaintenance> result2 = equipmentMaintenanceService.getEquipmentMaintenance("Emergency");
        List<EquipmentMaintenance> result3 = equipmentMaintenanceService.getEquipmentMaintenance("NonExistent");
        // Assert
        assertEquals(2, result1.size());
        assertEquals(1, result2.size());
        assertEquals(0, result3.size());
    }

    @Test
    void testGetEquipmentMaintenanceByMaintenance_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = new Maintenance();
        maintenance.setId(1); // Assuming this ID exists in the test data
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentMaintenance(maintenance);
        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getMaintenanceId());
        assertEquals(1, result.get(1).getMaintenanceId());
    }

    @Test
    void testGetEquipmentMaintenanceByMaintenance_NotFound() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        Maintenance maintenance = new Maintenance();
        maintenance.setId(999);
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentMaintenance(maintenance);
        // Assert
        assertEquals(0, result.size());
    }

    @Test
    void testGetEquipmentMaintenanceById_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        EquipmentMaintenance result = equipmentMaintenanceService.getEquipmentMaintenanceById(1);
        // Assert
        assertNotNull(result);
        assertEquals("Regular check for Equipment 1", result.getDescription());
        assertEquals(1, result.getEquipmentId());
        assertEquals(1, result.getMaintenanceId());
    }

    @Test
    void testGetEquipmentMaintenanceById_NotFound() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        EquipmentMaintenance result = equipmentMaintenanceService.getEquipmentMaintenanceById(999);
        // Assert
        assertNull(result);
    }

    @Test
    void testGetEquipmentByEquipmentMaintenance_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        Equipment result = equipmentMaintenanceService.getEquipmentByEquipmentMaintenance(1);
        // Assert
        assertNotNull(result);
        assertEquals("Equipment 1", result.getName());
        assertEquals("EQ001", result.getCode());
    }

    @Test
    void testGetEquipmentByEquipmentMaintenance_InvalidId() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.getEquipmentByEquipmentMaintenance(0);
        });
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL, e.getMessage());
    }

    /* =============================================================================
     * Test addEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testAddEquipmentMaintenance_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        EquipmentMaintenance em = new EquipmentMaintenance(
                2, 3, 1,
                "New maintenance for Equipment 2");        
        // Act
        boolean result = equipmentMaintenanceService.addEquipmentMaintenance(em);
        // Assert
        assertTrue(result);
        assertNotNull(em.getId()); // ID should be set after insert
        // Verify
        EquipmentMaintenance added = equipmentMaintenanceService.getEquipmentMaintenanceById(em.getId());
        assertEquals("New maintenance for Equipment 2", added.getDescription());
    }

    @Test
    void testAddEquipmentMaintenance_InvalidData() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        EquipmentMaintenance em = new EquipmentMaintenance(
                0, 3, 1,
                "Invalid equipment ID"
        );
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.addEquipmentMaintenance(em);
        });
        // Verify
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_NOT_INVALID, e.getMessage());
    }

    @Test
    void testAddEquipmentMaintenanceFull_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        EquipmentMaintenance em = new EquipmentMaintenance(
                2, 3, 1,
                "Full maintenance for Equipment 2",
                Result.NEED_REPAIR,
                100.0f,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );
        // Act
        boolean result = equipmentMaintenanceService.addEquipmentMaintenanceFull(em);
        // Assert
        assertTrue(result);
        assertNotNull(em.getId()); // ID should be set after insert
        // Verify
        EquipmentMaintenance added = equipmentMaintenanceService.getEquipmentMaintenanceById(em.getId());
        assertEquals("Full maintenance for Equipment 2", added.getDescription());
        assertEquals(Result.NEED_REPAIR, added.getResult());
        assertEquals(100.0f, added.getRepairPrice());
    }
    /* =============================================================================
     * Test updateEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testUpdateEquipmentMaintenance_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        EquipmentMaintenance em = equipmentMaintenanceService.getEquipmentMaintenanceById(1);
        em.setDescription("Updated description");
        em.setRepairPrice(75.0f);
        em.setResult(Result.NORMALLY);
        em.setInspectionDate(LocalDateTime.now());
        // Act
        boolean result = equipmentMaintenanceService.updateEquipmentMaintenance(em);
        // Assert
        assertTrue(result);
        // Verify
        EquipmentMaintenance updated = equipmentMaintenanceService.getEquipmentMaintenanceById(1);
        assertEquals("Updated description", updated.getDescription());
        assertEquals(75.0f, updated.getRepairPrice());
        assertEquals(Result.NORMALLY, updated.getResult());
        assertEquals(em.getInspectionDate().toString().substring(0, 13), updated.getInspectionDate().toString().substring(0, 13));
    }

    @Test
    void testUpdateEquipmentMaintenance_NotFound() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        EquipmentMaintenance em = new EquipmentMaintenance(
                999, 1, 1, 1,
                "Non-existent record",
                Result.NEED_REPAIR,
                50.0f,
                now,
                now,
                true
        );
        // Act
        boolean result = equipmentMaintenanceService.updateEquipmentMaintenance(em);
        // Assert
        assertFalse(result);
    }


    /* =============================================================================
     * Test deleteEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testDeleteEquipmentMaintenance_ById_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        boolean result = equipmentMaintenanceService.deleteEquipmentMaintenance(1);
        // Assert
        assertTrue(result);
        assertNull(equipmentMaintenanceService.getEquipmentMaintenanceById(1));
    }

    @Test
    void testDeleteEquipmentMaintenance_ById_NotFound() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        boolean result = equipmentMaintenanceService.deleteEquipmentMaintenance(999);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteEquipmentMaintenance_ByObject_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        EquipmentMaintenance em = equipmentMaintenanceService.getEquipmentMaintenanceById(1);
        
        // Act
        boolean result = equipmentMaintenanceService.deleteEquipmentMaintenance(em);
        
        // Assert
        assertTrue(result);
        assertNull(equipmentMaintenanceService.getEquipmentMaintenanceById(1));
    }

    @Test
    void testDeleteEquipmentMaintenance_ByObject_Null() {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.deleteEquipmentMaintenance((EquipmentMaintenance)null);
        });
        // Verify appropriate error message if defined
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL, e.getMessage());
    }

    /* =============================================================================
     * Test getEquipmentsMaintenanceByEMId
     * ========================================================================== */
    @Test
    void testGetEquipmentsMaintenanceByEMId_Success() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentsMaintenanceByEMId("Regular", 1);
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(em -> em.getDescription().contains("Regular")));
    }

    @Test
    void testGetEquipmentsMaintenanceByEMId_NotFound() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentsMaintenanceByEMId("NonExistent", 1);
        // Assert
        assertEquals(0, result.size());
    }

    /* =============================================================================
     * Additional test cases for getEquipmentMaintenance(String query)
     * ========================================================================== */
    @Test
    void testGetEquipmentMaintenanceByQuery_NullQuery() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentMaintenance((Maintenance) null);
        // Assert
        assertNotNull(result);
        // Should return all records (same as no query)
        assertEquals(4, result.size());
    }

    /* =============================================================================
     * Additional test cases for getEquipmentMaintenance(Maintenance m)
     * ========================================================================== */
    @Test
    void testGetEquipmentMaintenanceByMaintenance_NullMaintenance() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentMaintenance((Maintenance)null);
        // Assert
        assertNotNull(result);
        // Should return all records
        assertEquals(4, result.size());
    }

    /* =============================================================================
     * Additional test cases for addEquipmentMaintenanceFull
     * ========================================================================== */
    @Test
    void testAddEquipmentMaintenanceFull_InvalidData() {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        // Arrange
        EquipmentMaintenance em = new EquipmentMaintenance();
        em.setEquipmentId(0); // Invalid ID
        em.setMaintenanceId(3);
        em.setTechnicianId(1);
        em.setDescription("Invalid data test");
        em.setResult(Result.NEED_REPAIR);
        em.setRepairPrice(100.0f);
        em.setInspectionDate(LocalDateTime.now());

        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.addEquipmentMaintenanceFull(em);
        });
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_NOT_INVALID, e.getMessage());
    }

    /* =============================================================================
     * Additional test cases for updateEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testUpdateEquipmentMaintenance_NullObject() {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.updateEquipmentMaintenance(null);
        });
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL, e.getMessage());
    }

    @Test
    void testUpdateEquipmentMaintenance_InvalidData() {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        
        // Arrange
        EquipmentMaintenance em = new EquipmentMaintenance(
            1, 0, 1, 1, // Invalid equipment_id (0)
            "Invalid equipment ID",
            Result.NEED_REPAIR,
            50.0f,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.updateEquipmentMaintenance(em);
        });
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_NOT_INVALID, e.getMessage());
    }

    /* =============================================================================
     * Additional test cases for deleteEquipmentMaintenance(int id)
     * ========================================================================== */
    @Test
    void testDeleteEquipmentMaintenance_InvalidId() {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        
        // Act & Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            equipmentMaintenanceService.deleteEquipmentMaintenance(-1);
        });
        assertEquals(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL, e.getMessage());
    }

    /* =============================================================================
     * Additional test cases for getEquipmentsMaintenanceByEMId
     * ========================================================================== */
    @Test
    void testGetEquipmentsMaintenanceByEMId_NullKeyword() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentsMaintenanceByEMId(null, 1);
        
        // Assert
        assertNotNull(result);
        // Should return all equipment maintenances for maintenance ID 1
        assertEquals(2, result.size());
    }

    @Test
    void testGetEquipmentsMaintenanceByEMId_InvalidMaintenanceId() throws SQLException {
        EquipmentMaintenanceService equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentsMaintenanceByEMId("Regular", -1);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size()); // No records found for invalid ID
    }
}