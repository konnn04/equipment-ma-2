/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Image;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentService {
    Equipment getEquipmentById(int id) throws SQLException;
    List<Equipment> getEquipments() throws SQLException;
    List<Equipment> getEquipments(String query, int page, int pageSize, String key, String value) throws SQLException;
    List<EquipmentMaintenance> getEquipmentMaintainances(int id) throws SQLException;
    boolean addEquipment(Equipment e) throws SQLException;
    boolean addEquipment(Equipment e, Image image) throws SQLException;
    boolean updateEquipment(Equipment e) throws SQLException;
    boolean updateEquipment(Equipment e, Image image) throws SQLException;
    boolean deleteEquipment(int id) throws SQLException;
    List<Object> getDistinctValues(String columnName) throws SQLException;
}
