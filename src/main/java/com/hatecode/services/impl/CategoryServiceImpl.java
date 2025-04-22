package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.CategoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    // Chuyển RS thành đối tượng Category
    public static Category extractCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }

    @Override
    public List<Category> getCategories() throws SQLException {
        List<Category> res = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE is_active=true";
        
        try (Connection conn = JdbcUtils.getConn();PreparedStatement stm = conn.prepareStatement(sql);
            ) {
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                res.add(extractCategory(rs));
            }
        }
        return res;
    }

    @Override
    public List<Category> getCategories(String query) throws SQLException {
        if (query == null || query.isEmpty()) {
            query = "";
        }
        List<Category> res = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE name LIKE ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql);) {
            stm.setString(1, "%" + query + "%");
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    res.add(extractCategory(rs));
                }
            }
        } 
        return res;
    }

    @Override
    public Category getCategoryById(int id) throws SQLException {
        String sql = "SELECT * FROM category WHERE id = ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql);) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return extractCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Equipment> getEquipmentByCategory(int id) throws SQLException {
        List<Equipment> res = new ArrayList<>();

        String sql = "SELECT * FROM equipment e " +
                "WHERE category_id = ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql);) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    res.add(EquipmentServiceImpl.extractEquipment(rs));
                }
            }
        }
        return res;
    }

    @Override
    public boolean addCategory(Category cate) throws SQLException {
        if (cate == null || cate.getName() == null || cate.getName().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.CATEGORY_NAME_EMPTY);
        }
        // Tiến hành thêm vào CSDL
        String sql = "INSERT INTO category (name, is_active) VALUES (?, ?)";
        int rowsAffected = 0;
        try (Connection conn = JdbcUtils.getConn();
                PreparedStatement stm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
            rowsAffected = stm.executeUpdate();
            try (ResultSet rs = stm.getGeneratedKeys()) {
                if (rs.next()) {
                    cate.setId(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Unique index or primary key violation")) {
                throw new SQLException(ExceptionMessage.CATEGORY_NAME_DUPLICATE);
            }
            throw e;
        }
        return rowsAffected > 0;
    }

    @Override
    public boolean updateCategory(Category cate) throws SQLException {
        if (cate == null || cate.getName() == null || cate.getName().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.CATEGORY_NAME_EMPTY);
        }
        String checkSql = "SELECT * FROM category WHERE name = ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, cate.getName());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    throw new SQLException(ExceptionMessage.CATEGORY_NAME_DUPLICATE);
                }
            }
        }

        // Tiến hành cập nhật vào CSDL
        String sql = "UPDATE category SET name = ?, is_active = ? WHERE id = ? AND is_active=true";
        int rowsAffected = 0;
        try (Connection conn = JdbcUtils.getConn();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
            stm.setInt(3, cate.getId());
            rowsAffected = stm.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException(ExceptionMessage.CATEGORY_NAME_DUPLICATE);
            }
            throw e;
        }
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteCategory(int id) throws SQLException {
        String sql = "DELETE FROM category WHERE id = ? AND is_active=true";

        int rowsAffected = 0;
        try (Connection conn = JdbcUtils.getConn();
                PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, id);
            rowsAffected = stm.executeUpdate();
        }
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteCategory(Category cate) throws SQLException {
        if (cate == null) {
            throw new IllegalArgumentException(ExceptionMessage.CATEGORY_NULL);
        }
        if (cate.getId() <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.CATEGORY_ID_NULL);
        }
        return this.deleteCategory(cate.getId());
    }
}
