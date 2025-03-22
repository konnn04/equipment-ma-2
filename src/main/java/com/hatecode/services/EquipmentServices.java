/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintainance;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentServices {
    List<Equipment> getEquipments() throws SQLException;
    void getEquipmentsImage() throws SQLException;
    List<EquipmentMaintainance> getEquipmentMaintainances(int id) throws SQLException;
}
