package com.hatecode.services;

import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.MaintenanceStatus;

import java.sql.SQLException;
import java.util.List;

public interface MaintenanceService {
    List<Maintenance> getMaintenances() throws SQLException;
    List<Maintenance> getMaintenances(String query) throws SQLException;
    List<Maintenance> getCurrentMaintenances(String query) throws SQLException;
    List<Maintenance> getMaintenances(String query, MaintenanceStatus status)  throws SQLException;
    Maintenance getMaintenanceById(int id) throws SQLException;

    List<EquipmentMaintenance> getEquipmentMaintenancesByMaintenance(int id) throws SQLException;

    boolean addMaintenance(Maintenance maintenance) throws SQLException;
    boolean addMaintenance(Maintenance maintenance, List<EquipmentMaintenance> equipmentMaintenances) throws SQLException;
    boolean updateMaintenance(Maintenance maintenance) throws SQLException;
    boolean deleteMaintenance(Maintenance maintenance) throws SQLException;
    boolean deleteMaintenanceById(int id) throws SQLException;
}