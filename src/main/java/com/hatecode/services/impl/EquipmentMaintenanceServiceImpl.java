package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.EquipmentMaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentMaintenanceServiceImpl implements EquipmentMaintenanceService {
    // Generic method to execute database operations with proper connection handling
    private <T> T executeQuery(ThrowingFunction<Connection, T> dbOperation) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        try {
            return dbOperation.apply(conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static EquipmentMaintenance extractEquipmentMaintenance(ResultSet rs) throws SQLException {
        return new EquipmentMaintenance(
                rs.getInt("id"),
                rs.getInt("equipment_id"),
                rs.getInt("maintenance_id"),
                rs.getInt("technician_id"),
                rs.getString("equipmentName"),
                rs.getString("description"),
                rs.getInt("result") == 0 ? null : Result.fromCode(rs.getInt("result")),
                rs.getFloat("repair_price"),
                rs.getTimestamp("inspection_date") == null ? null : rs.getTimestamp("inspection_date").toLocalDateTime(),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        );
    }

    private void validateEquipmentMaintenance(EquipmentMaintenance em) {
        if (em == null) {
            throw new IllegalArgumentException(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL);
        }
        if (em.getEquipmentId() <= 0 || em.getMaintenanceId() <= 0 || em.getTechnicianId() <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.EQUIPMENT_MAINTENANCE_NOT_INVALID);
        }
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance() throws SQLException {
        return executeQuery(conn -> {
            List<EquipmentMaintenance> result = new ArrayList<>();
            try (PreparedStatement stm = conn.prepareStatement(
                    "SELECT em.*, e.name AS equipmentName " +
                            "FROM equipment_maintenance em " +
                            "JOIN equipment e ON em.equipment_id = e.id " +
                            "WHERE em.is_active = true")) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    result.add(extractEquipmentMaintenance(rs));
                }
            }
            return result;
        });
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance(String query) throws SQLException {
        if (query == null || query.isEmpty()) {
            return getEquipmentMaintenance();
        }

        return executeQuery(conn -> {
            List<EquipmentMaintenance> result = new ArrayList<>();
            String sql = "SELECT em.*, e.name AS equipmentName " +
                    "FROM equipment_maintenance em " +
                    "JOIN equipment e ON em.equipment_id = e.id " +
                    "WHERE em.is_active = true AND (em.description LIKE ? OR e.name LIKE ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, "%" + query + "%");
                stm.setString(2, "%" + query + "%");
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    result.add(extractEquipmentMaintenance(rs));
                }
            }
            return result;
        });
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance(Maintenance m) throws SQLException {
        if (m == null || m.getId() <= 0) {
            return getEquipmentMaintenance();
        }

        return executeQuery(conn -> {
            List<EquipmentMaintenance> result = new ArrayList<>();
            String sql = "SELECT em.*, e.name AS equipmentName " +
                    "FROM equipment_maintenance em " +
                    "JOIN equipment e ON em.equipment_id = e.id " +
                    "WHERE em.maintenance_id = ? AND em.is_active = true";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, m.getId());
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    result.add(extractEquipmentMaintenance(rs));
                }
            }
            return result;
        });
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance(Maintenance m,User u) throws SQLException {
        if (m == null || m.getId() <= 0) {
            return getEquipmentMaintenance();
        }

        return executeQuery(conn -> {
            List<EquipmentMaintenance> result = new ArrayList<>();
            String sql = "SELECT em.*, e.name AS equipmentName " +
                    "FROM equipment_maintenance em " +
                    "JOIN equipment e ON em.equipment_id = e.id " +
                    "WHERE em.maintenance_id = ? AND em.is_active = true";

            if(u != null && !u.getRole().equals(Role.ADMIN)){
                sql += " AND em.technician_id = ?";
            }

            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, m.getId());

                if(u != null && !u.getRole().equals(Role.ADMIN)){
                    stm.setInt(2,u.getId());
                }

                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    result.add(extractEquipmentMaintenance(rs));
                }
            }
            return result;
        });
    }

    @Override
    public EquipmentMaintenance getEquipmentMaintenanceById(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return executeQuery(conn -> {
            EquipmentMaintenance em = null;
            String sql = "SELECT em.*, e.name AS equipmentName " +
                    "FROM equipment_maintenance em " +
                    "JOIN equipment e ON em.equipment_id = e.id " +
                    "WHERE em.id = ? AND em.is_active = true";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    em = extractEquipmentMaintenance(rs);
                }
            }
            return em;
        });
    }

    @Override
    public Equipment getEquipmentByEquipmentMaintenance(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL);
        }

        return executeQuery(conn -> {
            Equipment equipment = null;
            String sql = "SELECT *, m.id AS em_id " +
                    "FROM equipment e " +
                    "LEFT JOIN equipment_maintenance m ON e.id = m.equipment_id " +
                    "WHERE m.id = ? AND m.is_active = true";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    equipment = EquipmentServiceImpl.extractEquipment(rs);
                }
            }
            return equipment;
        });
    }

    @Override
    public double getTotalPriceEquipmentMaintenance(int maintenanceId) throws SQLException {
        if (maintenanceId <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_ID_NULL);
        }

        return executeQuery(conn -> {
            double totalPrice = 0;
            String sql = "SELECT COALESCE(SUM(repair_price), 0) FROM equipmentma2.Equipment_Maintenance WHERE maintenance_id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, maintenanceId);
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    totalPrice = rs.getDouble(1);
                }
            }
            return totalPrice;
        });
    }

    @Override
    public boolean addEquipmentMaintenance(EquipmentMaintenance em) throws SQLException {
        validateEquipmentMaintenance(em);
        return executeQuery(conn -> {
            String sql = "INSERT INTO Equipment_Maintenance (equipment_id, maintenance_id, technician_id, description)" +
                    " VALUES (?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stm.setInt(1, em.getEquipmentId());
                stm.setInt(2, em.getMaintenanceId());
                stm.setInt(3, em.getTechnicianId());
                stm.setString(4, em.getDescription());

                int rowsAffected = stm.executeUpdate();
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        em.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating equipment maintenance failed, no ID obtained.");
                    }
                }
                return rowsAffected > 0;
            }
        });
    }

    @Override
    public boolean addEquipmentMaintenanceFull(EquipmentMaintenance em) throws SQLException {
        validateEquipmentMaintenance(em);

        return executeQuery(conn -> {
            String sql = "INSERT INTO Equipment_Maintenance (equipment_id, maintenance_id, technician_id, description, result, repair_price, inspection_date)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stm.setInt(1, em.getEquipmentId());
                stm.setInt(2, em.getMaintenanceId());
                stm.setInt(3, em.getTechnicianId());
                stm.setString(4, em.getDescription());
                stm.setInt(5, em.getResult().getCode());
                stm.setFloat(6, em.getRepairPrice());
                if (em.getInspectionDate() != null) {
                    stm.setTimestamp(7, Timestamp.valueOf(em.getInspectionDate()));
                } else {
                    stm.setNull(7, Types.TIMESTAMP);
                }
                int rowsAffected = stm.executeUpdate();
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        em.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating equipment maintenance failed, no ID obtained.");
                    }
                }
                return rowsAffected > 0;
            }
        });
    }

    @Override
    public boolean updateEquipmentMaintenance(EquipmentMaintenance em) throws SQLException {
        validateEquipmentMaintenance(em);
        return executeQuery(conn -> {
            String sql = "UPDATE equipment_maintenance" +
                    " SET equipment_id = ?, maintenance_id = ?, technician_id = ?, description = ?, result = ?, repair_price = ?, inspection_date = ? " +
                    " WHERE id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, em.getEquipmentId());
                stm.setInt(2, em.getMaintenanceId());
                stm.setInt(3, em.getTechnicianId());
                stm.setString(4, em.getDescription());
                stm.setInt(5, em.getResult().getCode());
                stm.setFloat(6, em.getRepairPrice());
                stm.setTimestamp(7, Timestamp.valueOf(em.getInspectionDate()));
                stm.setInt(8, em.getId());
                return stm.executeUpdate() > 0;
            }
        });
    }

    @Override
    public boolean deleteEquipmentMaintenance(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL);
        }

        return executeQuery(conn -> {
            String sql = "DELETE FROM equipment_maintenance WHERE id = ? AND is_active = true";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                return stm.executeUpdate() > 0;
            }
        });
    }

    @Override
    public boolean deleteEquipmentMaintenance(EquipmentMaintenance em) throws SQLException {
        if (em == null) {
            throw new IllegalArgumentException(ExceptionMessage.EQUIPMENT_MAINTENANCE_ID_NULL);
        }
        return deleteEquipmentMaintenance(em.getId());
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentsMaintenanceByEMId(String kw, int maintenanceId) throws SQLException {
        return executeQuery(conn -> {
            List<EquipmentMaintenance> maintenanceEquipments = new ArrayList<>();
            String sql = "SELECT em.*, e.name AS equipmentName " +
                    "FROM equipment_maintenance em " +
                    "JOIN equipment e ON em.equipment_id = e.id " +
                    "WHERE em.maintenance_id = ? AND em.is_active = true";
            if (kw != null && !kw.isEmpty()) {
                sql += " AND (e.name LIKE ? OR em.description LIKE ?)";
            }

            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, maintenanceId);
                if (kw != null && !kw.isEmpty()) {
                    stm.setString(2, "%" + kw + "%");
                    stm.setString(3, "%" + kw + "%");
                }
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    maintenanceEquipments.add(extractEquipmentMaintenance(rs));
                }
            }
            return maintenanceEquipments;
        });
    }

    // Functional interface for SQL operations that can throw SQLException
    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}