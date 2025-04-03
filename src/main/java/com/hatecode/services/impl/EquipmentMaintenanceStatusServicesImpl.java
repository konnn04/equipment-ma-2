package com.hatecode.services.impl;

import com.hatecode.pojo.EquipmentMaintenanceStatus;
import com.hatecode.services.interfaces.EquipmentMaintenanceStatusServices;
import com.hatecode.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentMaintenanceStatusServicesImpl implements EquipmentMaintenanceStatusServices {

    @Override
    public List<EquipmentMaintenanceStatus> getEquipmentMaintenanceStatus() throws SQLException {
        List<EquipmentMaintenanceStatus> res = new ArrayList<>();

        String sql = "SELECT * FROM equipmentma2test.equipment_maintenance_status";

        try(Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareCall(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                EquipmentMaintenanceStatus e = new EquipmentMaintenanceStatus(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                res.add(e);
            }
        }

        return res;
    }
}
