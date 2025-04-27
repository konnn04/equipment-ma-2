/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.*;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface EquipmentMaintenanceService {
    List<EquipmentMaintenance> getEquipmentMaintenance() throws SQLException;
    List<EquipmentMaintenance> getEquipmentMaintenance(String query) throws SQLException;
    List<EquipmentMaintenance> getEquipmentMaintenance(Maintenance m)  throws SQLException;
    public List<EquipmentMaintenance> getEquipmentMaintenance(Maintenance m, User u) throws SQLException;
    EquipmentMaintenance getEquipmentMaintenanceById(int id) throws SQLException;
    List<EquipmentMaintenance> getEquipmentsMaintenanceByEMId(String kw, int maintenanceId) throws SQLException;
    Equipment getEquipmentByEquipmentMaintenance(int id) throws SQLException;
    double getTotalPriceEquipmentMaintenance(int maintenanceId) throws SQLException;
    boolean addEquipmentMaintenance(EquipmentMaintenance em) throws SQLException;
    boolean addEquipmentMaintenanceFull(EquipmentMaintenance em) throws SQLException;
    boolean updateEquipmentMaintenance(EquipmentMaintenance em) throws SQLException;
    boolean deleteEquipmentMaintenance(int id) throws SQLException;
    boolean deleteEquipmentMaintenance(EquipmentMaintenance em) throws SQLException;

    boolean updateEquipmentMaintenanceResult(int equipmentMaintenanceId, Result result,
                                             String description, float repairPrice) throws SQLException;
}
