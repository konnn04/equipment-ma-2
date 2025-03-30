package com.hatecode.services.impl;

import com.hatecode.pojo.JdbcUtils;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.MaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceServiceImpl implements MaintenanceService {
    @Override
    public List<Maintenance> getMaintenances() throws SQLException {
        List<Maintenance> maintenances = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Mantenance");

            while (rs.next()) {
                Maintenance maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("start_datetime"),
                        rs.getString("end_datetime"),
                        rs.getInt("quantity")
                );
                maintenances.add(maintenance);
            }
        }

        return maintenances;
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
                        rs.getString("start_datetime"),
                        rs.getString("end_datetime"),
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
            stmt.setString(3, maintenance.getStartDatetime());
            stmt.setString(4, maintenance.getEndDatetime());
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
            stmt.setString(3, maintenance.getStartDatetime());
            stmt.setString(4, maintenance.getEndDatetime());
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
