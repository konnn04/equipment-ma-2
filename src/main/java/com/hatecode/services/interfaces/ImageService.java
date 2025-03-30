package com.hatecode.services.interfaces;

import com.hatecode.pojo.Image;
import java.sql.SQLException;
import java.util.List;

public interface ImageService {
    List<Image> getImages() throws SQLException;
    Image getImageById(int id) throws SQLException;
    boolean addImage(Image image) throws SQLException;
    boolean updateImage(Image image) throws SQLException;
    boolean deleteImage(int id) throws SQLException;
}