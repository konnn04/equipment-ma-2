package com.hatecode.services.impl;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.Role;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.User;
import com.hatecode.services.interfaces.UserService;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    private final CloundinaryServicesImpl cloudinaryService = new CloundinaryServicesImpl();

    @Override
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, " +
                "i.id AS avatar_id, i.filename AS avatar_filename, i.created_date AS avatar_created_date, i.path AS avatar_path " +
                "FROM User u " +
                "LEFT JOIN Image i ON u.avatar = i.id";

        try (Connection conn = JdbcUtils.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT u.*, " +
                "i.id AS avatar_id, i.filename AS avatar_filename, i.created_date AS avatar_created_date, i.path AS avatar_path " +
                "FROM User u " +
                "LEFT JOIN Image i ON u.avatar = i.id " +
                "WHERE u.id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, " +
                "i.id AS avatar_id, i.filename AS avatar_filename, i.created_date AS avatar_created_date, i.path AS avatar_path " +
                "FROM User u " +
                "LEFT JOIN Image i ON u.avatar = i.id " +
                "WHERE u.username = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO User (first_name, last_name, username, password, email, phone, role, is_active, avatar) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPhone());
            pstmt.setInt(7, user.getRole().getId());
            pstmt.setBoolean(8, user.isActive());
            
            // Set avatar if exists
            if (user.getAvatar() != null) {
                pstmt.setInt(9, user.getAvatar().getId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET first_name = ?, last_name = ?, password = ?, " +
                "email = ?, phone = ?, role = ?, is_active = ?, avatar = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setInt(6, user.getRole().getId());
            pstmt.setBoolean(7, user.isActive());
            
            // Set avatar if exists
            if (user.getAvatar() != null) {
                pstmt.setInt(8, user.getAvatar().getId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setInt(9, user.getId());

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
    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT u.*, " +
                "i.id AS avatar_id, i.filename AS avatar_filename, i.created_date AS avatar_created_date, i.path AS avatar_path " +
                "FROM User u " +
                "LEFT JOIN Image i ON u.avatar = i.id " +
                "WHERE u.username = ? AND u.password = ? AND u.is_active = TRUE";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public String getUserImage(User user) {
        if (user.getAvatar() == null || user.getAvatar().getPath() == null) {
            return null;
        }
        
        try {
            return cloudinaryService.getImageUrl(user.getAvatar().getPath());
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting user image", ex);
            return null;
        }
    }

    @Override
    public String uploadUserImage(File imageFile) {
        try {
            return cloudinaryService.uploadImage(imageFile);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error uploading user image", ex);
            return null;
        }
    }

    @Override
    public boolean deleteUserImage(String publicId) {
        try {
            return cloudinaryService.deleteImage(publicId);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting user image", ex);
            return false;
        }
    }

    /**
     * Helper method to extract User object from ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        // Lấy thông tin role
        Role role = Role.fromId(rs.getInt("role"));

        // Lấy thông tin avatar nếu có
        Image avatar = null;
        int avatarId = rs.getInt("avatar_id");
        if (!rs.wasNull()) {
            avatar = new Image(
                    avatarId,
                    rs.getString("avatar_filename"),
                    rs.getTimestamp("avatar_created_date") != null ? 
                        rs.getTimestamp("avatar_created_date").toLocalDateTime() : null,
                    rs.getString("avatar_path")
            );
        }

        // Lấy created_date
        LocalDateTime createdDate = rs.getTimestamp("created_date") != null ? 
                rs.getTimestamp("created_date").toLocalDateTime() : null;

        // Tạo và trả về đối tượng User
        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("phone"),
                role,
                rs.getBoolean("is_active"),
                avatar,
                createdDate
        );
    }
}