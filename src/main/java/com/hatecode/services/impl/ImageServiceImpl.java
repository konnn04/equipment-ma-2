package com.hatecode.services.impl;

import com.hatecode.pojo.Image;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.services.interfaces.ImageService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageServiceImpl implements ImageService {

    @Override
    public List<Image> getImages() throws SQLException {
        List<Image> images = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Image");

            while (rs.next()) {
                Image image = new Image(
                        rs.getInt("id"),
                        rs.getString("filename"),
                        rs.getTimestamp("created_at"),
                        rs.getString("path")
                );
                images.add(image);
            }
        }

        return images;
    }

    @Override
    public Image getImageById(int id) throws SQLException {
        Image image = null;
        String sql = "SELECT * FROM Image WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                image = new Image(
                        rs.getInt("id"),
                        rs.getString("filename"),
                        rs.getTimestamp("created_at"),
                        rs.getString("path")
                );
            }
        }

        return image;
    }

    @Override
    public boolean addImage(Image image) throws SQLException {
        String sql = "INSERT INTO Image (filename, created_at, path) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, image.getFilename());
            pstmt.setTimestamp(2, new java.sql.Timestamp(image.getCreateDate().getTime()));
            pstmt.setString(3, image.getPath());

            return pstmt.executeUpdate() > 0;
        }
    }


    @Override
    public boolean updateImage(Image image) throws SQLException {
        String sql = "UPDATE Image SET filename = ?, created_at = ?, path = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, image.getFilename());
            pstmt.setTimestamp(2, new java.sql.Timestamp(image.getCreateDate().getTime()));
            pstmt.setString(3, image.getPath());
            pstmt.setInt(4, image.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteImage(int id) throws SQLException {
        String sql = "DELETE FROM Image WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}