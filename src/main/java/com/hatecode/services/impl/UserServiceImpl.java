package com.hatecode.services.impl;

//import com.hatecode.pojo.Image;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.Role;
import com.hatecode.utils.ExtractImageIdUtils;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.User;

import java.io.File;

import com.hatecode.services.interfaces.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    private final CloundinaryServicesImpl cloudiServices = new CloundinaryServicesImpl();

    @Override
    public List<User> getUsers(String kw, int roleId) throws SQLException {
        List<User> users = new ArrayList<>();

        if (kw == null) kw = "";

        String sql = "SELECT u.*, i.id as avatar_id,i.filename,i.create_date,i.path, r.name as role_name, r.description as role_description " +
                "FROM user u JOIN image i ON u.avatar = i.id JOIN role r ON u.role = r.id " +
                "WHERE 1=1 ";

        // Thêm điều kiện tìm kiếm
        if (!kw.isEmpty()) {
            sql += "AND (u.username LIKE CONCAT('%', ?, '%') ";

            try {
                Integer.parseInt(kw); // Kiểm tra nếu kw là số
                sql += "OR u.id = ? ";
            } catch (NumberFormatException e) {
                sql += "OR FALSE "; // Không tìm theo ID nếu kw không phải số
            }
            sql += ") ";
        }

        // Thêm điều kiện role
        if (roleId != 0) {
            sql += "AND u.role = ? ";
        }

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (!kw.isEmpty()) {
                stm.setString(paramIndex++, kw);
                try {
                    Integer.parseInt(kw);
                    stm.setInt(paramIndex++, Integer.parseInt(kw));
                } catch (NumberFormatException ignored) {
                    // Bỏ qua nếu kw không phải số
                }
            }

            if (roleId != 0) {
                stm.setInt(paramIndex, roleId);
            }

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            new Role(
                                    rs.getInt("role"),
                                    rs.getString("role_name"),
                                    rs.getString("role_description")
                            ),
                            rs.getBoolean("is_active"),
                            new Image(
                                    rs.getInt("avatar_id"),
                                    rs.getString("filename"),
                                    rs.getDate("create_date").toLocalDate().atStartOfDay(),
                                    rs.getString("path")
                            )
                    );
                    user.setRoleName(rs.getString("role_name"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT u.*, i.*, r.name as role_name,r.description as role_description\n" +
                "FROM user u\n" +
                "JOIN image i\n" +
                "ON u.avatar = i.id\n" +
                "JOIN role r\n" +
                "ON u.role = r.id " +
                "WHERE u.id = ?";

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
                    new Role(
                            rs.getInt("role"),
                            rs.getString("role_name"),
                            rs.getString("role_description")
                    ),
                    rs.getBoolean("is_active"),
                    new Image(
                            rs.getString("filename"),
                            rs.getDate("create_date").toLocalDate().atStartOfDay(),
                            rs.getString("path")
                    )
            );
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        String sql = "SELECT u.*,r.name as role_name,r.description as role_description\n" +
                "FROM user u\n" +
                "JOIN role r\n" +
                "ON u.role = r.id\n" +
                "WHERE u.username = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            user = getUser(user, pstmt);
        }

        return user;
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        Connection conn = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm Image trước
            if (user.getAvatar() != null) {
                String sqlImage = "INSERT INTO image (filename, create_date, path) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlImage, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, user.getAvatar().getFilename());
                    pstmt.setTimestamp(2, Timestamp.valueOf(user.getAvatar().getCreateDate()));
                    pstmt.setString(3, user.getAvatar().getPath());

                    pstmt.executeUpdate();

                    // Lấy ID vừa tạo
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            user.getAvatar().setId(rs.getInt(1));
                        }
                    }
                }
            }

            // 2. Thêm User
            String sqlUser = "INSERT INTO user (first_name, last_name, username, password, email, phone, role, is_active, avatar) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUser)) {
                pstmt.setString(1, user.getFirstName());
                pstmt.setString(2, user.getLastName());
                pstmt.setString(3, user.getUsername());
                pstmt.setString(4, user.getPassword());
                pstmt.setString(5, user.getEmail());
                pstmt.setString(6, user.getPhone());
                pstmt.setInt(7, user.getRole().getId());
                pstmt.setBoolean(8, user.isActive());

                // Set image_id hoặc null
                if (user.getAvatar() != null) {
                    pstmt.setInt(9, user.getAvatar().getId());
                } else {
                    pstmt.setInt(9, 1);
                }

                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback nếu có lỗi
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
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
            pstmt.setInt(6, user.getRole().getId());
            pstmt.setBoolean(7, user.isActive());
            pstmt.setInt(8, user.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user, Image newImage) throws SQLException {
        Connection conn = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false); // Bắt đầu transaction
            int newImgId = 1;
            if (newImage != null) {
                String sqlImage = "";
                if (newImage.getId() != 0) {
                    sqlImage = "UPDATE image SET filename = ?, create_date = ?, path = ? WHERE id = ?";
                    this.deleteUserImage(user.getAvatar().getPath());
                } else {
                    sqlImage = "INSERT INTO image (filename, create_date, path) VALUES (?, ?, ?)";
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sqlImage, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, newImage.getFilename());
                    pstmt.setTimestamp(2, Timestamp.valueOf(newImage.getCreateDate()));
                    pstmt.setString(3, newImage.getPath());
                    if (newImage.getId() != 0) {
                        pstmt.setInt(4, newImage.getId());
                    }
                    pstmt.executeUpdate();

                    if (newImage.getId() == 0)
                        try (ResultSet rs = pstmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                newImgId = rs.getInt(1);
                            }
                        }
                    else
                        newImgId = user.getAvatar().getId();
                }
            }
            String sql = "UPDATE User SET first_name = ?, last_name = ?,username = ?, password = ?, " +
                    "email = ?, phone = ?, role = ?, is_active = ?, avatar = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getFirstName());
                pstmt.setString(2, user.getLastName());
                pstmt.setString(3, user.getUsername());
                pstmt.setString(4, user.getPassword());
                pstmt.setString(5, user.getEmail());
                pstmt.setString(6, user.getPhone());
                pstmt.setInt(7, user.getRole().getId());
                pstmt.setBoolean(8, user.isActive());
                pstmt.setInt(9, newImgId);
                pstmt.setInt(10, user.getId());
                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback nếu có lỗi
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM User WHERE id = ?";
        User u = getUserById(id);
        boolean b = deleteUserImage(u.getAvatar().getPath());

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return (pstmt.executeUpdate() > 0 && b);
        }
    }

    // https://res.cloudinary.com/dg66aou8q/image/upload/v1743086605/dysaruyl1ye7xejpakbp.png
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

    @Override
    public String getUserImage(User user) {
        try {
            String imgUrl = cloudiServices.getImageUrl(user.getAvatar().getPath());
            return imgUrl;
        } catch (SQLException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String uploadUserImage(File imageFile) {
        try {
            return cloudiServices.uploadImage(imageFile);
        } catch (SQLException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean deleteUserImage(String publicId) {
        try {
            return cloudiServices.deleteImage(publicId);
        } catch (SQLException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


}