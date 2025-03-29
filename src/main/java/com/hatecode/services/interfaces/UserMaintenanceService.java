package com.hatecode.services.interfaces;

import com.hatecode.pojo.User_Maintenance;
import java.sql.SQLException;
import java.util.List;

public interface UserMaintenanceService {
    List<User_Maintenance> getUserMaintenances() throws SQLException;
    User_Maintenance getUserMaintenanceById(int id) throws SQLException;
    boolean addUserMaintenance(User_Maintenance userMaintenance) throws SQLException;
    boolean updateUserMaintenance(User_Maintenance userMaintenance) throws SQLException;
    boolean deleteUserMaintenance(int id) throws SQLException;
}