package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.CategoryService;

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
    public List<Category> getCategories(String query) throws SQLException {
        List<Category> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM category WHERE name LIKE ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, "%" + query + "%");
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
    public boolean addCategory(Category cate) {
        if (cate == null) {
            return false;
        }
        // Kiểm tra tên danh mục không rỗng
        if (cate.getName() == null || cate.getName().isEmpty()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement checkStm = null;
        PreparedStatement insertStm = null;
        ResultSet rs = null;

        try {
            conn = JdbcUtils.getConn();
            // Bắt đầu transaction
            conn.setAutoCommit(false);

//            // Kiểm tra tên danh mục đã tồn tại chưa
//            String checkSql = "SELECT COUNT(*) FROM category WHERE name = ?";
//            checkStm = conn.prepareStatement(checkSql);
//            checkStm.setString(1, cate.getName());
//            rs = checkStm.executeQuery();
//
//            if (rs.next()) {
//                int count = rs.getInt(1);
//                if (count > 0) {
//                    // Tên danh mục đã tồn tại, không thực hiện thêm mới
//                    return false;
//                }
//            }

            // Thêm danh mục mới
            String insertSql = "INSERT INTO category (name, is_active) VALUES (?, ?)";
            insertStm = conn.prepareStatement(insertSql);
            insertStm.setString(1, cate.getName());
            insertStm.setBoolean(2, cate.isActive() != null ? cate.isActive() : true);

            int rowsAffected = insertStm.executeUpdate();
            // Nếu thêm thành công, commit transaction
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback khi có lỗi
                }
            } catch (SQLException rollbackEx) {
                // Ghi log lỗi rollback
                rollbackEx.printStackTrace();
            }
            // Nên sử dụng logger thay vì in stack trace
            e.printStackTrace();
            return false;
        }finally {
            try {
                // Đóng tất cả tài nguyên theo thứ tự ngược
                if (rs != null) rs.close();
                if (checkStm != null) checkStm.close();
                if (insertStm != null) insertStm.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Khôi phục trạng thái auto-commit
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }

    }

    @Override
    public boolean updateCategory(Category cate)  {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE category SET name = ?, is_active = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, cate.getName());
            stm.setBoolean(2, cate.isActive());
            stm.setInt(3, cate.getId());

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        }catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCategory(int id)  {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM category WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
