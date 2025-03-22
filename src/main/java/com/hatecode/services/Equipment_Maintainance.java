/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.Equipment;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface Equipment_Maintainance {
    List<EquipmentMaintainance> getEquipmentMaintainance() throws SQLException;
    List<Equipment> getEquipmentByEMId(int id) throws SQLException;
}
