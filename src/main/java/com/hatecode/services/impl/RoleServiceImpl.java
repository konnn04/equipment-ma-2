package com.hatecode.services.impl;

import com.hatecode.utils.JdbcUtils;
import com.hatecode.models.Role;
import com.hatecode.services.interfaces.RoleService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleServiceImpl implements RoleService {

    @Override
    public List<Role> getRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Role");

            while (rs.next()) {
                Role role = new Role(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                roles.add(role);
            }
        }

        return roles;
    }

    @Override
    public Role getRoleById(int id) throws SQLException {
        Role role = null;
        String sql = "SELECT * FROM Role WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                role = new Role(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
            }
        }

        return role;
    }

    @Override
    public boolean addRole(Role role) throws SQLException {
        String sql = "INSERT INTO Role (name, description) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.getName());
            pstmt.setString(2, role.getDescription());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateRole(Role role) throws SQLException {
        String sql = "UPDATE Role SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.getName());
            pstmt.setString(2, role.getDescription());
            pstmt.setInt(3, role.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteRole(int id) throws SQLException {
        String sql = "DELETE FROM Role WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}