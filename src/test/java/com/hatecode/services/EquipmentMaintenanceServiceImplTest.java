package com.hatecode.services;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.Result;
import com.hatecode.services.impl.EquipmentMaintenanceServiceImpl;
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

public class EquipmentMaintenanceServiceImplTest {
    private static Connection conn;
    private EquipmentMaintenanceService equipmentMaintenanceService;

    @BeforeEach
    void setupTestData() throws SQLException {
        conn = TestDBUtils.createIsolatedConnection();
        equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl(conn);
        
        // Tạo bảng và thêm dữ liệu mẫu
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
                    
                INSERT INTO equipment_maintenance (id, equipment_id, maintenance_id, technician_id, description, result, repair_name, repair_price, inspection_date, is_active)
                VALUES (1, 1, 1, 1, 'Regular check for Equipment 1', 1, 'Oil change', 50.0, '2025-01-01 10:00:00', true),
                    (2, 2, 1, 2, 'Regular check for Equipment 2', 2, 'Filter replacement', 30.0, '2025-01-01 11:00:00', true),
                    (3, 3, 2, 1, 'Emergency repair', 1, 'Motor replacement', 500.0, '2025-02-01 09:00:00', true),
                    (4, 1, 3, 2, 'Special maintenance', 0, 'Full inspection', 100.0, '2025-03-01 12:00:00', true),
                    (5, 2, 2, 1, 'Inactive record', 1, 'Test repair', 75.0, '2025-02-02 14:00:00', false);
                -- Reset sequences to avoid ID conflicts
                ALTER TABLE equipment_maintenance ALTER COLUMN id RESTART WITH 10;
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
     * Test getEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testGetEquipmentMaintenance_Success() throws SQLException {
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentMaintenance();
        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void testGetEquipmentMaintenanceByQuery_Success() throws SQLException {
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
        // Act
        EquipmentMaintenance result = equipmentMaintenanceService.getEquipmentMaintenanceById(999);
        // Assert
        assertNull(result);
    }

    @Test
    void testGetEquipmentByEquipmentMaintenance_Success() throws SQLException {
        // Act
        Equipment result = equipmentMaintenanceService.getEquipmentByEquipmentMaintenance(1);
        // Assert
        assertNotNull(result);
        assertEquals("Equipment 1", result.getName());
        assertEquals("EQ001", result.getCode());
    }

    @Test
    void testGetEquipmentByEquipmentMaintenance_InvalidId() throws SQLException {
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
        // Arrange
        EquipmentMaintenance em = new EquipmentMaintenance(
                2, 3, 1,
                "Full maintenance for Equipment 2",
                Result.NEED_REPAIR,
                "Software update",
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
        assertEquals("Software update", added.getRepairName());
        assertEquals(100.0f, added.getRepairPrice());
    }
    /* =============================================================================
     * Test updateEquipmentMaintenance
     * ========================================================================== */
    @Test
    void testUpdateEquipmentMaintenance_Success() throws SQLException {
        // Arrange
        EquipmentMaintenance em = equipmentMaintenanceService.getEquipmentMaintenanceById(1);
        em.setDescription("Updated description");
        em.setRepairName("Updated repair");
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
        assertEquals("Updated repair", updated.getRepairName());
        assertEquals(75.0f, updated.getRepairPrice());
        assertEquals(Result.NORMALLY, updated.getResult());
        assertEquals(em.getInspectionDate().toString().substring(0, 13), updated.getInspectionDate().toString().substring(0, 13));
    }

    @Test
    void testUpdateEquipmentMaintenance_NotFound() throws SQLException {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        EquipmentMaintenance em = new EquipmentMaintenance(
                999, 1, 1, 1,
                "Non-existent record",
                Result.NEED_REPAIR,
                "Test",
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
        // Act
        boolean result = equipmentMaintenanceService.deleteEquipmentMaintenance(1);
        // Assert
        assertTrue(result);
        assertNull(equipmentMaintenanceService.getEquipmentMaintenanceById(1));
    }

    @Test
    void testDeleteEquipmentMaintenance_ById_NotFound() throws SQLException {
        // Act
        boolean result = equipmentMaintenanceService.deleteEquipmentMaintenance(999);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteEquipmentMaintenance_ByObject_Success() throws SQLException {
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
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentsMaintenanceByEMId("Regular", 1);
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(em -> em.getDescription().contains("Regular") || em.getRepairName().contains("Regular")));
    }

    @Test
    void testGetEquipmentsMaintenanceByEMId_NotFound() throws SQLException {
        // Act
        List<EquipmentMaintenance> result = equipmentMaintenanceService.getEquipmentsMaintenanceByEMId("NonExistent", 1);
        // Assert
        assertEquals(0, result.size());
    }
}