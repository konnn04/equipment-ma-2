package com.hatecode.services.impl;

import com.hatecode.services.interfaces.UserMaintenanceService;
import com.hatecode.services.interfaces.UserService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.interfaces.MaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MaintenanceServiceImpl implements MaintenanceService {
    UserService us = new UserServiceImpl();

    @Override
    public List<Maintenance> getMaintenances() throws SQLException {
        List<Maintenance> maintenances = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM maintenance JOIN user_maintenance ON maintenance.id = user_maintenance.maintenance_id");

            while (rs.next()) {
                Maintenance maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("start_datetime"),
                        rs.getDate("end_datetime"),
                        rs.getInt("quantity")
                );
                maintenance.setTechnician(us.getUserById(rs.getInt("user_id")));
                maintenances.add(maintenance);
            }
        }
        return maintenances;
    }

    @Override
    public List<Maintenance> getMaintenances(String query, int page, int pageSize, String key, String value) throws SQLException {
        /* Kiểm tra và xử lý các tham số đầu vào */
        query = query.trim();
        if (query == null || query.isEmpty()) query = "";
        page = Math.max(1, page);
        pageSize = Math.max(1, pageSize);
        List<Maintenance> res = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM maintenance JOIN user_maintenance ON maintenance.id = user_maintenance.maintenance_id WHERE title LIKE ? OR description LIKE ? LIMIT ?, ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setInt(3, (page - 1) * pageSize);
            stmt.setInt(4, pageSize);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Maintenance maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("start_datetime"),
                        rs.getDate("end_datetime"),
                        rs.getInt("quantity")
                );
                maintenance.setTechnician(us.getUserById(rs.getInt("user_id")));
                res.add(maintenance);
            }
        }
        return res;
    }

    @Override
    public List<Maintenance> getMaintenances(String kw, Date fromDate, Date toDate, int page, int pageSize) throws SQLException {
        /* Kiểm tra và xử lý các tham số đầu vào */
        if (kw == null || kw.isEmpty())
            kw = "";
        page = Math.max(1, page);
        pageSize = Math.max(1, pageSize);

        List<Maintenance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM maintenance JOIN user_maintenance ON maintenance.id = user_maintenance.maintenance_id";

            boolean hasKw = kw != null && !kw.isEmpty();
            boolean hasDate = fromDate != null && toDate != null;

            if (hasKw)
                sql += " WHERE title LIKE ? OR description LIKE ?";

            if (hasDate) {
                sql += " AND start_datetime >= ? AND end_datetime <= ?";
            }

            sql += " LIMIT ?, ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            int index = 1;
            if (hasKw) {
                stmt.setString(index++, "%" + kw + "%");
                stmt.setString(index++, "%" + kw + "%");
            }

            if (hasDate) {
                stmt.setDate(index++, new java.sql.Date(fromDate.getTime()));
                stmt.setDate(index++, new java.sql.Date(toDate.getTime()));
            }

            stmt.setInt(index++, (page - 1) * pageSize);
            stmt.setInt(index++, pageSize);

            System.out.println(stmt.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Maintenance maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("start_datetime"),
                        rs.getDate("end_datetime"),
                        rs.getInt("quantity")
                );
                maintenance.setTechnician(us.getUserById(rs.getInt("user_id")));
                res.add(maintenance);
            }
        }
        return res;
    }

    @Override
    public Maintenance getMantenanceById(int id) throws SQLException {
        Maintenance maintenance = null;
        String sql = "SELECT * FROM Mantenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("start_datetime"),
                        rs.getDate("end_datetime"),
                        rs.getInt("quantity")
                );
            }
        }

        return maintenance;
    }

    @Override
    public boolean addMantenance(Maintenance maintenance) throws SQLException {
        String sql = "INSERT INTO Mantenance (title, description, start_datetime, end_datetime, quantity) VALUES (?, ?, ? ,? ,?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setDate(3, new java.sql.Date(maintenance.getStartDatetime().getTime()));
            stmt.setDate(4, new java.sql.Date(maintenance.getEndDatetime().getTime()));
            stmt.setInt(5, maintenance.getQuantity());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateMantenance(Maintenance maintenance) throws SQLException {
        String sql = "UPDATE Mantenance SET title = ?, description = ?, start_datetime = ?, end_datetime = ?, quantity = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setDate(3, new java.sql.Date(maintenance.getStartDatetime().getTime()));
            stmt.setDate(4, new java.sql.Date(maintenance.getEndDatetime().getTime()));
            stmt.setInt(5, maintenance.getQuantity());
            stmt.setInt(6, maintenance.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteMantenance(int id) throws SQLException {
        String sql = "DELETE FROM Mantenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        }
    }
}
