/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.EquipmentImage;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentImageServices {
    List<EquipmentImage> getEquipmentImage() throws SQLException;
    boolean addEquipmentImage(EquipmentImage cate) throws SQLException;
    boolean updateEquipmentImage(EquipmentImage cate) throws SQLException;
    boolean deleteEquipmentImage(EquipmentImage id) throws SQLException;
    
}
