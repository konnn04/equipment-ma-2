package com.hatecode.services.impl;

import com.hatecode.utils.JdbcUtils;
import com.hatecode.models.MaintenanceType;
import com.hatecode.services.interfaces.MaintenanceTypeService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceTypeServiceImpl implements MaintenanceTypeService {

    @Override
    public List<MaintenanceType> getMaintenanceTypes() throws SQLException {
        List<MaintenanceType> maintenanceTypes = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Maintenance_Type");

            while (rs.next()) {
                MaintenanceType maintenanceType = new MaintenanceType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("suggest_price")
                );
                maintenanceTypes.add(maintenanceType);
            }
        }

        return maintenanceTypes;
    }

    @Override
    public MaintenanceType getMaintenanceTypeById(int id) throws SQLException {
        MaintenanceType maintenanceType = null;
        String sql = "SELECT * FROM Maintenance_Type WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                maintenanceType = new MaintenanceType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("suggest_price")
                );
            }
        }

        return maintenanceType;
    }

    @Override
    public boolean addMaintenanceType(MaintenanceType maintenanceType) throws SQLException {
        String sql = "INSERT INTO Maintenance_Type (name, description, suggest_price) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maintenanceType.getName());
            pstmt.setString(2, maintenanceType.getDescription());
            pstmt.setFloat(3, maintenanceType.getSuggestPrice());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateMaintenanceType(MaintenanceType maintenanceType) throws SQLException {
        String sql = "UPDATE Maintenance_Type SET name = ?, description = ?, suggest_price = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maintenanceType.getName());
            pstmt.setString(2, maintenanceType.getDescription());
            pstmt.setFloat(3, maintenanceType.getSuggestPrice());
            pstmt.setInt(4, maintenanceType.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteMaintenanceType(int id) throws SQLException {
        String sql = "DELETE FROM Maintenance_Type WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}