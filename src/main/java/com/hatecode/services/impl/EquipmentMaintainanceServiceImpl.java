/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.services.interfaces.*;
import com.hatecode.utils.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.hatecode.config.AppConfig.PAGE_SIZE;

/**
 * @author ADMIN
 */
public class EquipmentMaintainanceServiceImpl implements EquipmentMaintainanceService {

    UserService us = new UserServiceImpl();
    EquipmentService es = new EquipmentServiceImpl();
    MaintenanceTypeService mts = new MaintenanceTypeServiceImpl();
    StatusService statusService = new StatusServiceImpl();


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
                        es.getEquipmentById(rs.getInt("equipment_id")),
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
    public Equipment getEquipmentByEMId(int id) throws SQLException {
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
    public List<EquipmentMaintainance> getEquipmentsMaintenanceByEMId(String kw, int maintenanceId, int page, int pageSize) throws SQLException {
        if (page < 1)
            page = 1;


        List<EquipmentMaintainance> maintainanceEquipments = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            // Truy vấn để lấy thông tin thiết bị dựa trên id của bản ghi bảo trì
            String sql = "SELECT em.* FROM equipment_maintenance em JOIN equipment e ON em.equipment_id = e.id WHERE em.maintenance_id = ?";

            if (kw != null && !kw.isEmpty()) {
                sql += " AND (e.name LIKE ?)";
            }
            sql +=  " LIMIT ?, ?";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, maintenanceId);
            if (kw != null && !kw.isEmpty()) {
                stm.setString(2, "%" + kw + "%");
                stm.setInt(3, (page - 1) * pageSize);
                stm.setInt(4, pageSize);
            } else {
                stm.setInt(2, (page - 1) * pageSize);
                stm.setInt(3, pageSize);
            }

//            System.out.println(stm.toString());
            ResultSet rs = stm.executeQuery();

            // Nếu có kết quả, tạo đối tượng Equipment từ dữ liệu trả về
            if (rs.next()) {
                EquipmentMaintainance maintainanceEquipment = new EquipmentMaintainance(
                           rs.getInt("id"),
                            es.getEquipmentById(rs.getInt("equipment_id")),
                            rs.getString("description"),
                            mts.getMaintenanceTypeById(rs.getInt("maintenance_type_id")),
                            rs.getFloat("price"),
                            rs.getInt("maintenance_id")
                );

                maintainanceEquipments.add(maintainanceEquipment);
            }
        }
        return maintainanceEquipments;
    }

    @Override
    public boolean addEquipmentMaintainance(EquipmentMaintainance em) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO equipment_maintenance (equipment_id, description, maintenance_type_id, price, maintenance_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, em.getEquipment().getId());
            stm.setString(2, em.getDescription());
            stm.setInt(3, em.getMaintenanceType().getId());
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
            stm.setInt(1, em.getEquipment().getId());
            stm.setString(2, em.getDescription());
            stm.setInt(3, em.getMaintenanceType().getId());
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
