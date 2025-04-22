package com.hatecode.services;

import com.hatecode.pojo.Image;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ImageService {
    List<Image> getImages() throws SQLException;
    Image getImageById(int id) throws SQLException;
    Image getImageByPath(String path) throws SQLException;
    boolean addImage(Image image) throws SQLException;
    boolean updateImage(Image image) throws SQLException;
    boolean deleteImage(int id) throws SQLException;
}