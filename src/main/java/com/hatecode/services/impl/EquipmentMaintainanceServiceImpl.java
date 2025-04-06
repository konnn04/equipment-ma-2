/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.EquipmentMaintainanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ADMIN
 */
public class EquipmentMaintainanceServiceImpl implements EquipmentMaintainanceService {

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintainance() throws SQLException {
        List<EquipmentMaintenance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment_maintenance";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                EquipmentMaintenance em = new EquipmentMaintenance(
                        rs.getInt("id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("maintenance_id"),
                        rs.getInt("technician_id"),
                        rs.getString("description"),
                        Result.fromCode(rs.getInt("result")),
                        rs.getString("repair_name"),
                        rs.getFloat("repair_price"),
                        rs.getTimestamp("inspection_date") != null ? rs.getTimestamp("inspection_date").toLocalDateTime() : null,
                        rs.getTimestamp("created_date") != null ? rs.getTimestamp("created_date").toLocalDateTime() : null
                );
                res.add(em);
            }
        }
        return res;
    }

    @Override
    public Equipment getEquipmentByEMId(int id) throws SQLException {
        Equipment equipment = null;
        try (Connection conn = JdbcUtils.getConn()) {
            // Truy vấn để lấy thông tin thiết bị dựa trên id của bản ghi bảo trì
            String sql = "SELECT e.*, m.id AS maintenance_id, " +
                    "c.id AS category_id, c.name AS category_name ," +
                    "i.id AS image_id, i.filename AS image_filename, i.created_date AS image_created_date, i.path AS image_path " +
                    "FROM equipment e " +
                    "LEFT JOIN equipment_maintenance m ON e.id = m.equipment_id " +
                    "LEFT JOIN image i ON e.image = i.id " +
                    "LEFT JOIN Category c ON e.category = c.id " +
                    "WHERE m.id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id); // Thiết lập giá trị tham số cho id của bản ghi bảo trì
            ResultSet rs = stm.executeQuery();

            // Nếu có kết quả, tạo đối tượng Equipment từ dữ liệu trả về
            if (rs.next()) {
                equipment = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        Status.fromId(rs.getInt("status")),
                        new Category(
                                rs.getInt("category_id"),
                                rs.getString("category_name"),
                                rs.getBoolean("category_is_active")
                        ),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        new Image(
                                rs.getInt("image_id"),
                                rs.getString("image_filename"),
                                rs.getTimestamp("image_created_date") != null ? rs.getTimestamp("image_created_date").toLocalDateTime() : null,
                                rs.getString("image_path")
                        ),
                        rs.getInt("regular_maintenance_day"),
                        rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                );
            }
        }
        return equipment;
    }

    @Override
    public boolean addEquipmentMaintainance(EquipmentMaintenance em) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
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
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được thêm vào
        }
    }

    @Override
    public boolean updateEquipmentMaintainance(EquipmentMaintenance em) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment_maintenance" +
            " SET equipment_id = ?, maintenance_id = ?, technician_id = ?, description = ?, result = ?, repair_name = ?, repair_price = ?, inspection_date = ? " +
            " WHERE id = ?";
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
            stm.setInt(9, em.getId()); // Thiết lập id của bản ghi cần cập nhật
            // Thực hiện câu lệnh cập nhật
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được cập nhật
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteEquipmentMaintainance(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM equipment_maintenance WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được xóa
        }
    }

}
