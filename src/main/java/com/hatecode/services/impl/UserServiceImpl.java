package com.hatecode.services.impl;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.Role;
import com.hatecode.services.ImageService;
import com.hatecode.utils.EmailValidator;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.User;

import java.io.File;

import com.hatecode.services.UserService;
import com.hatecode.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    private final CloudinaryServiceImpl cloudiServices = new CloudinaryServiceImpl();
    
    public static User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("phone"),
                Role.fromId(rs.getInt("role")),
                rs.getBoolean("is_active"),
                rs.getInt("avatar_id")
        );
    }

    @Override
    public List<User> getUsers(String kw, int roleId) throws SQLException {
        List<User> users = new ArrayList<>();
        if (kw == null) {
            kw = "";
        }
        String sql = "SELECT u.* FROM user u WHERE 1=1 ";
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

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {

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
                    User user = extractUser(rs);
                    users.add(user);
                }
            }
        }
        return users;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT u.*, i.* "
                + "FROM `user` u "
                + "JOIN `user` i "
                + "ON u.avatar_id = i.id "
                + "WHERE u.id = ? ";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            user = getUser(user, pstmt);
        }

        return user;
    }

    private User getUser(User user, PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            user = extractUser(rs);
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM `User` WHERE username = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            user = getUser(user, pstmt);
        }

        return user;
    }

    @Override
    public boolean addUser(User user, Image image) throws SQLException {
        if (EmailValidator.isValidEmail(user.getEmail()) == false
                || user.getRole() == null
                || user.getFirstName() == null
                || user.getLastName() == null
                || user.getPhone() == null
                || user.getPassword() == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false); // Bắt đầu transaction
            if (image != null) { // Trường hợp có chọn ảnh
                String sqlImage = "INSERT INTO image (filename, created_at, path) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlImage, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, image.getFilename());
                    pstmt.setTimestamp(2, Timestamp.valueOf(image.getCreatedAt()));
                    pstmt.setString(3, image.getPath());
                    pstmt.executeUpdate();
                    // Lấy ID vừa tạo
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            image.setId(rs.getInt(1));
                        }
                    }
                }
            }

            // 2. Thêm User
            String sqlUser = "INSERT INTO user (first_name, last_name, username, password, email, phone, role, is_active, avatar_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                if (image != null) {
                    pstmt.setInt(9, image.getId());
                } else {
                    pstmt.setInt(9, 1);
                }

                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();// Rollback nếu có lỗi
            }
            return false;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }


    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE `User` SET first_name = ?, last_name = ?, username = ?, password = ?, "
                + "email = ?, phone = ?, role = ?, is_active = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPhone());
            pstmt.setInt(7, user.getRole().getId());
            pstmt.setBoolean(8, user.isActive());
            pstmt.setInt(9, user.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user, Image newImage) throws SQLException {
        if (EmailValidator.isValidEmail(user.getEmail()) == false
                || user.getRole() == null
                || user.getFirstName() == null
                || user.getLastName() == null
                || user.getPhone() == null
                || user.getPassword() == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false); // Bắt đầu transaction
            int newImgId = 1;
            if (newImage != null) {
                String sqlImage = "";
                if (newImage.getId() != 0) {
                    sqlImage = "UPDATE `image` SET filename = ?, created_at = ?, path = ? WHERE id = ?";
                    ImageService imageService = new ImageServiceImpl();
                    Image oldImage = imageService.getImageById(newImage.getId());
                    this.deleteUserImage(oldImage.getPath());
                } else {
                    sqlImage = "INSERT INTO `image` (filename, created_at, path) VALUES (?, ?, ?)";
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sqlImage, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, newImage.getFilename());
                    pstmt.setTimestamp(2, Timestamp.valueOf(newImage.getCreatedAt()));
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
                    } else {
                        newImgId = newImage.getId();
                    }
                }
            }
            String sql = "UPDATE `User` SET first_name = ?, last_name = ?,username = ?, password = ?, "
                    + "email = ?, phone = ?, role = ?, is_active = ?, avatar_id = ? WHERE id = ?";
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
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false; // Không tồn tại user có id => false
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback nếu có lỗi
                return false;
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean deleteUser(int id) throws SQLException, NullPointerException {
        Connection conn = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);

            User u = getUserById(id);
            if (u == null) {
                return false;
            }

            ImageService imageService = new ImageServiceImpl();
            Image image = imageService.getImageById(u.getAvatarId());

            boolean shouldDeleteImage = image != null && image.getId() != 1;

            // 1. Xóa user trước (tránh lỗi foreign key)
            String sqlDeleteUser = "DELETE FROM `User` WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteUser)) {
                pstmt.setInt(1, id);
                if (pstmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Nếu có ảnh custom thì xóa ảnh trong DB (CHƯA xóa trên Cloud)
            if (shouldDeleteImage) {
                String sqlDeleteImage = "DELETE FROM `image` WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteImage)) {
                    pstmt.setInt(1, image.getId());
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Commit DB thành công

            // 3. Sau khi commit, mới xóa trên Cloudinary (ngoài transaction)
            if (shouldDeleteImage) {
                boolean cloudDeleted = deleteUserImage(image.getPath());
                if (!cloudDeleted) {
                    System.err.println("Ảnh đã bị xóa khỏi DB nhưng không xóa được trên Cloudinary.");
                    // Có thể log hoặc gửi cảnh báo tại đây
                }
            }

            return true;
        } catch (SQLException | NullPointerException ex) {
            if (conn != null) {
                conn.rollback();
            }
            ex.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }

    }

    // https://res.cloudinary.com/dg66aou8q/image/upload/v1743086605/dysaruyl1ye7xejpakbp.png
    @Override
    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT u.*, i.id as avatarId, i.filename, i.created_at, i.path\n"
                + "FROM `User` u\n"
                + "LEFT JOIN `image` i ON u.avatar_id = i.id\n"
                + "WHERE username = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (PasswordUtils.checkPassword(password, hashedPassword)) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;
    }

    @Override
    public String getUserImage(User user) {
        try {
            ImageService imageService = new ImageServiceImpl();
            Image image = imageService.getImageById(user.getAvatarId());
            return cloudiServices.getImageUrl(image.getPath());
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
