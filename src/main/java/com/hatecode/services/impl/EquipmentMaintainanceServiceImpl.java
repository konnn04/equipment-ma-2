/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.EquipmentMaintainanceService;
import com.hatecode.services.interfaces.StatusService;
import com.mysql.cj.jdbc.JdbcConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ADMIN
 */
public class EquipmentMaintainanceServiceImpl implements EquipmentMaintainanceService {

    @Override
    public List<EquipmentMaintainance> getEquipmentMaintainance() throws SQLException {
        List<EquipmentMaintainance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT em.*, ems.name, ems.description as ems_description, u.id as userId ,u.username\n" +
                    "FROM equipmentma2test.equipment_maintenance em\n" +
                    "LEFT JOIN equipmentma2test.equipment_maintenance_status ems ON em.status_id = ems.id\n" +
                    "LEFT JOIN equipmentma2test.user u ON em.technical_man_id = u.id;";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                EquipmentMaintainance em = new EquipmentMaintainance(
                        rs.getInt("id"),
                        rs.getInt("equipment_id"),
                        rs.getString("description"),
                        rs.getInt("maintenance_type_id"),
                        rs.getFloat("price"),
                        rs.getInt("maintenance_id"),
                        new User(
                                rs.getInt("userId"),
                                rs.getString("username")
                        ),
                        new EquipmentMaintenanceStatus(
                                rs.getInt("status_id"),
                                rs.getString("name"),
                                rs.getString("ems_description")
                        ),
                        rs.getDate("inspection_date")
                );
                res.add(em);
            }
        }
        return res;
    }

    @Override
    public List<EquipmentMaintainance> getEquipmentMaintainance(Maintenance m) throws SQLException {
        List<EquipmentMaintainance> res = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            String sql = "SELECT em.*, ems.name, ems.description as ems_description, u.id as userId ,u.username\n" +
                    "FROM equipmentma2test.equipment_maintenance em\n" +
                    "LEFT JOIN equipmentma2test.equipment_maintenance_status ems ON em.status_id = ems.id\n" +
                    "LEFT JOIN equipmentma2test.user u ON em.technical_man_id = u.id\n"+
                    "WHERE em.maintenance_id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1,m.getId());
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                EquipmentMaintainance em = new EquipmentMaintainance(
                        rs.getInt("id"),
                        rs.getInt("equipment_id"),
                        rs.getString("description"),
                        rs.getInt("maintenance_type_id"),
                        rs.getFloat("price"),
                        rs.getInt("maintenance_id"),
                        new User(
                                rs.getInt("userId"),
                                rs.getString("username")
                        ),
                        new EquipmentMaintenanceStatus(
                                rs.getInt("status_id"),
                                rs.getString("name"),
                                rs.getString("ems_description")
                        ),
                        rs.getDate("inspection_date")
                );
                res.add(em);
            }
        }
        return res;
    }

    @Override
    public Equipment getEquipmentByEMId(int id) throws SQLException {
        StatusService statusService = new StatusServiceImpl();
        Equipment equipment = null;
        try (Connection conn = JdbcUtils.getConn()) {
            // Truy vấn để lấy thông tin thiết bị dựa trên id của bản ghi bảo trì
            String sql = "SELECT e.*, s.id AS status_id, s.name AS status_name, s.description AS status_description "+
                    "c.id AS category_id, c.name AS category_name"+
                    "JOIN equipment_maintenance em ON e.id = em.equipment_id "+
                    "WHERE em.id = ?";
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
            String sql = "UPDATE `equipmentma2test`.`equipment_maintenance`\n" +
                    "SET\n" +
                    "`equipment_id` = ?,\n" +
                    "`description` = ?,\n" +
                    "`maintenance_type_id` = ?,\n" +
                    "`price` = ?,\n" +
                    "`maintenance_id` = ?,\n" +
                    "`technical_man_id` = ?,\n" +
                    "`inspection_date` = ?,\n" +
                    "`status_id` = ?\n" +
                    "WHERE `id` = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, em.getEquipmentId());
            stm.setString(2, em.getDescription());
            stm.setInt(3, em.getMaintainanceTypeId());
            stm.setFloat(4, em.getPrice());
            stm.setInt(5, em.getMaintainanceId());
            stm.setInt(6, em.getTechnician().getId());
            // Xử lý chuyển đổi ngày inspection
            if (em.getInspectionDate() != null) {
                stm.setDate(7, new java.sql.Date(em.getInspectionDate().getTime()));
            } else {
                stm.setNull(7, Types.DATE);
            }
            stm.setInt(8,em.getEquipmentMaintenanceStatus().getId());
            stm.setInt(9,em.getId());

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
