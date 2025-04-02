package com.hatecode.services.impl;


import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.Status;
import com.hatecode.services.interfaces.MaintenanceTypeService;
import com.hatecode.services.interfaces.UserService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.services.interfaces.StatusService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ADMIN
 */
public class EquipmentServiceImpl implements EquipmentService {
    MaintenanceTypeService mts = new MaintenanceTypeServiceImpl();

    @Override
    public Equipment getEquipmentById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT e.*, s.id AS status_id, s.name AS status_name, s.description AS status_description, " +
                    "c.id AS category_id, c.name AS category_name FROM equipment e " +
                    "LEFT JOIN Status s ON e.status = s.id " +
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
                        rs.getDate("import_date"),
                        new Status(
                                rs.getInt("status"),
                                rs.getString("status_name"),
                                rs.getString("status_description")
                        ),
                        new Category(
                                rs.getInt("category"),
                                rs.getString("category_name")
                        ),
                        rs.getString("description")
                );
                e.setStatusName(rs.getString("status_name"));
                return e;
            }
            return null;
        }
    }

    @Override
    public List<Equipment> getEquipments() throws SQLException {
        List<Equipment> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT e.*, s.id AS status_id, s.name AS status_name, s.description AS status_description, " +
                    "c.id AS category_id, c.name AS category_name " +
                    "FROM equipment e " +
                    "LEFT JOIN Status s ON e.status = s.id " +
                    "LEFT JOIN Category c ON e.category = c.id";

            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getDate("import_date"),
                        new Status(
                                rs.getInt("status_id"),
                                rs.getString("status_name"),
                                rs.getString("status_description")
                        ),
                        new Category(
                                rs.getInt("category_id"),
                                rs.getString("category_name")
                        ),
                        rs.getString("description")
                );
                e.setStatusName(rs.getString("status_name"));
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
            String sql = "SELECT e.*, s.id AS status_id, s.name AS status_name, s.description AS status_description, " +
                    "c.id AS category_id, c.name AS category_name " +
                    "FROM equipment e " +
                    "LEFT JOIN Status s ON e.status = s.id " +
                    "LEFT JOIN Category c ON e.category = c.id " +
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
                        rs.getDate("import_date"),
                        new Status(
                                rs.getInt("status_id"),
                                rs.getString("status_name"),
                                rs.getString("status_description")
                        ),
                        new Category(
                                rs.getInt("category_id"),
                                rs.getString("category_name")
                        ),
                        rs.getString("description")
                );
                e.setStatusName(rs.getString("status_name"));
                res.add(e);
            }
        }
        return res;
    }

    @Override
    public void getEquipmentsImage() throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT e.id AS equipment_id, i.filename, i.path " +
                    "FROM equipment e " +
                    "JOIN equipment_image ei ON e.id = ei.equipment_id " +
                    "JOIN image i ON ei.image_id = i.id";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int equipmentId = rs.getInt("equipment_id");
                String filename = rs.getString("filename");
                String path = rs.getString("path");

                // In thông tin hình ảnh (hoặc thực hiện hành động khác)
                System.out.println("Equipment ID: " + equipmentId + ", Filename: " + filename + ", Path: " + path);
            }
        }
    }

    @Override
    public List<EquipmentMaintainance> getEquipmentMaintainances(int id) throws SQLException {
        List<EquipmentMaintainance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment_maintenance WHERE equipment_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                EquipmentMaintainance em = new EquipmentMaintainance(
                        rs.getInt("id"),
                        this.getEquipmentById(rs.getInt("equipment_id")),
                        rs.getString("description"),
                        mts.getMaintenanceTypeById(rs.getInt("maintenance_type_id")),
                        rs.getFloat("price"),
                        rs.getInt("maintenance_id")
                );
                res.add(em);
            }
        }
        return res;
    }

    @Override
    public boolean addEquipment(Equipment e) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO equipment (code, name, status, category, import_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, e.getCode());
            stm.setString(2, e.getName());
            stm.setInt(3, e.getStatus().getId());
            stm.setInt(4, e.getCategory().getId());
            stm.setDate(5, new java.sql.Date(e.getImportDate().getTime()));

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được thêm vào
        }
    }

    @Override
    public boolean updateEquipment(Equipment e) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment SET code = ?, name = ?, status = ?, category = ?, import_date = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, e.getCode());
            stm.setString(2, e.getName());
            stm.setInt(3, e.getStatus().getId());
            stm.setInt(4, e.getCategory().getId());
            stm.setDate(5, new java.sql.Date(e.getImportDate().getTime()));
            stm.setInt(6, e.getId());

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
