/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.interfaces;

import com.hatecode.models.Equipment;
import com.hatecode.models.EquipmentImage;
import com.hatecode.models.Image;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentImageService {
    List<Image> getEquipmentImage(int id) throws SQLException;
    boolean addEquipmentImage(EquipmentImage e) throws SQLException;
    boolean updateEquipmentImage(EquipmentImage e) throws SQLException;
    boolean deleteEquipmentImage(int id) throws SQLException;
    
}
