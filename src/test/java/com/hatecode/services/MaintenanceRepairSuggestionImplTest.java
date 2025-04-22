package com.hatecode.services;

import com.hatecode.pojo.MaintenanceRepairSuggestion;
import com.hatecode.services.impl.MaintenanceRepairSuggestionImpl;
import com.hatecode.utils.TestDBUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceRepairSuggestionImplTest {
    private Connection conn;
    private MaintenanceRepairSuggestionService maintenanceRepairSuggestionService;

    @BeforeEach
    void setupTestData() throws SQLException {
        conn = TestDBUtils.createIsolatedConnection();
        maintenanceRepairSuggestionService = new MaintenanceRepairSuggestionImpl(conn);
        
        // Initialize test data
        String sql = """
                INSERT INTO Maintenance_Repair_Suggestion (id, name, description, suggest_price, created_at)\s
                VALUES (1, 'Thay động cơ', 'Thay động cơ cho máy khoan', 1500000, '2025-04-15 01:54:45'),
                       (11, 'Test Delete', 'Test record for deletion', 50000, '2025-04-15 01:54:45');
               \s""";
        
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
     * Test getMaintenanceTypes methods
     * ========================================================================== */
    @ParameterizedTest
    @CsvSource({"1,Thay động cơ,Thay động cơ cho máy khoan,1500000,2025-04-15T01:54:45"})
    void testGetMaintenanceRepairSuggestion(int id, String name, String description,
                                           float suggestPrice, LocalDateTime createdAt) throws SQLException {

        List<MaintenanceRepairSuggestion> maintenanceRepairSuggestions = maintenanceRepairSuggestionService.getMaintenanceTypes();

        assertNotNull(maintenanceRepairSuggestions);
        assertFalse(maintenanceRepairSuggestions.isEmpty());
        
        for (MaintenanceRepairSuggestion maintenanceRepairSuggestion : maintenanceRepairSuggestions) {
            if (maintenanceRepairSuggestion.getId() == id) {
                assertEquals(id, maintenanceRepairSuggestion.getId());
                assertEquals(name, maintenanceRepairSuggestion.getName());
                assertEquals(description, maintenanceRepairSuggestion.getDescription());
                assertEquals(suggestPrice, maintenanceRepairSuggestion.getSuggestPrice());
                assertEquals(createdAt, maintenanceRepairSuggestion.getcreatedAt());
            }
        }
    }

    @ParameterizedTest
    @CsvSource({"1,Thay động cơ,Thay động cơ cho máy khoan,1500000,2025-04-15T01:54:45"})
    void testGetMaintenanceRepairSuggestionById(int id, String name, String description,
                                               float suggestPrice, LocalDateTime createdAt) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = maintenanceRepairSuggestionService.getMaintenanceTypeById(id);

        assertNotNull(maintenanceRepairSuggestion);
        assertEquals(id, maintenanceRepairSuggestion.getId());
        assertEquals(name, maintenanceRepairSuggestion.getName());
        assertEquals(description, maintenanceRepairSuggestion.getDescription());
        assertEquals(suggestPrice, maintenanceRepairSuggestion.getSuggestPrice());
        assertEquals(createdAt, maintenanceRepairSuggestion.getcreatedAt());
    }

    @Test
    void testGetMaintenanceRepairSuggestionById_NotFound() throws SQLException {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = maintenanceRepairSuggestionService.getMaintenanceTypeById(-1);
        assertNull(maintenanceRepairSuggestion);
    }

    /* =============================================================================
     * Test addMaintenanceType methods
     * ========================================================================== */
    @Test
    void testAddMaintenanceRepairSuggestion_Success() throws SQLException {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                "Oil Change",
                "Recommended every 3 months for optimal performance.",
                49.99f,
                LocalDateTime.now()
        );

        boolean result = maintenanceRepairSuggestionService.addMaintenanceType(maintenanceRepairSuggestion);
        assertTrue(result);
    }

    @Test
    void testAddMaintenanceRepairSuggestionWithNegativePrice() {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                "Oil Change",
                "Recommended every 3 months for optimal performance.",
                -49.99f,
                LocalDateTime.now()
        );

        assertThrows(SQLException.class, () -> 
            maintenanceRepairSuggestionService.addMaintenanceType(maintenanceRepairSuggestion),
            "Suggested price cannot be negative"
        );
    }

    /* =============================================================================
     * Test updateMaintenanceType methods
     * ========================================================================== */
    @Test
    void testUpdateMaintenanceRepairSuggestion_Success() throws SQLException {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                1,
                "Updated Engine Replacement",
                "Updated description for engine replacement",
                1600000f,
                LocalDateTime.now()
        );

        boolean result = maintenanceRepairSuggestionService.updateMaintenanceType(maintenanceRepairSuggestion);
        assertTrue(result);
        
        // Verify update
        MaintenanceRepairSuggestion updated = maintenanceRepairSuggestionService.getMaintenanceTypeById(1);
        assertEquals("Updated Engine Replacement", updated.getName());
        assertEquals("Updated description for engine replacement", updated.getDescription());
        assertEquals(1600000f, updated.getSuggestPrice());
    }

    @Test
    void testUpdateMaintenanceRepairSuggestion_NotFound() throws SQLException {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                -1,
                "Non-existent Record",
                "This record doesn't exist",
                1500000f,
                LocalDateTime.now()
        );

        boolean result = maintenanceRepairSuggestionService.updateMaintenanceType(maintenanceRepairSuggestion);
        assertFalse(result);
    }

    /* =============================================================================
     * Test deleteMaintenanceType methods
     * ========================================================================== */
    @Test
    void testDeleteMaintenanceRepairSuggestion_Success() throws SQLException {
        // Act
        boolean result = maintenanceRepairSuggestionService.deleteMaintenanceType(11);
        
        // Assert
        assertTrue(result);
        
        // Verify deletion
        MaintenanceRepairSuggestion deleted = maintenanceRepairSuggestionService.getMaintenanceTypeById(11);
        assertNull(deleted);
    }

    @Test
    void testDeleteMaintenanceRepairSuggestion_NotFound() throws SQLException {
        boolean result = maintenanceRepairSuggestionService.deleteMaintenanceType(-12);
        assertFalse(result);
    }
}