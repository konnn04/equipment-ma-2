/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.interfaces;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.Maintenance;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentMaintainanceService {
    List<EquipmentMaintainance> getEquipmentMaintainance() throws SQLException;
    List<EquipmentMaintainance> getEquipmentMaintainance(Maintenance m) throws SQLException;
    Equipment getEquipmentByEMId(int id) throws SQLException;
    boolean addEquipmentMaintainance(EquipmentMaintainance em) throws SQLException;
    boolean updateEquipmentMaintainance(EquipmentMaintainance em) throws SQLException;
    boolean deleteEquipmentMaintainance(int id) throws SQLException;
}
