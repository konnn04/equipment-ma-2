package com.hatecode.services.impl;

import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.interfaces.MaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceServiceImpl implements MaintenanceService {
    @Override
    public List<Maintenance> getMaintenances() throws SQLException {
        List<Maintenance> maintenances = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM maintenance");

            while (rs.next()) {
                Maintenance maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_datetime").toLocalDateTime(),
                        rs.getTimestamp("end_datetime").toLocalDateTime(),
                        rs.getTimestamp("created_date").toLocalDateTime()
                );
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
            String sql = "SELECT * FROM maintenance WHERE title LIKE ? OR description LIKE ? LIMIT ?, ?";
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
                        rs.getTimestamp("start_datetime").toLocalDateTime(),
                        rs.getTimestamp("end_datetime").toLocalDateTime(),
                        rs.getTimestamp("created_date").toLocalDateTime()
                );
                res.add(maintenance);
            }
        }
        return res;
    }

    @Override
    public List<Maintenance> getMaintenances(String query) throws SQLException {
        List<Maintenance> res = new ArrayList<>();

        if (query == null) {
            query = "";
        }

        String sql = "SELECT * FROM maintenance WHERE 1=1";

        if (!query.isEmpty()) {
            sql += " AND (title LIKE CONCAT('%',?,'%') OR description LIKE CONCAT('%',?,'%')";

            try {
                Integer.parseInt(query);
                sql += " OR id = ?";
            } catch (NumberFormatException e) {
                // Không làm gì nếu query không phải số
            }
            sql += ")";
        }

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (!query.isEmpty()) {
                stm.setString(paramIndex++, query);
                stm.setString(paramIndex++, query);

                try {
                    int id = Integer.parseInt(query);
                    stm.setInt(paramIndex++, id);
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu query không phải số
                }
            }

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Maintenance m = new Maintenance(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("start_datetime").toLocalDateTime(),
                            rs.getTimestamp("end_datetime").toLocalDateTime(),
                            rs.getTimestamp("created_date").toLocalDateTime()
                    );
                    res.add(m);
                }
            }
        }
        return res;
    }



    @Override
    public Maintenance getMantenanceById(int id) throws SQLException {
        Maintenance maintenance = null;
        String sql = "SELECT * FROM Maintenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_datetime").toLocalDateTime(),
                        rs.getTimestamp("end_datetime").toLocalDateTime(),
                        rs.getTimestamp("created_date").toLocalDateTime()
                );
            }
        }

        return maintenance;
    }

    @Override
    public boolean addMantenance(Maintenance maintenance) throws SQLException {
        String sql = "INSERT INTO Maintenance (title, description, start_datetime, end_datetime) VALUES (?, ?, ? ,?)";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateMantenance(Maintenance maintenance) throws SQLException {
        String sql = "UPDATE Maintenance SET title = ?, description = ?, start_datetime = ?, end_datetime = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            stmt.setInt(5, maintenance.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteMantenance(int id) throws SQLException {
        String sql = "DELETE FROM Maintenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        }
    }
}
