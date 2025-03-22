package com.hatecode.services.impl;

import com.hatecode.pojo.JdbcUtils;
import com.hatecode.pojo.User_Maintenance;
import com.hatecode.services.UserMaintenanceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserMaintenanceServiceImpl implements UserMaintenanceService {

    @Override
    public List<User_Maintenance> getUserMaintenances() throws SQLException {
        List<User_Maintenance> userMaintenances = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM User_Maintenance");

            while (rs.next()) {
                User_Maintenance userMaintenance = new User_Maintenance(
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
    public User_Maintenance getUserMaintenanceById(int id) throws SQLException {
        User_Maintenance userMaintenance = null;
        String sql = "SELECT * FROM User_Maintenance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userMaintenance = new User_Maintenance(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("maintenance_id")
                );
            }
        }

        return userMaintenance;
    }

    @Override
    public boolean addUserMaintenance(User_Maintenance userMaintenance) throws SQLException {
        String sql = "INSERT INTO User_Maintenance (user_id, maintenance_id) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userMaintenance.getUserId());
            pstmt.setInt(2, userMaintenance.getMaintenanceId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUserMaintenance(User_Maintenance userMaintenance) throws SQLException {
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