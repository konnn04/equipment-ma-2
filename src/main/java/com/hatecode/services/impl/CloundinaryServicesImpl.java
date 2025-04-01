/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hatecode.services.CloundinaryServices;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * @author ADMIN
 */
public class CloundinaryServicesImpl implements CloundinaryServices {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLOUD_NAME = dotenv.get("CLOUD_NAME");
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String API_SECRET = dotenv.get("API_SECRET");

    private final Cloudinary cloudinary;

    public CloundinaryServicesImpl() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET,
                "secure", true
        ));
    }


    @Override
    public String getImageUrl(String publicID) throws SQLException {
        return this.cloudinary.url().generate(publicID);
    }

    @Override
    public String uploadImage(File imageFile) throws SQLException {
        try {
            Map<?, ?> uploadResult;
            uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (IOException ex) {
            Logger.getLogger(CloundinaryServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean deleteImage(String publicID) throws SQLException {
        try {
            String extractID = this.extractPublicIdFromUrl(publicID);
            Map<?, ?> result = cloudinary.uploader().destroy(extractID, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException ex) {
            Logger.getLogger(CloundinaryServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // https://res.cloudinary.com/dg66aou8q/image/upload/v1743086605/dysaruyl1ye7xejpakbp.png
    public static String extractPublicIdFromUrl(String imageUrl) {
        // URL có dạng: https://res.cloudinary.com/demo/image/upload/v1234567/public_id.jpg
        String[] parts = imageUrl.split("/upload/")[1].split("/");
        String lastPart = parts[parts.length - 1];
        return lastPart.contains(".")
                ? lastPart.substring(0, lastPart.lastIndexOf('.'))
                : lastPart;
    }
}
