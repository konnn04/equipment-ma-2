package com.hatecode.services.equipment;

import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteEquipment {
    static Connection conn = null;
    EquipmentService equipmentService = new EquipmentServiceImpl();

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
    @CsvSource({"21"})
    public void testDeleteEquipment(int id) {
        try {
            boolean result = equipmentService.deleteEquipment(id);
            assertTrue(result, "Failed to delete equipment with ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException occurred while deleting equipment with ID: " + id);
        }
    }

    @ParameterizedTest
    @CsvSource({"1"})
    public void testDeleteEquipmentWithForeignKey(int id) {
        assertThrows(SQLException.class, () -> equipmentService.deleteEquipment(id), "Failed to delete equipment with ID: " + id);
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    public void testDeleteEquipmentNotFound(int id) {
        try {

            boolean result = equipmentService.deleteEquipment(id);
            assertFalse(result, "Expected failure when deleting non-existent equipment with ID: " + id);

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException occurred while deleting equipment with ID: " + id);
        }
    }
}
