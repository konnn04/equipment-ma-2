package com.hatecode.services.equipment;

import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import utils.TestDBUtils;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteEquipment {
    static Connection conn;
    EquipmentService equipmentService;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        JdbcUtils.connectionProvider = TestDBUtils::getConnection;
        conn = TestDBUtils.getConnection();
    }

    @BeforeEach
    void setupTestData() throws SQLException {
        equipmentService = new EquipmentServiceImpl();
        conn.setAutoCommit(false);
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        conn.rollback();
        conn.setAutoCommit(true);
    }

    @AfterAll
    static void shutdownDatabase() throws SQLException {
        if (conn != null) {
            conn.close();
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
        assertThrows(SQLException.class, () -> equipmentService.hardDeleteEquipment(id), "Failed to delete equipment with ID: " + id);
    }

    @ParameterizedTest
    @CsvSource({"-1"})
    public void testDeleteEquipmentNotFound(int id) {
        assertThrows(SQLException.class, () -> equipmentService.deleteEquipment(id), "Failed to delete equipment with ID: " + id);
    }
}
