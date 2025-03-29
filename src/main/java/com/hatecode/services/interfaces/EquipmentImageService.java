/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.interfaces;

import com.hatecode.pojo.EquipmentImage;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentImageService {
    List<EquipmentImage> getEquipmentImage(int id) throws SQLException;
    boolean addEquipmentImage(EquipmentImage cate) throws SQLException;
    boolean updateEquipmentImage(EquipmentImage cate) throws SQLException;
    boolean deleteEquipmentImage(int id) throws SQLException;
    
}
