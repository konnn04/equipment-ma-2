package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.EquipmentService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EquipmentServiceImpl implements EquipmentService {
    private final ImageService imageService = new ImageServiceImpl();

    /**
     * Lấy thông tin thiết bị theo ID
     */
    @Override
    public Equipment getEquipmentById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment e " +
                    "LEFT JOIN Category c ON e.category = c.id " +
                    "WHERE e.id = ?";

            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        return extractEquipmentFromResultSet(rs);
                    }
                }
            }
            return null;
        }
    }

    /**
     * Lấy tất cả thiết bị từ database
     */
    @Override
    public List<Equipment> getEquipments() throws SQLException {
        List<Equipment> equipments = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT e.* FROM equipment e";
            try (PreparedStatement stm = conn.prepareStatement(sql);
                 ResultSet rs = stm.executeQuery()) {

                while (rs.next()) {
                    equipments.add(extractFullEquipmentFromResultSet(rs));
                }
            }
        }
        return equipments;
    }

    /**
     * Tìm kiếm thiết bị theo các tiêu chí
     * @param query Từ khóa tìm kiếm (tìm trong code hoặc name)
     * @param page Trang hiện tại
     * @param pageSize Số lượng bản ghi trên một trang
     * @param key Tên cột để lọc (nếu có)
     * @param value Giá trị để lọc (nếu có)
     */
    @Override
    public List<Equipment> getEquipments(String query, int page, int pageSize, String key, String value) throws SQLException {
        // Chuẩn hóa các tham số đầu vào
        query = query != null ? query.trim() : "";
        page = Math.max(1, page);
        pageSize = Math.max(1, pageSize);
        List<Equipment> equipments = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT e.* FROM equipment e WHERE (e.code LIKE ? OR e.name LIKE ?) ");

            boolean hasFilterCondition = key != null && !key.isEmpty() && value != null && !value.isEmpty();
            if (hasFilterCondition) {
                sqlBuilder.append("AND (").append(key.toLowerCase()).append(" = ?)");
            }
            sqlBuilder.append(" LIMIT ?, ?");

            try (PreparedStatement stm = conn.prepareStatement(sqlBuilder.toString())) {
                // Thiết lập các tham số truy vấn
                stm.setString(1, "%" + query + "%");
                stm.setString(2, "%" + query + "%");

                if (hasFilterCondition) {
                    stm.setString(3, value);
                    stm.setInt(4, (page - 1) * pageSize);
                    stm.setInt(5, pageSize);
                } else {
                    stm.setInt(3, (page - 1) * pageSize);
                    stm.setInt(4, pageSize);
                }

                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        equipments.add(extractFullEquipmentFromResultSet(rs));
                    }
                }
            }
        }
        return equipments;
    }

    /**
     * Lấy lịch sử bảo trì của một thiết bị
     */
    @Override
    public List<EquipmentMaintenance> getEquipmentMaintainances(int id) throws SQLException {
        List<EquipmentMaintenance> maintenanceList = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment_maintenance WHERE equipment_id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        maintenanceList.add(extractMaintenanceFromResultSet(rs));
                    }
                }
            }
        }
        return maintenanceList;
    }

    /**
     * Thêm thiết bị mới (không bao gồm hình ảnh)
     */
    @Override
    public boolean addEquipment(Equipment e) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO Equipment (code, name, status, category, regular_maintenance_day, description, image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, e);
                stm.setInt(7, 1); // Default image ID

                return stm.executeUpdate() > 0;
            }
        }
    }

    /**
     * Thêm thiết bị mới kèm hình ảnh (sử dụng transaction)
     */
    @Override
    public boolean addEquipment(Equipment equipment, Image image) throws SQLException {
        Connection conn = null;
        boolean success = false;

        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);

            // Thêm hình ảnh trước
            imageService.addImage(image);

            // Thêm thiết bị với tham chiếu đến hình ảnh
            String sql = "INSERT INTO Equipment (code, name, status, category, regular_maintenance_day, description, image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, equipment);
                stm.setInt(7, image.getId());

                success = stm.executeUpdate() > 0;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    throw new SQLException("Failed to rollback transaction: " + e.getMessage(), e);
                }
            }
            throw new SQLException("Failed to add equipment: " + ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log this exception but don't throw it
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * Cập nhật thông tin thiết bị
     */
    @Override
    public boolean updateEquipment(Equipment e) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment SET code = ?, name = ?, status = ?, category = ?, " +
                    "regular_maintenance_day = ?, last_maintenance_time = ?, description = ? WHERE id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, e);
                stm.setTimestamp(6, Timestamp.valueOf(e.getLastMaintenanceTime()));
                stm.setInt(8, e.getId());

                return stm.executeUpdate() > 0;
            }
        }
    }

    /**
     * Cập nhật thông tin thiết bị kèm hình ảnh (sử dụng transaction)
     */
    @Override
    public boolean updateEquipment(Equipment equipment, Image image) throws SQLException {
        Connection conn = null;
        boolean success = false;

        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);

            // Cập nhật hình ảnh
            imageService.addImage(image);

            // Cập nhật thiết bị với tham chiếu đến hình ảnh mới
            String sql = "UPDATE equipment SET code = ?, name = ?, status = ?, category = ?, " +
                    "regular_maintenance_day = ?, last_maintenance_time = ?, description = ?, image = ? WHERE id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, equipment);
                stm.setTimestamp(6, Timestamp.valueOf(equipment.getLastMaintenanceTime()));
                stm.setInt(8, image.getId());
                stm.setInt(9, equipment.getId());

                success = stm.executeUpdate() > 0;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    throw new SQLException("Failed to rollback transaction: " + e.getMessage(), e);
                }
            }
            throw new SQLException("Failed to update equipment: " + ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * Xóa thiết bị theo ID
     */
    @Override
    public boolean deleteEquipment(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM equipment WHERE id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                return stm.executeUpdate() > 0;
            }
        }
    }

    /**
     * Lấy các giá trị duy nhất của một cột trong bảng equipment
     */
    @Override
    public List<Object> getDistinctValues(String columnName) throws SQLException {
        List<Object> distinctValues = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT DISTINCT " + columnName + " FROM equipment";
            try (PreparedStatement stm = conn.prepareStatement(sql);
                 ResultSet rs = stm.executeQuery()) {

                while (rs.next()) {
                    if (columnName.equals("status") || columnName.equals("category")) {
                        distinctValues.add(rs.getInt(columnName));
                    } else {
                        distinctValues.add(rs.getString(columnName));
                    }
                }
            }
        }
        return distinctValues;
    }

    /**
     * Helper method để thiết lập các tham số cho PreparedStatement
     */
    private void setEquipmentStatementParameters(PreparedStatement stm, Equipment e) throws SQLException {
        stm.setString(1, e.getCode());
        stm.setString(2, e.getName());
        stm.setInt(3, e.getStatus().getId());
        stm.setInt(4, e.getCategoryId());
        stm.setInt(5, e.getRegularMaintenanceDay());
        stm.setString(6, e.getDescription());
    }

    /**
     * Extract basic equipment data from ResultSet
     */
    private Equipment extractEquipmentFromResultSet(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                Status.fromId(rs.getInt("status")),
                rs.getInt("category"),
                rs.getInt("image"),
                rs.getInt("regular_maintenance_day"),
                rs.getString("description")
        );
    }

    /**
     * Extract full equipment data including dates from ResultSet
     */
    private Equipment extractFullEquipmentFromResultSet(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                Status.fromId(rs.getInt("status")),
                rs.getInt("category"),
                rs.getTimestamp("created_date") != null ? rs.getTimestamp("created_date").toLocalDateTime() : null,
                rs.getInt("image"),
                rs.getInt("regular_maintenance_day"),
                rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                rs.getString("description"),
                rs.getBoolean("is_active")
        );
    }

    /**
     * Extract maintenance data from ResultSet
     */
    private EquipmentMaintenance extractMaintenanceFromResultSet(ResultSet rs) throws SQLException {
        return new EquipmentMaintenance(
                rs.getInt("id"),
                rs.getInt("equipment_id"),
                rs.getInt("maintenance_id"),
                rs.getInt("technician_id"),
                rs.getString("description"),
                Result.fromCode(rs.getInt("result")),
                rs.getString("repair_name"),
                rs.getFloat("repair_price"),
                rs.getTimestamp("inspection_date") != null ? rs.getTimestamp("inspection_date").toLocalDateTime() : null,
                rs.getTimestamp("created_date") != null ? rs.getTimestamp("created_date").toLocalDateTime() : null
        );
    }
}