/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Image;
import com.hatecode.pojo.Status;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.CategoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<Category> getCategories() throws SQLException {
        List<Category> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM category";
            PreparedStatement stm = conn.prepareCall(sql);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at"): null
                );
                res.add(c);
            }
            return res;
        }
    }

    @Override
    public Category getCategoryById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM category WHERE id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at"): null
                );
                return c;
            }
            return null;
        }
    }

    @Override
    public List<Equipment> getEquipmentByCategory(int id) throws SQLException {
        List<Equipment> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment e " +
                    "WHERE category = ?";
            // Truy vấn để lấy thông tin thiết bị dựa trên id của bản ghi bảo trì
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        Status.fromId(rs.getInt("status")),
                        rs.getInt("category"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at") : null,
                        rs.getInt("image"),
                        rs.getInt("regular_maintenance_day"),
                        rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time") : null,
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                );
                res.add(e);
            }
            return res;
        }
    }

    @Override
    public boolean addCategory(Category cate) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO category (name) VALUES (?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, cate.getName());
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean updateCategory(Category cate) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE category SET name = ?, is_active = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
            stm.setInt(3, cate.getId());

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được cập nhật
        }
    }

    @Override
    public boolean deleteCategory(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM category WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được xóa
        }
    }

}
