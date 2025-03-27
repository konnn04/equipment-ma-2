/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hatecode.services;

import java.sql.SQLException;
import javafx.stage.Stage;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;

/**
 *
 * @author ADMIN
 */
public interface CloundinaryServices {
    String getImageUrl(String publicID) throws SQLException;
    String uploadImage(File imageFile) throws SQLException;
    boolean deleteImage(String publicID) throws SQLException;
}
