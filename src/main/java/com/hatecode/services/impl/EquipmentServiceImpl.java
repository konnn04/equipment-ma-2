package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.EquipmentService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentServiceImpl implements EquipmentService {

    ImageService imageService = new ImageServiceImpl();

    @Override
    public Equipment getEquipmentById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment e " +
                    "LEFT JOIN Category c ON e.category = c.id " +
                    "WHERE e.id = ?";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Equipment e = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        Status.fromId(rs.getInt("status")),
                        rs.getInt("category"),
                        rs.getInt("image"),
                        rs.getInt("regular_maintenance_day"),
                        rs.getString("description")
                );
                return e;
            }
            return null;
        }
    }

    @Override
    public List<Equipment> getEquipments() throws SQLException {
        List<Equipment> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT e.* FROM equipment e ";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        Status.fromId(rs.getInt("status")),
                        rs.getInt("category"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getInt("image"),
                        rs.getInt("regular_maintenance_day"),
                        rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                );
                res.add(e);
            }
        }
        return res;
    }

    @Override
    public List<Equipment> getEquipments(String query, int page, int pageSize, String key, String value) throws SQLException {
        /* Kiểm tra và xử lý các tham số đầu vào */
        query = query.trim();
        if (query == null || query.isEmpty()) query = "";
        page = Math.max(1, page);
        pageSize = Math.max(1, pageSize);
        List<Equipment> res = new ArrayList<>();
        System.out.println(key+ " == " + value);
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT e.* " +
                    "FROM equipment e " +
                    "WHERE (e.code LIKE ? OR e.name LIKE ?) ";
            boolean b = key != null && !key.isEmpty() && value != null && !value.isEmpty();
            if (b) {
                sql += "AND (" + key.toLowerCase() + " = ?)";
            }
            sql += " LIMIT ?, ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, "%" + query + "%");
            stm.setString(2, "%" + query + "%");
            if (b) {
                stm.setString(3, value);
                stm.setInt(4, (page - 1) * pageSize);
                stm.setInt(5, pageSize);
            } else {
                stm.setInt(3, (page - 1) * pageSize);
                stm.setInt(4, pageSize);
            }

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        Status.fromId(rs.getInt("status")),
                        rs.getInt("category"),
                        rs.getTimestamp("created_date") != null ? rs.getTimestamp("created_date").toLocalDateTime() : null,
                        rs.getInt("image"),
                        rs.getInt("regular_maintenance_day"),
                        rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                );
                res.add(e);
            }
        }
        return res;
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintainances(int id) throws SQLException {
        List<EquipmentMaintenance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment_maintenance WHERE equipment_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
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
    public boolean addEquipment(Equipment e) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO Equipment (code, name, status, category, regular_maintenance_day, description, image) VALUES"+
                    " (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, e.getCode());
            stm.setString(2, e.getName());
            stm.setInt(3, e.getStatus().getId());
            stm.setInt(4, e.getCategoryId());
            stm.setInt(5, e.getRegularMaintenanceDay());
//            stm.setTimestamp(6, e.getLastMaintenanceTime() != null ? java.sql.Timestamp.valueOf(e.getLastMaintenanceTime()) : null);
            stm.setString(6, e.getDescription());
            stm.setInt(7, e.getImageId());
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được thêm vào
        }
    }

    @Override
    public boolean updateEquipment(Equipment e) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment SET code = ?, name = ?, status = ?, category = ?, regular_maintenance_day = ?, last_maintenance_time = ?, description = ?, image = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, e.getCode());
            stm.setString(2, e.getName());
            stm.setInt(3, e.getStatus().getId());
            stm.setInt(4, e.getCategoryId());
            stm.setInt(5, e.getRegularMaintenanceDay());
            stm.setTimestamp(6, Timestamp.valueOf(e.getLastMaintenanceTime()));
            stm.setString(7, e.getDescription());
            stm.setInt(8, e.getImageId());
            stm.setInt(9, e.getId()); // ID của thiết bị cần cập nhật
            // Thực hiện câu lệnh cập nhật
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được cập nhật
        }
    }

    @Override
    public boolean deleteEquipment(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM equipment WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được xóa
        }
    }

    @Override
    public List<Object> getDistinctValues(String columnName) throws SQLException {
        List<Object> distinctValues = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT DISTINCT " + columnName + " FROM equipment";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                if (columnName.equals("status")) {
                    distinctValues.add(rs.getInt(columnName));
                } else if (columnName.equals("category")) {
                    distinctValues.add(rs.getInt(columnName));
                } else {
                    distinctValues.add(rs.getString(columnName));
                }
            }
        }
        return distinctValues;
    }
}
