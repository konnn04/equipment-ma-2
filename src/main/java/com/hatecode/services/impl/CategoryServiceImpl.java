/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.CategoryService;
import com.hatecode.utils.OperationResult;

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
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
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
                return new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                );
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
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getInt("image"),
                        rs.getInt("regular_maintenance_day"),
                        rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                );
                res.add(e);
            }
            return res;
        }
    }

    @Override
    public OperationResult addCategory(Category cate) {
        // Check if the category name is empty
        if (cate.getName().trim().isEmpty()) {
            return new OperationResult(false, "Category name cannot be empty");
        }
        // Check if the category name already exists
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO category (name, is_active) VALUES (?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
            int rowsAffected = stm.executeUpdate();
            return new OperationResult(rowsAffected > 0, "Added category successfully");
        } catch (SQLException e) {
            return new OperationResult(false, e.getMessage());
        }
    }

    @Override
    public OperationResult updateCategory(Category cate)  {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE category SET name = ?, is_active = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
            stm.setInt(3, cate.getId());

            int rowsAffected = stm.executeUpdate();
            return new OperationResult(rowsAffected > 0, "Updated category successfully");
        }catch (SQLException e) {
            return new OperationResult(false, e.getMessage());
        }
    }

    @Override
    public OperationResult deleteCategory(int id)  {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM category WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            return new OperationResult(rowsAffected > 0, "Deleted category successfully");
        } catch (SQLException e) {
            return new OperationResult(false, e.getMessage());
        }
    }

}
