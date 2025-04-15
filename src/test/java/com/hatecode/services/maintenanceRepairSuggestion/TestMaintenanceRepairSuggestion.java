package com.hatecode.services.maintenanceRepairSuggestion;

import com.hatecode.pojo.MaintenanceRepairSuggestion;
import com.hatecode.services.impl.MaintenanceRepairSuggestionImpl;
import com.hatecode.services.interfaces.MaintenanceRepairSuggestionService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TestMaintenanceRepairSuggestion {
    static Connection conn = null;
    MaintenanceRepairSuggestionService maintenanceRepairSuggestionService = new MaintenanceRepairSuggestionImpl();

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = JdbcUtils.getConn();
        conn.setAutoCommit(false);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();  // Roll back any pending changes
            } finally {
                conn.setAutoCommit(true);
                conn.close();     // Close the connection
            }
        }
    }

    @ParameterizedTest
    @CsvSource({"1,Thay động cơ, Thay động cơ cho máy khoan,1500000,2025-04-15T01:54:45"})
    public void testGetMaintenanceRepairSuggestion(int id, String name, String description,
                                                float suggestPrice, LocalDateTime createdDate) throws SQLException {

        List<MaintenanceRepairSuggestion> maintenanceRepairSuggestions = maintenanceRepairSuggestionService.getMaintenanceTypes();

        for (MaintenanceRepairSuggestion maintenanceRepairSuggestion : maintenanceRepairSuggestions) {
            if (maintenanceRepairSuggestion.getId() == id) {
                assertEquals(id, maintenanceRepairSuggestion.getId());
                assertEquals(name, maintenanceRepairSuggestion.getName());
                assertEquals(description, maintenanceRepairSuggestion.getDescription());
                assertEquals(suggestPrice, maintenanceRepairSuggestion.getSuggestPrice());
                assertEquals(createdDate, maintenanceRepairSuggestion.getCreatedDate());
            }
        }
    }

    @ParameterizedTest
    @CsvSource({"1,Thay động cơ,Thay động cơ cho máy khoan,1500000,2025-04-15T01:54:45"})
    public void testGetMaintenanceRepairSuggestionById(int id, String name, String description,
                                                               float suggestPrice, LocalDateTime createdDate) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = maintenanceRepairSuggestionService.getMaintenanceTypeById(id);

        assertEquals(id, maintenanceRepairSuggestion.getId());
        assertEquals(name, maintenanceRepairSuggestion.getName());
        assertEquals(description, maintenanceRepairSuggestion.getDescription());
        assertEquals(suggestPrice, maintenanceRepairSuggestion.getSuggestPrice());
        assertEquals(createdDate, maintenanceRepairSuggestion.getCreatedDate());
    }

    @ParameterizedTest
    @CsvSource({"-1,Thay động cơ,Thay động cơ cho máy khoan,1500000,2025-04-13T10:18:00"})
    public void testGetNotFoundMaintenanceRepairSuggestionById(int id, String name, String description,
                                                   float suggestPrice, LocalDateTime createdDate) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = maintenanceRepairSuggestionService.getMaintenanceTypeById(id);

        assertNull(maintenanceRepairSuggestion);
    }

    @ParameterizedTest
    @CsvSource({"Oil Change,Recommended every 3 months for optimal performance.,49.99,2024-03-10T10:15:00"})
    public void testAddMaintenanceRepairSuggestion(String name, String description,
                                                   float suggestPrice, LocalDateTime createdDate) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                name,
                description,
                suggestPrice,
                createdDate
        );

        boolean result = maintenanceRepairSuggestionService.addMaintenanceType(maintenanceRepairSuggestion);

        assertTrue(result);
    }

    @ParameterizedTest
    @CsvSource({"Oil Change,Recommended every 3 months for optimal performance.,-49.99,2024-03-10T10:15:00"})
    public void testAddMaintenanceRepairSuggestionWithNegativePrice(String name, String description,
                                                   float suggestPrice, LocalDateTime createdDate) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                name,
                description,
                suggestPrice,
                createdDate
        );

        assertThrows(SQLException.class, () -> maintenanceRepairSuggestionService.addMaintenanceType(maintenanceRepairSuggestion),
                "Suggested price cannot be negative");
    }

    @ParameterizedTest
    @CsvSource({"1,Thay động cơ,Thay động cơ cho máy khoan,1500000,2025-04-13T10:18:00"})
    public void testUpdateMaintenanceRepairSuggestion(int id, String name, String description,
                                                      float suggestPrice, LocalDateTime createdDate) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                id,
                name,
                description,
                suggestPrice,
                createdDate
        );

        boolean result = maintenanceRepairSuggestionService.updateMaintenanceType(maintenanceRepairSuggestion);

        assertTrue(result);
    }

    @ParameterizedTest
    @CsvSource({"-1,Thay động cơ,Thay động cơ cho máy khoan,1500000,2025-04-13T10:18:00"})
    public void testUpdateMaintenanceRepairSuggestionWithNotFound(int id, String name, String description,
                                                      float suggestPrice, LocalDateTime createdDate) throws SQLException {

        MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                id,
                name,
                description,
                suggestPrice,
                createdDate
        );

        boolean result = maintenanceRepairSuggestionService.updateMaintenanceType(maintenanceRepairSuggestion);

        assertFalse(result);
    }

    @ParameterizedTest
    @CsvSource({"11"})
    public void testDeleteMaintenanceRepairSuggestion(int id) throws SQLException {

        boolean result = maintenanceRepairSuggestionService.deleteMaintenanceType(id);

        assertTrue(result);
    }

    @ParameterizedTest
    @CsvSource({"-12"})
    public void testDeleteMaintenanceRepairSuggestionWithNotFound(int id) throws SQLException {

        boolean result = maintenanceRepairSuggestionService.deleteMaintenanceType(id);

        assertFalse(result);
    }

}
