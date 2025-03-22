package com.hatecode.services.impl;

import com.hatecode.pojo.JdbcUtils;
import com.hatecode.pojo.User;
import com.hatecode.services.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM User");

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("role"),
                        rs.getBoolean("is_active")
                );
                users.add(user);
            }
        }

        return users;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM User WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            user = getUser(user, pstmt);
        }

        return user;
    }

    private User getUser(User user, PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("role"),
                    rs.getBoolean("is_active")
            );
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM User WHERE username = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            user = getUser(user, pstmt);
        }

        return user;
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO User (first_name, last_name, username, password, email, phone, role, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPhone());
            pstmt.setInt(7, user.getRole());
            pstmt.setBoolean(8, user.isActive());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET first_name = ?, last_name = ?, password = ?, " +
                "email = ?, phone = ?, role = ?, is_active = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setInt(6, user.getRole());
            pstmt.setBoolean(7, user.isActive());
            pstmt.setInt(8, user.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM User WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM User WHERE username = ? AND password = ? AND is_active = 1";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        }
    }
}