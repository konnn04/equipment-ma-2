package com.hatecode.services.impl;

import com.hatecode.pojo.Image;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.ImageService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageServiceImpl implements ImageService {
    public static Image extractImage(ResultSet rs) throws SQLException{
        return new Image(
                rs.getInt("id"),
                rs.getString("filename"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("path")
        );
    }

    @Override
    public List<Image> getImages() throws SQLException {
        List<Image> images = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn();
             Statement stm = conn.createStatement();) {
            ResultSet rs = stm.executeQuery("SELECT * FROM `Image`");
            while (rs.next()) {
                Image image = extractImage(rs);
                images.add(image);
            }
        }

        return images;
    }

    @Override
    public Image getImageById(int id) throws SQLException {
        Image image = null;
        String sql = "SELECT * FROM `Image` WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                image = extractImage(rs);
            }
        }

        return image;
    }

    @Override
    public boolean addImage(Image image) throws SQLException {
        String sql = "INSERT INTO Image (filename, created_at, path) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, image.getFilename());
            pstmt.setTimestamp(2, Timestamp.valueOf(image.getCreatedAt()));
            pstmt.setString(3, image.getPath());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateImage(Image image) throws SQLException {
        if (image.getFilename() == null || image.getCreatedAt() == null || image.getPath() == null)
            throw new IllegalArgumentException("Image fields must not be null.");

        String sql = "UPDATE Image SET filename = ?, created_at = ?, path = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, image.getFilename());
            pstmt.setTimestamp(2, Timestamp.valueOf(image.getCreatedAt()));
            pstmt.setString(3, image.getPath());
            pstmt.setInt(4, image.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteImage(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        String sql = "DELETE FROM Image WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public Image getImageByPath(String path) throws SQLException {
        String sql = "SELECT * FROM image WHERE path = ?";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, path);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Image img = new Image();
                    img.setId(rs.getInt("id"));
                    img.setFilename(rs.getString("filename"));
                    img.setCreatedAt(rs.getTimestamp("created_date").toLocalDateTime());
                    img.setPath(rs.getString("path"));
                    return img;
                }
            }
        }
        return null;
    }

}
