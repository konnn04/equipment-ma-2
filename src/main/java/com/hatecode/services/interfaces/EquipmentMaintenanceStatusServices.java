package com.hatecode.services.interfaces;

import com.hatecode.pojo.EquipmentMaintenanceStatus;

import java.sql.SQLException;
import java.util.List;

public interface EquipmentMaintenanceStatusServices {
    List<EquipmentMaintenanceStatus> getEquipmentMaintenanceStatus() throws SQLException;
}
