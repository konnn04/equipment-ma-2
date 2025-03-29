/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.interfaces;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintainance;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentService {
    List<Equipment> getEquipments() throws SQLException;
    List<Equipment> getEquipments(String query, int page, int pageSize, String key, String value) throws SQLException;
    void getEquipmentsImage() throws SQLException;
    List<EquipmentMaintainance> getEquipmentMaintainances(int id) throws SQLException;
    boolean addEquipment(Equipment e) throws SQLException;
    boolean updateEquipment(Equipment e) throws SQLException;
    boolean deleteEquipment(int id) throws SQLException;
}
