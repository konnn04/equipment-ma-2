package com.hatecode.services.impl;

import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.MaintenanceRepairSuggestion;
import com.hatecode.services.MaintenanceRepairSuggestionService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRepairSuggestionImpl implements MaintenanceRepairSuggestionService {

    @Override
    public List<MaintenanceRepairSuggestion> getMaintenanceTypes() throws SQLException {
        List<MaintenanceRepairSuggestion> maintenanceRepairSuggestions = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Maintenance_Repair_Suggestion");

            while (rs.next()) {
                MaintenanceRepairSuggestion maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("suggest_price"),
                        rs.getTimestamp("created_at")
                );
                maintenanceRepairSuggestions.add(maintenanceRepairSuggestion);
            }
        }

        return maintenanceRepairSuggestions;
    }

    @Override
    public MaintenanceRepairSuggestion getMaintenanceTypeById(int id) throws SQLException {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = null;
        String sql = "SELECT * FROM Maintenance_Repair_Suggestion WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                maintenanceRepairSuggestion = new MaintenanceRepairSuggestion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("suggest_price"),
                        rs.getTimestamp("created_at")
                );
            }
        }

        return maintenanceRepairSuggestion;
    }

    @Override
    public boolean addMaintenanceType(MaintenanceRepairSuggestion maintenanceRepairSuggestion) throws SQLException {
        String sql = "INSERT INTO Maintenance_Repair_Suggestion (name, description, suggest_price) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maintenanceRepairSuggestion.getName());
            pstmt.setString(2, maintenanceRepairSuggestion.getDescription());
            pstmt.setFloat(3, maintenanceRepairSuggestion.getSuggestPrice());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateMaintenanceType(MaintenanceRepairSuggestion maintenanceRepairSuggestion) throws SQLException {
        String sql = "UPDATE Maintenance_Repair_Suggestion SET name = ?, description = ?, suggest_price = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maintenanceRepairSuggestion.getName());
            pstmt.setString(2, maintenanceRepairSuggestion.getDescription());
            pstmt.setFloat(3, maintenanceRepairSuggestion.getSuggestPrice());
            pstmt.setInt(4, maintenanceRepairSuggestion.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteMaintenanceType(int id) throws SQLException {
        String sql = "DELETE FROM Maintenance_Repair_Suggestion WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}