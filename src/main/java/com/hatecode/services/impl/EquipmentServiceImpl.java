package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.services.ImageService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.EquipmentService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.hatecode.services.impl.EquipmentMaintenanceServiceImpl.extractEquipmentMaintenance;


public class EquipmentServiceImpl implements EquipmentService {
    // Chuyển RS thành đối tượng Equipment
    public static Equipment extractEquipment(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                Status.fromId(rs.getInt("status")),
                rs.getInt("category_id"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getInt("image_id"),
                rs.getInt("regular_maintenance_day"),
                rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                rs.getString("description"),
                rs.getBoolean("is_active")
        );
    }

    /**
     * Lấy thông tin thiết bị theo ID
     */
    @Override
    public Equipment getEquipmentById(int id) throws SQLException {
        String sql = "SELECT * FROM equipment e " +
                "LEFT JOIN Category c ON e.category_id = c.id " +
                "WHERE e.id = ? AND e.is_active = true";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return extractEquipmentFromResultSet(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new SQLException("Failed to get equipment by ID: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    @Override
    public Equipment getEquipmentByCode(String code) throws SQLException {
        String sql = "SELECT * FROM equipment e " +
                "LEFT JOIN Category c ON e.category_id = c.id " +
                "WHERE e.code = ? AND e.is_active = true";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, code);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return extractEquipmentFromResultSet(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new SQLException("Failed to get equipment by ID: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    /**
     * Lấy tất cả thiết bị từ database
     */
    @Override
    public List<Equipment> getEquipments() throws SQLException {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT e.* FROM equipment e WHERE e.is_active = true";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                equipments.add(extractEquipment(rs));
            }

            return equipments;
        } catch (SQLException e) {
            throw new SQLException("Failed to get all equipments: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    @Override
    public List<Equipment> getEquipments(String query) throws SQLException {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT e.* FROM equipment e WHERE (e.code LIKE ? OR e.name LIKE ?) AND e.is_active = true";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, "%" + query + "%");
            stm.setString(2, "%" + query + "%");

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    equipments.add(extractEquipment(rs));
                }
            }
            return equipments;
        } catch (SQLException e) {
            throw new SQLException("Failed to get equipments: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
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
                    "SELECT e.* FROM equipment e WHERE (e.code LIKE ? OR e.name LIKE ?) AND e.is_active = true ");

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
                        equipments.add(extractEquipment(rs));
                    }
                }
            }
            return equipments;
        } catch (SQLException e) {
            throw new SQLException("Failed to get equipments: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    /**
     * Lấy lịch sử bảo trì của một thiết bị
     */
    @Override
    public List<EquipmentMaintenance> getEquipmentMaintainances(int id) throws SQLException {
        List<EquipmentMaintenance> maintenanceList = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM equipment_maintenance WHERE equipment_id = ? AND is_active = true";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                try (ResultSet rs = stm.executeQuery()) {
                    while (rs.next()) {
                        maintenanceList.add(extractEquipmentMaintenance(rs));
                    }
                }
            }
            return maintenanceList;
        } catch (SQLException e) {
            throw new SQLException("Failed to get equipment maintenance history: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    /**
     * Thêm thiết bị mới (không bao gồm hình ảnh)
     */
    @Override
    public boolean addEquipment(Equipment e) throws SQLException {
        // Validate regularMaintenanceDay
        if (e.getRegularMaintenanceDay() <= 0) {
            throw new SQLException("Regular maintenance day must be greater than zero");
        }

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO Equipment (code, name, status, category_id, regular_maintenance_day, description, image_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, e);
                stm.setInt(7, 1); // Default image_id ID
                if (stm.executeUpdate() > 0)
                    return true;
                else
                    throw new SQLException("Failed to add equipment");
            }
        } catch (SQLException ex) {
            throw new SQLException("Failed to add equipment: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new SQLException("An unexpected error occurred while accessing the database", ex);
        }
    }

    /**
     * Thêm thiết bị mới kèm hình ảnh (sử dụng transaction)
     */
    @Override
    public boolean addEquipment(Equipment equipment, Image image_id) throws SQLException {
        ImageService imageService = new ImageServiceImpl();
        if (equipment.getRegularMaintenanceDay() <= 0) {
            throw new SQLException("Regular maintenance day must be greater than zero");
        }

        Connection conn = null;
        boolean success = false;

        try {
//             Thêm hình ảnh trước --> Gọi service lồng nhau connection bị lỗi vì imageService đóng connection
            imageService.addImage(image_id);

            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);

            // Thêm thiết bị với tham chiếu đến hình ảnh
            String sql = "INSERT INTO Equipment (code, name, status, category_id, regular_maintenance_day, description, image_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, equipment);
                stm.setInt(7, image_id.getId());

                success = stm.executeUpdate() > 0;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new SQLException("Failed to add equipment");
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
        if (e.getRegularMaintenanceDay() <= 0) {
            throw new SQLException("Regular maintenance day must be greater than zero");
        }

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE equipment SET code = ?, name = ?, status = ?, category_id = ?, " +
                    "regular_maintenance_day = ?, description = ?, last_maintenance_time = ?  WHERE id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, e);
                stm.setTimestamp(7, e.getLastMaintenanceTime() != null ? Timestamp.valueOf(e.getLastMaintenanceTime()): null);
                stm.setInt(8, e.getId());

                if (stm.executeUpdate() > 0)
                    return true;
                else
                    throw new SQLException("Failed to update equipment");
            }
        } catch (SQLException ex) {
            throw new SQLException("Failed to update equipment: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new SQLException("An unexpected error occurred while accessing the database", ex);
        }
    }

    /**
     * Cập nhật thông tin thiết bị kèm hình ảnh (sử dụng transaction)
     */
    @Override
    public boolean updateEquipment(Equipment equipment, Image image_id) throws SQLException {
        ImageService imageService = new ImageServiceImpl();
        if (equipment.getRegularMaintenanceDay() <= 0) {
            throw new SQLException("Regular maintenance day must be greater than zero");
        }

        Connection conn = null;
        boolean success = false;

        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);
            // Cập nhật hình ảnh
            imageService.addImage(image_id);
            // Cập nhật thiết bị với tham chiếu đến hình ảnh mới
            String sql = "UPDATE equipment SET code = ?, name = ?, status = ?, category_id = ?, " +
                    "regular_maintenance_day = ?, description = ?, last_maintenance_time = ?, image_id = ? WHERE id = ?";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                setEquipmentStatementParameters(stm, equipment);
                stm.setTimestamp(7, equipment.getLastMaintenanceTime() != null ? Timestamp.valueOf(equipment.getLastMaintenanceTime()): null);
                stm.setInt(8, image_id.getId());
                stm.setInt(9, equipment.getId());

                success = stm.executeUpdate() > 0;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new SQLException("Failed to update equipment");
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

    @Override
    public boolean deleteEquipment(int id) throws SQLException {
        String sql = "UPDATE equipment SET is_active = 0 WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, id);
            if (stm.executeUpdate() > 0)
                return true;
            else
                throw new SQLException("Failed to delete equipment");
        } catch (SQLException e) {
            throw new SQLException("Failed to update equipment is_active status: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    /**
     * Xóa thiết bị theo ID
     */
    @Override
    public boolean hardDeleteEquipment(int id) throws SQLException {
        String sql = "DELETE FROM equipment WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setInt(1, id);
                if (stm.executeUpdate() > 0)
                    return true;
                else
                    throw new SQLException("Failed to delete equipment");
        } catch (SQLException e) {
            throw new SQLException("Failed to delete equipment: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    /**
     * Lấy các giá trị duy nhất của một cột trong bảng equipment
     */
    @Override
    public List<Object> getDistinctValues(String columnName) throws SQLException {
        List<Object> distinctValues = new ArrayList<>();
        String sql = "SELECT DISTINCT " + columnName + " FROM equipment";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                if (columnName.equals("status") || columnName.equals("category_id")) {
                    distinctValues.add(rs.getInt(columnName));
                } else {
                    distinctValues.add(rs.getString(columnName));
                }
            }

            return distinctValues;
        } catch (SQLException e) {
            throw new SQLException("Failed to get distinct values: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    /**
     * Helper method để thiết lập các tham số cho PreparedStatement
     */
    private static void setEquipmentStatementParameters(PreparedStatement stm, Equipment e) throws SQLException {
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
    private static Equipment extractEquipmentFromResultSet(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                Status.fromId(rs.getInt("status")),
                rs.getInt("category_id"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getInt("image_id"),
                rs.getInt("regular_maintenance_day"),
                rs.getTimestamp("last_maintenance_time") != null ? rs.getTimestamp("last_maintenance_time").toLocalDateTime() : null,
                rs.getString("description"),
                rs.getBoolean("is_active")
        );
    }
}
