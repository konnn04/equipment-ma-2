package com.hatecode.services.equipment;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import utils.TestDBUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateEquipment {
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
    @CsvSource({"21,EQPP002,Welding Machine,2,2,1,60,Main welding unit, 2022-02-22,2021-01-21, true"})
    public void testUpdateEquipment(int id, String code, String name, int status, int categoryId,
                                    int imageId, int regularMaintenanceDay,
                                    String description, Date lastMaintenanceTime, Date createdDate, boolean isActive) {
        // Create an Equipment object with the provided parameters
        Equipment equipment = new Equipment(
                id,
                code,
                name,
                Status.fromId(status),
                categoryId,
                createdDate,
                imageId,
                regularMaintenanceDay,
                lastMaintenanceTime,
                description,
                isActive
        );

        try {
            // Call the updateEquipment method and assert the result
            boolean result = equipmentService.updateEquipment(equipment);
            assertTrue(result, "Update equipment should return " + true);

        }
        catch (SQLException e) {
            // Handle the exception if needed
            e.printStackTrace();
            fail("SQLException occurred while updating equipment: " + e.getMessage());
        }
        catch (Exception ex) {
            // Handle the exception if needed
            ex.printStackTrace();
        }
    }

    @ParameterizedTest
    @CsvSource({"1,EQP-002,Welding Machine,2,2,1,-90,Main welding unit, 2022-02-22,2021-01-21, true"})
    public void testUpdateEquipmentWithNegativeRegularMaintenanceDay(int id, String code, String name, int status,
                                                                     int categoryId, int imageId, int regularMaintenanceDay,
                                                                     String description, Date lastMaintenanceTime,
                                                                     Date createdDate, boolean isActive) {
        // Create an Equipment object with the provided parameters
        Equipment equipment = new Equipment(
                id,
                code,
                name,
                Status.fromId(status),
                categoryId,
                createdDate,
                imageId,
                regularMaintenanceDay,
                lastMaintenanceTime,
                description,
                isActive
        );

        try {
            // Call the updateEquipment method and assert the result
            assertThrows(SQLException.class, () -> equipmentService.updateEquipment(equipment),
                    "Update equipment should throw SQLException for negative regularMaintenanceDay");

        } catch (Exception ex) {
            // Handle the exception if needed
            ex.printStackTrace();
        }
    }

//    @ParameterizedTest
//    @CsvSource({})
//    public void testUpdateEquipmentWithNullField(int id, String code, String name, int status,
//                                                 int categoryId, int imageId, int regularMaintenanceDay,
//                                                 String description, LocalDateTime lastMaintenanceTime,
//                                                 LocalDateTime createdDate, boolean isActive){
//
//    }




}
