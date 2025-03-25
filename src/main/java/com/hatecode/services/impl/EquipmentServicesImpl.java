package com.hatecode.services.impl;


import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.JdbcUtils;
import com.hatecode.services.EquipmentServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ADMIN
 */
public class EquipmentServicesImpl implements EquipmentServices{

    @Override
    public List<Equipment> getEquipments() throws SQLException {
        List<Equipment> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getDate("import_date"),
                        rs.getInt("category"),
                        rs.getInt("status")
                );
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
                        rs.getInt("equipment_id"),
                        rs.getString("description"),
                        rs.getInt("maintenance_type_id"),
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
            stm.setInt(3, e.getStatus());
            stm.setInt(4, e.getCategory());
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
            stm.setInt(3, e.getStatus());
            stm.setInt(4, e.getCategory());
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
    
}
