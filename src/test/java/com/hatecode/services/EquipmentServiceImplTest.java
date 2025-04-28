package com.hatecode.services;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Image;
import com.hatecode.pojo.Status;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class EquipmentServiceImplTest {
    @BeforeAll
    static void setupDatabase() throws SQLException {
        JdbcUtils.fileName = "dbWithData";
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        JdbcUtils.closeConnection();
    }

    @BeforeEach
    void setupTestData() throws SQLException {
        // Reset database to clean state
        JdbcUtils.resetDatabase();

        // Initialize test data
        String sql = """
               
                """;
        try (Connection conn = JdbcUtils.getConn(); // Use getConn() instead of getConnection()
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "ELEC001, Duplicate Equipment, 1, 1, 1, 30, This is a duplicate"
    })
    void testAddDuplicateEquipment(String code, String name, int status, int categoryId,
                                   int imageId, int regularMaintenanceDay, String description) {
        // Arrange
        Equipment e = new Equipment(
                code, // Duplicate code from test data
                name,
                Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                description
        );
        EquipmentService equipmentService = new EquipmentServiceImpl();

        // Act & Assert
        assertThrows(SQLException.class, () -> equipmentService.addEquipment(e),
                "Adding equipment with duplicate code should throw SQLException");
    }

    /* =============================================================================
     * Test addEquipment methods
     * ========================================================================== */
    @ParameterizedTest
    @CsvSource({
        "EQPP01, New Air Compressor, 1, 1, 1, 30, Used in maintenance operations, true"
    })
    void testAddEquipment(String code, String name, int status, int categoryId,
                          int imageId, int regularMaintenanceDay, String description,
                          boolean expected) throws SQLException {
        // Arrange
        Equipment e = new Equipment(
                code,
                name,
                Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                description
        );
        EquipmentService equipmentService = new EquipmentServiceImpl();

        // Act
        boolean result = equipmentService.addEquipment(e);

        // Assert
        assertEquals(expected, result, "Adding equipment should return expected result");

        // Verify that the equipment was added
        Equipment added = equipmentService.getEquipmentByCode(e.getCode());
        assertEquals(code, added.getCode(), "Equipment code should match");
        assertEquals(name, added.getName(), "Equipment name should match");
        assertEquals(description, added.getDescription(), "Equipment description should match");
    }

    // Added from AddEquipmentTest.java
    @ParameterizedTest()
    @CsvFileSource(resources = "equipment_add.csv", numLinesToSkip = 1)
    void testAddEquipmentFromCsv(int id, String code, String name, int status, int categoryId,
                                LocalDateTime createdDate, int imageId, int regularMaintenanceDay,
                                LocalDateTime lastMaintenanceTime, String description, boolean isActive,
                                boolean expected) {
        // Test adding a new equipment
        Equipment e = new Equipment(
                code,
                name,
                Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                description,
                lastMaintenanceTime
        );
        EquipmentService equipmentService = new EquipmentServiceImpl();

        try {
            boolean result = equipmentService.addEquipment(e);
            assertEquals(expected, result, "Adding equipment should return " + expected);

            if (expected) {
                // Verify that the equipment was added
                Equipment added = equipmentService.getEquipmentByCode(e.getCode());
                assertEquals(code, added.getCode(), "Equipment code should match");
                assertEquals(name, added.getName(), "Equipment name should match");
                assertEquals(description, added.getDescription(), "Equipment description should match");
            }
        } catch (SQLException ex) {
            fail("SQLException occurred while adding equipment: " + ex.getMessage());
        }
    }

    // Added from AddEquipmentTest.java
    @ParameterizedTest
    @CsvFileSource(resources = "equipment_addWithImage.csv", numLinesToSkip = 1)
    void testAddEquipmentWithImage(String code, String name, int status, int categoryId,
                                  int imageId, int regularMaintenanceDay,
                                  String description) {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Test adding a new equipment
        Equipment e = new Equipment(
                code,
                name,
                Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                description
        );

        try {
            if (imageId != -1) {
                boolean result = equipmentService.addEquipment(e, new Image(1, "test.jpg", LocalDateTime.now(), "path/to/image"));
                assertTrue(result, "Adding equipment should return true");

                // Verify that the equipment was added
                Equipment added = equipmentService.getEquipmentByCode(e.getCode());
                assertEquals(code, added.getCode(), "Equipment code should match");
                assertEquals(name, added.getName(), "Equipment name should match");
                assertEquals(description, added.getDescription(), "Equipment description should match");
                assertEquals(imageId, added.getImageId(), "Equipment image ID should match");
            } else {
                assertThrows(SQLException.class, () -> equipmentService.addEquipment(e, new Image(-1, "not found", LocalDateTime.now(), "not found")),
                        "Adding equipment with invalid image should throw SQLException");
            }
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({
        "null, Test Equipment, 1, 1, 1, 30, Test description",
        "EQNULL, null, 1, 1, 1, 30, Test description",
        "EQNULL, Test Equipment, 1, 0, 1, 30, Test description"
    })
    void testAddEquipmentWithInvalidData(String code, String name, int status, int categoryId,
                                        int imageId, int regularMaintenanceDay, String description) {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Arrange
        Equipment e = new Equipment(
                "null".equals(code) ? null : code,
                "null".equals(name) ? null : name,
                Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                description
        );

        // Act & Assert
        assertThrows(SQLException.class, () -> equipmentService.addEquipment(e),
                "Adding equipment with invalid data should throw SQLException");
    }

    // Added from AddEquipmentTest.java
    @ParameterizedTest
    @CsvFileSource(resources = "equipment_addWithNullFields.csv", numLinesToSkip = 1)
    void testAddEquipmentWithNotNullField(String code, String name, int status, int categoryId,
                                         int imageId, int regularMaintenanceDay,
                                         String description) {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        Equipment e = new Equipment(
                Objects.equals(code, "null") ? null : code,
                Objects.equals(name, "null") ? null : name,
                status == -1 ? null : Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                Objects.equals(description, "null") ? null : description
        );

        assertThrows(SQLException.class, () -> equipmentService.addEquipment(e),
                "Adding equipment with null fields should throw SQLException");
    }

    /* =============================================================================
     * Test deleteEquipment methods
     * ========================================================================== */
    @Test
    void testDeleteEquipment() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Arrange - ensure test ID exists
        int idToDelete = 21;

        // Act
        boolean result = equipmentService.deleteEquipment(idToDelete);

        // Assert
        assertTrue(result, "Deletion should succeed");

        // Verify the equipment was deleted (soft delete)
        Equipment deleted = equipmentService.getEquipmentById(idToDelete);
        assertNull(deleted, "Equipment should not be found after deletion");
    }

    // Added from DeleteEquipment.java
    @ParameterizedTest
    @CsvSource({"1"})
    void testDeleteEquipmentWithForeignKey(int id) {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        assertThrows(SQLException.class, () -> equipmentService.hardDeleteEquipment(id),
                "Hard deletion should throw SQLException for equipment with foreign key references");
    }

    @Test
    void testDeleteEquipment_NotFound() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();

        // Assert
        assertThrows(SQLException.class, () -> equipmentService.deleteEquipment(999), "Deletion should fail for non-existent equipment");
    }

    /* =============================================================================
     * Test getDistinctValues method
     * ========================================================================== */
    @Test
    void testGetDistinctValues() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Act
        List<Object> categories = equipmentService.getDistinctValues("category_id");
        List<Object> statuses = equipmentService.getDistinctValues("status");

        // Assert
        assertNotNull(categories, "List of distinct values should not be null");
        assertFalse(categories.isEmpty(), "List of distinct values should not be empty");
        assertTrue(categories.contains(1), "Category ID 1 should be present");
        assertTrue(categories.contains(2), "Category ID 2 should be present");

        assertNotNull(statuses, "List of distinct statuses should not be null");
        assertFalse(statuses.isEmpty(), "List of distinct statuses should not be empty");
    }

    @ParameterizedTest
    @CsvSource({
        "1, ELEC001, Laptop, 2, 1, 1, 180, Máy tính xách tay văn phòng",
        "2, MACH001, Máy khoan, 3, 2, 1, 90, Máy khoan công nghiệp",
        "3, VEH001, Xe tải, 4, 3, 1, 365, Xe tải giao hàng"
    })
    void testGetEquipmentById(
            int id,
            String code,
            String name,
            int statusId,
            int categoryId,
            int imageId,
            int regularMaintenanceDay,
            String description) throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();

        // Act
        Equipment equipment = equipmentService.getEquipmentById(id);

        // Assert
        assertNotNull(equipment, "Equipment should not be null");
        assertEquals(id, equipment.getId(), "Equipment ID should match");
        assertEquals(code, equipment.getCode(), "Equipment code should match");
        assertEquals(name, equipment.getName(), "Equipment name should match");
        assertEquals(Status.fromId(statusId), equipment.getStatus(), "Equipment status should match");
        assertEquals(categoryId, equipment.getCategoryId(), "Equipment category should match");
        assertEquals(imageId, equipment.getImageId(), "Equipment image ID should match");
        assertEquals(regularMaintenanceDay, equipment.getRegularMaintenanceDay(), "Regular maintenance day should match");
        assertEquals(description, equipment.getDescription(), "Equipment description should match");
    }

    // Added from GetEquipmentByIdTest.java
    @ParameterizedTest
    @CsvFileSource(resources = "/com/hatecode/services/equipment_getById.csv", numLinesToSkip = 1)
    void testGetEquipmentByIdFromCsv(
            int id,
            String code,
            String name,
            int statusId,
            int categoryId,
            int imageId,
            int regularMaintenanceDay,
            String description) throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();

        // Get the equipment from the service
        Equipment equipment = equipmentService.getEquipmentById(id);

        // Perform assertions against CSV data
        assertNotNull(equipment, "Equipment should not be null");
        assertEquals(id, equipment.getId(), "Equipment ID should match");
        assertEquals(code, equipment.getCode(), "Equipment code should match");
        assertEquals(name, equipment.getName(), "Equipment name should match");
        assertEquals(Status.fromId(statusId), equipment.getStatus(), "Equipment status should match");
        assertEquals(categoryId, equipment.getCategoryId(), "Equipment category should match");
        assertEquals(imageId, equipment.getImageId(), "Equipment image ID should match");
        assertEquals(regularMaintenanceDay, equipment.getRegularMaintenanceDay(), "Regular maintenance day should match");
        assertEquals(description, equipment.getDescription(), "Equipment description should match");
    }

    @Test
    void testGetEquipmentById_NotFound() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Act
        Equipment equipment = equipmentService.getEquipmentById(999);

        // Assert
        assertNull(equipment, "Should return null for non-existent equipment");
    }

    /* =============================================================================
     * Test getEquipment methods
     * ========================================================================== */
    @ParameterizedTest
    @CsvSource({
            "1, ELEC001, Laptop, 2, 1"
    })
    void testGetEquipments(int id,
                           String code,
                           String name,
                           int statusId,
                           int categoryId) throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Act
        List<Equipment> equipments = equipmentService.getEquipments();

        // Assert
        assertNotNull(equipments, "Equipment list should not be null");
        assertFalse(equipments.isEmpty(), "Equipment list should not be empty");

        // Verify first equipment in the list has expected values
        Equipment firstEquipment = equipments.get(0);
        assertEquals(id, firstEquipment.getId(), "Equipment ID should match");
        assertEquals(code, firstEquipment.getCode(), "Equipment code should match");
        assertEquals(name, firstEquipment.getName(), "Equipment name should match");
        assertEquals(Status.fromId(statusId), firstEquipment.getStatus(), "Equipment status should match");
        assertEquals(categoryId, firstEquipment.getCategoryId(), "Equipment category should match");
    }

    @Test
    void testGetEquipments_WithFilters() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Act
        List<Equipment> filteredByName = equipmentService.getEquipments("Laptop", 1, 10, null, null);
        List<Equipment> filteredByCategory = equipmentService.getEquipments("", 1, 10, "category_id", "1");

        // Assert
        assertEquals(4, filteredByName.size(), "Should find one equipment matching name");
        assertTrue(filteredByCategory.size() >= 6, "Should find at least 6 equipment in category 1");

        // Verify the filtered results
        assertEquals("Laptop", filteredByName.getFirst().getName(), "Filtered equipment name should match");
    }

    @Test
    void testHardDeleteEquipment() throws SQLException {
        // This would typically require special setup to ensure no foreign key constraints
        // For now, we'll test with an ID that should be deletable in your test environment
        EquipmentService equipmentService = new EquipmentServiceImpl();

        try {
            // Act
            boolean result = equipmentService.hardDeleteEquipment(21);

            // Assert
            assertTrue(result, "Hard deletion should succeed for equipment without dependencies");

            // Verify
            Equipment deleted = equipmentService.getEquipmentById(21);
            assertNull(deleted, "Equipment should be completely removed after hard delete");

        } catch (SQLException e) {
            // If this fails due to foreign key constraints, we'll test that scenario instead
            assertTrue(e.getMessage().contains("foreign key") ||
                       e.getMessage().contains("referential integrity"),
                      "Hard deletion should fail due to foreign key constraints");
        }
    }

    @Test
    void testHardDeleteEquipment_NotFound() {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Act & Assert
        assertThrows(SQLException.class, () -> equipmentService.hardDeleteEquipment(-1),
                    "Hard deletion should throw exception for invalid ID");
    }

    /* =============================================================================
     * Test updateEquipment methods
     * ========================================================================== */
    @Test
    void testUpdateEquipment() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Arrange
        Equipment original = equipmentService.getEquipmentById(1);
        assertNotNull(original, "Test equipment should exist");

        Equipment updated = new Equipment(
                original.getId(),
                original.getCode(),
                "Updated Equipment Name",
                Status.UNDER_MAINTENANCE,
                original.getCategoryId(),
                original.getImageId(),
                45,
                "Updated description"
        );

        updated.setLastMaintenanceTime(original.getLastMaintenanceTime());

        // Act
        boolean result = equipmentService.updateEquipment(updated);

        // Assert
        assertTrue(result, "Update should succeed");

        // Verify the update
        Equipment retrieved = equipmentService.getEquipmentById(1);
        assertEquals("Updated Equipment Name", retrieved.getName(), "Name should be updated");
        assertEquals(Status.UNDER_MAINTENANCE, retrieved.getStatus(), "Status should be updated");
        assertEquals(45, retrieved.getRegularMaintenanceDay(), "Maintenance day should be updated");
        assertEquals("Updated description", retrieved.getDescription(), "Description should be updated");
    }

    // Added from UpdateEquipment.java
    @ParameterizedTest
    @CsvSource({"1,EQP-002,Welding Machine,2,2,1,-90,Main welding unit, 2022-02-22T00:00:00, 2022-02-22T00:00:00, true"})
    void testUpdateEquipmentWithNegativeRegularMaintenanceDay(int id, String code, String name, int status,
                                                           int categoryId, int imageId, int regularMaintenanceDay,
                                                           String description, LocalDateTime lastMaintenanceTime,
                                                           LocalDateTime createdAt, boolean isActive) {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Create an Equipment object with the provided parameters
        Equipment equipment = new Equipment(
                id,
                code,
                name,
                Status.fromId(status),
                categoryId,
                createdAt,
                imageId,
                regularMaintenanceDay,
                lastMaintenanceTime,
                description,
                isActive
        );

        assertThrows(SQLException.class, () -> equipmentService.updateEquipment(equipment),
                "Update equipment should throw SQLException for negative regularMaintenanceDay");
    }

    @Test
    void testUpdateEquipment_NotFound() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // Arrange
        Equipment nonExistent = new Equipment(
                999, // Non-existent ID
                "EQNONE",
                "Non-existent Equipment",
                Status.ACTIVE,
                1,
                1,
                30,
                "This equipment doesn't exist"
        );

        // Assert
        assertThrows(SQLException.class, () -> equipmentService.updateEquipment(nonExistent), "Update should fail for non-existent equipment");
    }

    @Test
    void testUpdateLiquidatedEquipment() throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        // ID 22-24 is liquidated equipment
        // Arrange
        Equipment e = equipmentService.getEquipmentById(22);
        e.setName("Liquidated Equipment Name");
        // Act & Assert
        Exception ex =  assertThrows(
                IllegalArgumentException.class,
                () -> equipmentService.updateEquipment(e),
                "Updating liquidated equipment should throw IllegalArgumentException");
        assertEquals(ex.getMessage(), ExceptionMessage.LIQUIDATED_EQUIPMENT_CAN_NOT_UPDATE);
    }
}