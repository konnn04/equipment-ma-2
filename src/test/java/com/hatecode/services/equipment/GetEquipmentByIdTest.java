package com.hatecode.services.equipment;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetEquipmentByIdTest {
    static Connection conn = null;
    EquipmentService equipmentService = new EquipmentServiceImpl();

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = JdbcUtils.getConn();
        conn.setAutoCommit(false);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.setAutoCommit(true);
    }

    @Test
    public void testGetEquipments() throws SQLException {
        // Get all equipment from the service
        List<Equipment> equipments = equipmentService.getEquipments();

        // Verify the list is not null and has expected items
        assertNotNull(equipments, "Equipment list should not be null");
        assertFalse(equipments.isEmpty(), "Equipment list should not be empty");

        // Verify the equipment with ID 1 matches expected values from CSV
        Equipment firstEquipment = equipments.stream()
                .filter(e -> e.getId() == 1)
                .findFirst()
                .orElse(null);

        assertNotNull(firstEquipment, "Equipment with ID 1 should exist");
        assertEquals(1, firstEquipment.getId(), "Equipment ID should match");
        assertEquals("ELEC001", firstEquipment.getCode());
        assertEquals("Laptop", firstEquipment.getName());
        assertEquals(Status.fromId(2), firstEquipment.getStatus());
        assertEquals(1, firstEquipment.getCategoryId());
        assertEquals(1, firstEquipment.getImageId(), "Equipment image ID should match");
        assertEquals(180, firstEquipment.getRegularMaintenanceDay(), "Regular maintenance day should match");
        assertEquals("Máy tính xách tay văn phòng", firstEquipment.getDescription(), "Equipment description should match");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "getEquipmentById.csv", numLinesToSkip = 1)
    public void testGetEquipmentById(
            int id,
            String code,
            String name,
            int statusId,
            int categoryId,
            int imageId,
            int regularMaintenanceDay,
            String description) throws SQLException {

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

//    @ParameterizedTest
//    @CsvFileSource(resources = "addEquipmentWithNullFields.csv", numLinesToSkip = 1)
//    public void testGetEquipmentsWithFilters(
//            String query,
//            int page,
//            int pageSize,
//            String key,
//            String value,
//            int expectedMinSize) throws SQLException {
//
//        // Get filtered equipment
//        List<Equipment> equipments = equipmentService.getEquipments(query, page, pageSize, key, value);
//
//        // Verify results
//        assertNotNull(equipments, "Equipment list should not be null");
//        assertTrue(equipments.size() <= pageSize, "Results should respect page size limit");
//        assertTrue(equipments.size() >= expectedMinSize,
//                "Should find at least " + expectedMinSize + " equipment matching criteria");
//
//        // Verify query filtering
//        if (query != null && !query.isEmpty()) {
//            equipments.forEach(e ->
//                    assertTrue(
//                            e.getCode().contains(query) || e.getName().contains(query),
//                            "Equipment should match query: " + query
//                    )
//            );
//        }
//
//        // Verify key/value filtering
//        if (key != null && value != null) {
//            if (key.equals("status")) {
//                int statusId = Integer.parseInt(value);
//                equipments.forEach(e ->
//                        assertEquals(Status.fromId(statusId), e.getStatus(),
//                                "Equipment status should match filtered value")
//                );
//            } else if (key.equals("category")) {
//                int categoryId = Integer.parseInt(value);
//                equipments.forEach(e ->
//                        assertEquals(categoryId, e.getCategoryId(),
//                                "Equipment category should match filtered value")
//                );
//            }
//        }
//    }
}