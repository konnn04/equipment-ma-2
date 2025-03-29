package com.hatecode.services.interfaces;

import com.hatecode.pojo.MaintenanceType;
import java.sql.SQLException;
import java.util.List;

public interface MaintenanceTypeService {
    List<MaintenanceType> getMaintenanceTypes() throws SQLException;
    MaintenanceType getMaintenanceTypeById(int id) throws SQLException;
    boolean addMaintenanceType(MaintenanceType maintenanceType) throws SQLException;
    boolean updateMaintenanceType(MaintenanceType maintenanceType) throws SQLException;
    boolean deleteMaintenanceType(int id) throws SQLException;
}