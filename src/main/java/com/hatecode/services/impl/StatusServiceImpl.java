package com.hatecode.services.impl;

import com.hatecode.pojo.JdbcUtils;
import com.hatecode.pojo.Status;
import com.hatecode.services.StatusService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusServiceImpl implements StatusService {

    @Override
    public List<Status> getStatuses() throws SQLException {
        List<Status> statuses = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Status");

            while (rs.next()) {
                Status status = new Status(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                statuses.add(status);
            }
        }

        return statuses;
    }

    @Override
    public Status getStatusById(int id) throws SQLException {
        Status status = null;
        String sql = "SELECT * FROM Status WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                status = new Status(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
            }
        }

        return status;
    }

    @Override
    public boolean addStatus(Status status) throws SQLException {
        String sql = "INSERT INTO Status (name, description) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.getName());
            pstmt.setString(2, status.getDescription());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Status status) throws SQLException {
        String sql = "UPDATE Status SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.getName());
            pstmt.setString(2, status.getDescription());
            pstmt.setInt(3, status.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteStatus(int id) throws SQLException {
        String sql = "DELETE FROM Status WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}