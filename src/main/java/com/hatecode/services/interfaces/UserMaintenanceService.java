package com.hatecode.services.interfaces;

import com.hatecode.models.UserMaintenance;
import java.sql.SQLException;
import java.util.List;

public interface UserMaintenanceService {
    List<UserMaintenance> getUserMaintenances() throws SQLException;
    UserMaintenance getUserMaintenanceById(int id) throws SQLException;
    boolean addUserMaintenance(UserMaintenance userMaintenance) throws SQLException;
    boolean updateUserMaintenance(UserMaintenance userMaintenance) throws SQLException;
    boolean deleteUserMaintenance(int id) throws SQLException;
}