/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.EquipmentMaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ADMIN
 */
public class EquipmentMaintenanceServiceImpl implements EquipmentMaintenanceService {
    private final Connection externalConn;
    private boolean isTestingConnect = false;
    public EquipmentMaintenanceServiceImpl() {
        this.externalConn = null;
    }
    public EquipmentMaintenanceServiceImpl(Connection conn) {
        this.externalConn = conn;
        this.isTestingConnect = true;
    }

    private Connection getConnection() throws SQLException {
        if (externalConn != null) return externalConn;
        return JdbcUtils.getConn();
    }

    public static EquipmentMaintenance extractEquipmentMaintenance(ResultSet rs) throws SQLException {
        return new EquipmentMaintenance(
                rs.getInt("id"),
                rs.getInt("equipment_id"),
                rs.getInt("maintenance_id"),
                rs.getInt("technician_id"),
                rs.getString("description"),
                Result.fromCode(rs.getInt("result")),
                rs.getString("repair_name"),
                rs.getFloat("repair_price"),
                rs.getTimestamp("inspection_date").toLocalDateTime(),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        );
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance() throws SQLException {
        List<EquipmentMaintenance> res = new ArrayList<>();
        Connection conn = getConnection();
        String sql = "SELECT * FROM equipment_maintenance WHERE is_active = true";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                res.add(extractEquipmentMaintenance(rs));
            }
        }
        if (!isTestingConnect) {
            conn.close();
        }
        return res;
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance(String query) throws SQLException {
        if (query == null || query.isEmpty()) {
            return getEquipmentMaintenance();
        }
        List<EquipmentMaintenance> res = new ArrayList<>();
        Connection conn = getConnection();
        String sql = "SELECT * FROM equipment_maintenance WHERE is_active = true AND (description LIKE ? OR repair_name LIKE ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, "%" + query + "%");
            stm.setString(2, "%" + query + "%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                res.add(extractEquipmentMaintenance(rs));
            }
        }
        if (!isTestingConnect) {
            conn.close();
        }
        return res;
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenance(Maintenance m) throws SQLException {
        if (m == null || m.getId() <= 0) {
            return getEquipmentMaintenance();
        }
        List<EquipmentMaintenance> res = new ArrayList<>();
        String sql = "SELECT * FROM equipment_maintenance WHERE maintenance_id = ? AND is_active = true";
        Connection conn = getConnection();
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, m.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                res.add(extractEquipmentMaintenance(rs));
            }
        }
        if (!isTestingConnect) {
            conn.close();
        }
        return res;
    }

    @Override
    public Equipment getEquipmentByEquipmentMaintenance(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        Equipment equipment = null;
        try (Connection conn = getConnection()) {
            String sql = "SELECT *, m.id AS em_id " +
                    "FROM equipment e " +
                    "LEFT JOIN equipment_maintenance m ON e.id = m.equipment_id " +
                    "WHERE m.id = ? AND m.is_active = true";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                equipment = EquipmentServiceImpl.extractEquipment(rs);
            }
        return equipment;
        }
    }

    @Override
    public boolean addEquipmentMaintenance(EquipmentMaintenance em) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO Equipment_Maintenance (equipment_id, maintenance_id, technician_id, description, result, repair_name, repair_price, inspection_date)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, em.getEquipmentId());
            stm.setInt(2, em.getMaintenanceId());
            stm.setInt(3, em.getTechnicianId());
            stm.setString(4, em.getDescription());
            stm.setInt(5, em.getResult().getCode());
            stm.setString(6, em.getRepairName());
            stm.setFloat(7, em.getRepairPrice());
            if (em.getInspectionDate() != null) {
                stm.setTimestamp(8, Timestamp.valueOf(em.getInspectionDate()));
            } else {
                stm.setNull(8, Types.TIMESTAMP);
            }

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean updateEquipmentMaintenance(EquipmentMaintenance em) throws SQLException {
        Connection conn = getConnection();
        String sql = "UPDATE equipment_maintenance" +
                " SET equipment_id = ?, maintenance_id = ?, technician_id = ?, description = ?, result = ?, repair_name = ?, repair_price = ?, inspection_date = ? " +
                " WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, em.getEquipmentId());
            stm.setInt(2, em.getMaintenanceId());
            stm.setInt(3, em.getTechnicianId());
            stm.setString(4, em.getDescription());
            stm.setInt(5, em.getResult().getCode());
            stm.setString(6, em.getRepairName());
            stm.setFloat(7, em.getRepairPrice());
            stm.setTimestamp(8, Timestamp.valueOf(em.getInspectionDate()));
            stm.setInt(9, em.getId());
            int rowsAffected = stm.executeUpdate();
            if (!isTestingConnect) {
                conn.close();
            }
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteEquipmentMaintenance(int id) throws SQLException {
        String sql = "DELETE FROM equipment_maintenance WHERE id = ?";
        Connection conn = getConnection();
        int rowsAffected = 0;
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, id);
            rowsAffected = stm.executeUpdate();
        } finally {
            if (!isTestingConnect) {
                conn.close();
            }
        }
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteEquipmentMaintenance(EquipmentMaintenance em) throws SQLException {
        Connection conn = getConnection();
        String sql = "DELETE FROM equipment_maintenance WHERE id = ?";
        int rowsAffected;
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, em.getId());
            rowsAffected = stm.executeUpdate();
        } finally {
            if (!isTestingConnect) {
                conn.close();
            }
        }
        return rowsAffected > 0;
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentsMaintenanceByEMId(String kw, int maintenanceId) throws SQLException {
        List<EquipmentMaintenance> maintenanceEquipments = new ArrayList<>();
        String sql = "SELECT em.* FROM equipment_maintenance em JOIN equipment e ON em.equipment_id = e.id WHERE em.maintenance_id = ? AND em.is_active = true";
        if (kw != null && !kw.isEmpty()) {
            sql += " AND (e.name LIKE ?)";
        }
        Connection conn = getConnection();
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, maintenanceId);
            stm.setString(2, "%" + kw + "%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                maintenanceEquipments.add(extractEquipmentMaintenance(rs));
            }
        }
        return maintenanceEquipments;
    }
}
