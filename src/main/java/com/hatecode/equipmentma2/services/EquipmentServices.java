/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.equipmentma2.services;

import com.hatecode.equipmentma2.pojo.Equipment;
import com.hatecode.equipmentma2.pojo.EquipmentMaintainance;
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
