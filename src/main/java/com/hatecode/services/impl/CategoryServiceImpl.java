package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
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
                rs.getTimestamp("created_at")
        );
    }
    @Override
    public List<Category> getCategories() throws SQLException {
        List<Category> res = new ArrayList<>();
        String sql = "SELECT * FROM category";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    res.add(extractCategory(rs));
                }
            }
        }
        return res;
    }

    @Override
    public List<Category> getCategories(String query) throws SQLException {
        List<Category> res = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE name LIKE ?";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
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
        String sql = "SELECT * FROM category WHERE id = ?";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return extractCategory(rs);
                }
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
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    res.add(EquipmentServiceImpl.extractEquipment(rs));
                }
            }
            return res;
        }
    }

    @Override
    public boolean addCategory(Category cate) throws SQLException {
        // Kiểm tra danh mục không null và tên không rỗng
        if (cate == null || cate.getName() == null || cate.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        // Tiến hành thêm vào CSDL
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO category (name, is_active) VALUES (?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
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
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteCategory(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE is_active = false WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }
    }

//    @Override
//    public boolean hardDeleteCategory(int id) throws SQLException {
//        try (Connection conn = JdbcUtils.getConn()) {
//            String sql = "DELETE FROM category WHERE id = ?";
//            PreparedStatement stm = conn.prepareStatement(sql);
//            stm.setInt(1, id);
//            int rowsAffected = stm.executeUpdate();
//            return rowsAffected > 0;
//        }
//    }

}
