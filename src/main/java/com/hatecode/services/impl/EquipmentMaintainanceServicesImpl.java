/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.JdbcUtils;
import com.hatecode.services.EquipmentMaintainanceServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ADMIN
 */
public class EquipmentMaintainanceServicesImpl implements EquipmentMaintainanceServices {

    @Override
    public List<EquipmentMaintainance> getEquipmentMaintainance() throws SQLException {
        List<EquipmentMaintainance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment_maintenance";
            PreparedStatement stm = conn.prepareStatement(sql);
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
    public Equipment getEquipmentByEMId(int id) throws SQLException {
        Equipment equipment = null;
        try (Connection conn = JdbcUtils.getConn()) {
            // Truy vấn để lấy thông tin thiết bị dựa trên id của bản ghi bảo trì
            String sql = "SELECT e.* FROM equipment e "
                    + "JOIN equipment_maintenance em ON e.id = em.equipment_id "
                    + "WHERE em.id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id); // Thiết lập giá trị tham số cho id của bản ghi bảo trì
            ResultSet rs = stm.executeQuery();

            // Nếu có kết quả, tạo đối tượng Equipment từ dữ liệu trả về
            if (rs.next()) {
                equipment = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getDate("import_date"),
                        rs.getInt("category"),
                        rs.getInt("status")
                );
            }
        }
        return equipment;
    }

    @Override
    public boolean addEquipmentMaintainance(EquipmentMaintainance em) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO equipment_maintenance (equipment_id, description, maintenance_type_id, price, maintenance_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, em.getEquipmentId());
            stm.setString(2, em.getDescription());
            stm.setInt(3, em.getMaintainanceTypeId());
            stm.setFloat(4, em.getPrice());
            stm.setInt(5, em.getMaintainanceId());

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được thêm vào
        }
    }

    @Override
    public boolean updateEquipmentMaintainance(EquipmentMaintainance em) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment_maintenance SET equipment_id = ?, description = ?, maintenance_type_id = ?, price = ?, maintenance_id = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, em.getEquipmentId());
            stm.setString(2, em.getDescription());
            stm.setInt(3, em.getMaintainanceTypeId());
            stm.setFloat(4, em.getPrice());
            stm.setInt(5, em.getMaintainanceId());
            stm.setInt(6, em.getId());

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được cập nhật
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
