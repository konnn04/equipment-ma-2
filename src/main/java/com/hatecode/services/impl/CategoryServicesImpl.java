/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.JdbcUtils;
import com.hatecode.services.CategoryServices;
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
public class CategoryServicesImpl implements CategoryServices {

    @Override
    public List<Category> getCategory() throws SQLException {
        List<Category> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM category";
            PreparedStatement stm = conn.prepareCall(sql);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("id"), rs.getString("name"));
                res.add(c);
            }
            return res;
        }
    }

    @Override
    public List<Equipment> getEquipmentByCategory(int id) throws SQLException {
        List<Equipment> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment WHERE category = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, id);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Equipment e = new Equipment(rs.getInt("id"), rs.getString("code"), rs.getString("name"), rs.getDate("import_date"), rs.getInt("status"), rs.getInt("category"));
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
            return rowsAffected > 0; // Trả về true nếu có ít nhất một dòng được thêm vào
        }
    }

    @Override
    public boolean updateCategory(Category cate) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE category SET name = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, cate.getName());
            stm.setInt(2, cate.getId());

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
