package com.hatecode.services.equipment;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateEquipment {
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
    @CsvSource({"22,EQPP002,Welding Machine,2,2,1,60,Main welding unit, 2022-02-22T22:22:22,2021-01-21T21:21:21, true"})
    public void testUpdateEquipment(int id, String code, String name, int status, int categoryId,
                                    int imageId, int regularMaintenanceDay,
                                    String description, LocalDateTime lastMaintenanceTime, LocalDateTime createdDate, boolean isActive) {
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

        } catch (Exception ex) {
            // Handle the exception if needed
            ex.printStackTrace();
        }
    }

    @ParameterizedTest
    @CsvSource({"1,EQP-002,Welding Machine,2,2,1,-90,Main welding unit, 2022-02-22T22:22:22,2021-01-21T21:21:21, true"})
    public void testUpdateEquipmentWithNegativeRegularMaintenanceDay(int id, String code, String name, int status,
                                                                     int categoryId, int imageId, int regularMaintenanceDay,
                                                                     String description, LocalDateTime lastMaintenanceTime,
                                                                     LocalDateTime createdDate, boolean isActive) {
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
