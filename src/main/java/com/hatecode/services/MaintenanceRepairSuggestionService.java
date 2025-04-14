package com.hatecode.services;

import com.hatecode.pojo.MaintenanceRepairSuggestion;
import java.sql.SQLException;
import java.util.List;

public interface MaintenanceRepairSuggestionService {
    List<MaintenanceRepairSuggestion> getMaintenanceTypes() throws SQLException;
    MaintenanceRepairSuggestion getMaintenanceTypeById(int id) throws SQLException;
    boolean addMaintenanceType(MaintenanceRepairSuggestion maintenanceRepairSuggestion) throws SQLException;
    boolean updateMaintenanceType(MaintenanceRepairSuggestion maintenanceRepairSuggestion) throws SQLException;
    boolean deleteMaintenanceType(int id) throws SQLException;
}