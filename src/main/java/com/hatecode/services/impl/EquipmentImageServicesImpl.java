/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentImage;
import com.hatecode.pojo.Image;
import com.hatecode.pojo.JdbcUtils;
import com.hatecode.services.EquipmentImageServices;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class EquipmentImageServicesImpl implements EquipmentImageServices{
    
    @Override
    public List<Image> getEquipmentImage(int id) throws SQLException {
        List<Image> res = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            String sql = "SELECT i.* FROM image i JOIN equipment_image on i.id = ei.image_id WHERE ei.equipment_id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                Image i = new Image(
                        rs.getInt("id"),
                        rs.getString("filename"),
                        rs.getTimestamp("create_date").toLocalDateTime(),
                        rs.getString("path")
                );
                res.add(i);
            }
            return res;
        }
    }
    
    @Override
    public boolean addEquipmentImage(EquipmentImage equipmentImage) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO equipment_image (equipment_id, image_id) VALUES (?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, equipmentImage.getEquipmentId());
            stm.setInt(2, equipmentImage.getImageId());

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean updateEquipmentImage(EquipmentImage equipmentImage) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment_image SET equipment_id = ?, image_id = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, equipmentImage.getEquipmentId());
            stm.setInt(2, equipmentImage.getImageId());
            stm.setInt(3, equipmentImage.getId());

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteEquipmentImage(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM equipment_image WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }
    }

    
}
