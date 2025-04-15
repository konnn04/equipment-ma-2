package com.hatecode.services.equipment;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Image;
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
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AddEquipmentTest {

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
    @CsvSource({
            "1, EQPP01, Air Compressor, 1, 1, 2024-01-15T10:00:00, 1, 30, 2024-03-15T09:00:00, Used in welding operations, true, true"
    })
    public void testAddEquipment(int id, String code, String name, int status, int categoryId,
                                 LocalDateTime createdDate, int imageId, int regularMaintenanceDay,
                                 LocalDateTime lastMaintenanceTime, String description, boolean isActive,
                                 boolean Expected){
        // Test adding a new equipment
        Equipment e = new Equipment(
                code,
                name,
                Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                description
        );;

        try{

            boolean result = equipmentService.addEquipment(e);
            assertEquals(Expected, result, "Adding equipment should return " + Expected);

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1, EQP-001, Air Compressor, 1, 1, 2024-01-15T10:00:00, 1, 30, 2024-03-15T09:00:00, Used in welding operations, true, true"
    })
    public void testAddDuplicateEquipment(int id, String code, String name, int status, int categoryId,
                                 LocalDateTime createdDate, int imageId, int regularMaintenanceDay,
                                 LocalDateTime lastMaintenanceTime, String description, boolean isActive,
                                 boolean Expected){

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

        try{
            //Add first equipment
            boolean result = equipmentService.addEquipment(e);
            assertEquals(Expected, result, "Adding equipment should return " + Expected);

            //Add duplicate equipment
            assertThrows(SQLException.class,() -> equipmentService.addEquipment(e), "Adding duplicate equipment should throw SQLIntegrityConstraintViolationException");

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "addEquipmentWithNullFields.csv", numLinesToSkip = 1)
    public void testAddEquipmentWithNotNullField(String code, String name, int status, int categoryId,
                                                 int imageId, int regularMaintenanceDay,
                                                 String description){
        Equipment e = new Equipment(
                Objects.equals(code, "null") ? null : code,
                Objects.equals(name, "null") ? null : name,
                status == -1 ? null : Status.fromId(status),
                categoryId,
                imageId,
                regularMaintenanceDay,
                Objects.equals(description, "null") ? null : description
        );

        assertThrows(SQLException.class,() -> equipmentService.addEquipment(e), "Adding equipment with null fields should throw SQLIntegrityConstraintViolationException");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "addEquipmentWithImage.csv", numLinesToSkip = 1)
    public void testAddEquipmentWithImage(String code, String name, int status, int categoryId,
                                                 int imageId, int regularMaintenanceDay,
                                                 String description){
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

        try{
            if (imageId != -1) {
                boolean result = equipmentService.addEquipment(e, new Image(1, "test.jpg", new Date(), "path/to/image"));
                assertTrue(result, "Adding equipment should return " + true);
            }
            else
//                reference to imageId is not found
                assertThrows(SQLException.class,() -> equipmentService.addEquipment(e, new Image(-1, "not found", new Date(), "not found")), "Adding equipment with null fields should throw SQLIntegrityConstraintViolationException");

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }



}
