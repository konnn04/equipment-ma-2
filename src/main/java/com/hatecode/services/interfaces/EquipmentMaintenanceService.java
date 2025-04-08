/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.interfaces;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentMaintenanceService {
    List<EquipmentMaintenance> getEquipmentMaintenance() throws SQLException;
    List<EquipmentMaintenance> getEquipmentMaintenance(Maintenance m);
    Equipment getEquipmentByEMId(int id) throws SQLException;
    boolean addEquipmentMaintenance(EquipmentMaintenance em) throws SQLException;
    boolean updateEquipmentMaintenance(EquipmentMaintenance em) throws SQLException;
    boolean deleteEquipmentMaintenance(int id) throws SQLException;

}
