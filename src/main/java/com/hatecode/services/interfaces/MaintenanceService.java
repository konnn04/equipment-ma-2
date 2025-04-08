package com.hatecode.services.interfaces;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import java.sql.SQLException;
import java.util.List;

public interface MaintenanceService {
    List<Maintenance> getMaintenances() throws SQLException;
    List<Maintenance> getMaintenances(String query, int page, int pageSize, String key, String value) throws SQLException;
    List<Maintenance> getMaintenances(String query) throws SQLException;
    Maintenance getMantenanceById(int id) throws SQLException;
    boolean addMantenance(Maintenance maintenance) throws SQLException;
    boolean updateMantenance(Maintenance maintenance) throws SQLException;
    boolean deleteMantenance(int id) throws SQLException;
}