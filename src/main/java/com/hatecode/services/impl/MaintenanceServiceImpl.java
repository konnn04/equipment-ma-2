package com.hatecode.services.impl;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.MaintenanceStatus;
import com.hatecode.services.UserService;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.MaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceServiceImpl implements MaintenanceService {
    private final Connection externalConn;
    private boolean isConnectTesting = false;
    public MaintenanceServiceImpl() {
        this.externalConn = null;
    }

    public MaintenanceServiceImpl(Connection conn) {
        this.externalConn = conn;
        this.isConnectTesting = true;
    }

    private Connection getConnection() throws SQLException {
        if (externalConn != null) return externalConn;
        return JdbcUtils.getConn();
    }

    public static Maintenance extractMaintenance(ResultSet rs) throws SQLException {
        return new Maintenance(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("start_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private List<Maintenance> getMaintenances(String query, List<Maintenance> res, String sql) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res.add(extractMaintenance(rs));
            }
        }
        if (!isConnectTesting) {
            conn.close();
        }
        return res;
    }

    @Override
    public List<Maintenance> getMaintenances() throws SQLException {
        List<Maintenance> maintenances = new ArrayList<>();
        Connection conn = getConnection();
        String sql = "SELECT * FROM maintenance where is_active=true";
        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                maintenances.add(extractMaintenance(rs));
            }
        }
        if (!isConnectTesting) {
            conn.close();
        }
        return maintenances;
    }

    @Override
    public List<Maintenance> getMaintenances(String query, MaintenanceStatus status) throws SQLException {
        if (query == null || query.isEmpty()) {
            query = "";
        }
        String sql = "SELECT * FROM maintenance  WHERE (title LIKE ? OR description LIKE ?) AND is_active=true AND ";
        List<Maintenance> res = new ArrayList<>();
        switch (status) {
            case PENDING:
                sql += "start_datetime > NOW()";
                break;
            case IN_PROGRESS:
                sql += "start_datetime <= NOW() AND end_datetime >= NOW()";
                break;
            case COMPLETED:
                sql += "end_datetime < NOW()";
                break;
            default:
                throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_INVALID_STATUS);
        }
        return getMaintenances(query, res, sql);
    }

    @Override
    public List<Maintenance> getMaintenances(String query) throws SQLException {
        if (query == null || query.isEmpty()) {
            query = "";
        }
        List<Maintenance> res = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE title LIKE ? OR description LIKE ? AND is_active=true";
        return getMaintenances(query, res, sql);
    }

    @Override
    public Maintenance getMantenanceById(int id) throws SQLException {
        Maintenance maintenance = null;
        String sql = "SELECT * FROM Maintenance WHERE id = ? AND is_active=true";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maintenance = extractMaintenance(rs);
            }
        }
        if (!isConnectTesting) {
            conn.close();
        }
        return maintenance;
    }

    @Override
    public boolean addMantenance(Maintenance maintenance) throws SQLException {
        if (maintenance == null) {
            return false;
        }
        if (maintenance.getTitle() == null || maintenance.getTitle().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_NAME_EMPTY);
        }
        if (maintenance.getStartDateTime().isAfter(maintenance.getEndDateTime())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_INVALID);
        }
        String sql = "INSERT INTO Maintenance (title, description, start_datetime, end_datetime) VALUES (?, ?, ? ,?)";
        Connection conn = getConnection();
        int rowsAffected = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    maintenance.setId(rs.getInt(1));
                }
            }
            if (!isConnectTesting) {
                conn.close();
            }
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateMantenance(Maintenance maintenance) throws SQLException {
        if (maintenance == null) {
            return false;
        }
        if (maintenance.getId() <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_ID_NULL);
        }
        if (maintenance.getTitle() == null || maintenance.getTitle().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_NAME_EMPTY);
        }
        if (maintenance.getStartDateTime().isAfter(maintenance.getEndDateTime())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_INVALID);
        }
        String sql = "UPDATE Maintenance SET title = ?, description = ?, start_datetime = ?, end_datetime = ? WHERE id = ? AND is_active=true";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            stmt.setInt(5, maintenance.getId());
            return stmt.executeUpdate() > 0;
        } finally {
            if (!isConnectTesting) {
                conn.close();
            }
        }
    }

    @Override
    public boolean deleteMantenance(Maintenance maintenance) throws SQLException {
        if (maintenance == null || maintenance.getId() <= 0) {
            return false;
        }
        return deleteMantenanceById(maintenance.getId());
    }

    @Override
    public boolean deleteMantenanceById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_ID_NULL);
        }
        String sql = "UPDATE Maintenance SET is_active = false WHERE id = ? AND is_active=true";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        } finally {
            if (!isConnectTesting) {
                conn.close();
            }
        }
    }
}
