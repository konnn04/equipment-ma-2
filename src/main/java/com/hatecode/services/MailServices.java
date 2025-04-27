/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface MailServices {
    void sendEmailNotify(String receivedUser, String subject, List<Integer> maintainanceIds);
    
    /**
     * Send enhanced email with detailed maintenance information
     * 
     * @param receivedUser Email address of the recipient
     * @param subject Email subject
     * @param maintenance The maintenance schedule
     * @param equipmentMaintenanceList List of equipment maintenance records 
     */
    void sendEnhancedMaintenanceEmail(
            String receivedUser, 
            String subject,
            Maintenance maintenance, 
            List<EquipmentMaintenance> equipmentMaintenanceList);
}
