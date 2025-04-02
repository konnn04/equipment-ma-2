package com.hatecode.services.impl;

import com.hatecode.utils.JdbcUtils;
import com.hatecode.models.UserMaintenance;
import com.hatecode.services.interfaces.UserMaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserMaintenanceServiceImpl implements UserMaintenanceService {

    @Override
    public List<UserMaintenance> getUserMaintenances() throws SQLException {
        List<UserMaintenance> userMaintenances = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM User_Maintenance");

            while (rs.next()) {
                UserMaintenance userMaintenance = new UserMaintenance(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("maintenance_id")
                );
                userMaintenances.add(userMaintenance);
            }
        }

        return userMaintenances;
    }

    @Override
    public UserMaintenance getUserMaintenanceById(int id) throws SQLException {
        UserMaintenance userMaintenance = null;
        String sql = "SELECT * FROM User_Maintenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userMaintenance = new UserMaintenance(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("maintenance_id")
                );
            }
        }

        return userMaintenance;
    }

    @Override
    public boolean addUserMaintenance(UserMaintenance userMaintenance) throws SQLException {
        String sql = "INSERT INTO User_Maintenance (user_id, maintenance_id) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userMaintenance.getUserId());
            pstmt.setInt(2, userMaintenance.getMaintenanceId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUserMaintenance(UserMaintenance userMaintenance) throws SQLException {
        String sql = "UPDATE User_Maintenance SET user_id = ?, maintenance_id = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userMaintenance.getUserId());
            pstmt.setInt(2, userMaintenance.getMaintenanceId());
            pstmt.setInt(3, userMaintenance.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteUserMaintenance(int id) throws SQLException {
        String sql = "DELETE FROM User_Maintenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}