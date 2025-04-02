package com.hatecode.services.interfaces;

import com.hatecode.pojo.Maintenance;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface MaintenanceService {
    List<Maintenance> getMaintenances() throws SQLException;
    List<Maintenance> getMaintenances(String query, int page, int pageSize, String key, String value) throws SQLException;
    List<Maintenance> getMaintenances(String kw, Date startDate, Date endDate, int page, int pageSize) throws SQLException;
    Maintenance getMantenanceById(int id) throws SQLException;
    boolean addMantenance(Maintenance maintenance) throws SQLException;
    boolean updateMantenance(Maintenance maintenance) throws SQLException;
    boolean deleteMantenance(int id) throws SQLException;
}