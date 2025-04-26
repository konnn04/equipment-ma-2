package com.hatecode.services.impl;

import com.hatecode.pojo.*;
import com.hatecode.services.EquipmentMaintenanceService;
import com.hatecode.services.EquipmentService;
import com.hatecode.services.MaintenanceService;
import com.hatecode.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaintenanceStatusUpdateServiceImpl {
    private static final Logger LOGGER = Logger.getLogger(MaintenanceStatusUpdateServiceImpl.class.getName());
    
    private final MaintenanceService maintenanceService;
    private final EquipmentService equipmentService;
    private final EquipmentMaintenanceService equipmentMaintenanceService;
    
    public MaintenanceStatusUpdateServiceImpl() {
        this.maintenanceService = new MaintenanceServiceImpl();
        this.equipmentService = new EquipmentServiceImpl();
        this.equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
    }
    
    /**
     * Updates the status of all maintenance schedules based on current time
     */
    public void updateAllMaintenanceStatuses() {
        try {
            LOGGER.info("Starting maintenance status update process");
            
            // Get all active maintenance schedules
            List<Maintenance> maintenances = maintenanceService.getMaintenances();
            LocalDateTime now = LocalDateTime.now();
            
            for (Maintenance maintenance : maintenances) {
                // Determine the appropriate status based on start and end times
                MaintenanceStatus currentStatus = maintenance.getMaintenanceStatus();
                MaintenanceStatus newStatus = determineMaintenanceStatus(maintenance, now);
                
                // If status has changed, update maintenance and equipment statuses
                if (currentStatus != newStatus) {
                    updateMaintenanceStatus(maintenance, newStatus);
                    updateEquipmentStatuses(maintenance, newStatus);
                    
                    LOGGER.info(String.format("Updated maintenance #%d status: %s -> %s", 
                        maintenance.getId(), currentStatus, newStatus));
                }
            }
            
            LOGGER.info("Maintenance status update process completed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating maintenance statuses", e);
        }
    }
    
    /**
     * Determines the appropriate maintenance status based on current time
     */
    private MaintenanceStatus determineMaintenanceStatus(Maintenance maintenance, LocalDateTime now) {
        // Maintenance is cancelled
        if (maintenance.getMaintenanceStatus() == MaintenanceStatus.CANCELLED) {
            return MaintenanceStatus.CANCELLED;
        }
        
        // Maintenance hasn't started yet
        if (now.isBefore(maintenance.getStartDateTime())) {
            return MaintenanceStatus.PENDING;
        }
        
        // Maintenance has finished
        if (now.isAfter(maintenance.getEndDateTime())) {
            return MaintenanceStatus.COMPLETED;
        }
        
        // Maintenance is ongoing
        return MaintenanceStatus.IN_PROGRESS;
    }
    
    /**
     * Updates the status of a maintenance in the database
     */
    private void updateMaintenanceStatus(Maintenance maintenance, MaintenanceStatus newStatus) throws SQLException {
        maintenance.setMaintenanceStatus(newStatus);
        
        // Use direct SQL update for efficiency
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE maintenance SET status = ? WHERE id = ?")) {
            stmt.setInt(1, newStatus.getCode());
            stmt.setInt(2, maintenance.getId());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Updates the status of equipment based on maintenance status
     */
    private void updateEquipmentStatuses(Maintenance maintenance, MaintenanceStatus maintenanceStatus) throws SQLException {
        List<EquipmentMaintenance> equipmentMaintenances = 
                maintenanceService.getEquipmentMaintenancesByMaintenance(maintenance.getId());
        
        for (EquipmentMaintenance em : equipmentMaintenances) {
            Equipment equipment = equipmentService.getEquipmentById(em.getEquipmentId());
            
            if (equipment == null) {
                LOGGER.warning("Equipment not found: ID " + em.getEquipmentId());
                continue;
            }
            
            Status newStatus = determineEquipmentStatus(equipment, em, maintenanceStatus);
            
            // Only update if status changed
            if (equipment.getStatus() != newStatus) {
                updateEquipmentStatus(equipment, newStatus);
                
                // If maintenance is completed, update the last maintenance time
                if (maintenanceStatus == MaintenanceStatus.COMPLETED) {
                    equipment.setLastMaintenanceTime(LocalDateTime.now());
                    equipmentService.updateEquipment(equipment);
                }
                
                LOGGER.info(String.format("Updated equipment #%d status: %s -> %s", 
                    equipment.getId(), equipment.getStatus(), newStatus));
            }
        }
    }
    
    /**
     * Determines the appropriate equipment status based on maintenance status and results
     */
    private Status determineEquipmentStatus(
            Equipment equipment, 
            EquipmentMaintenance em, 
            MaintenanceStatus maintenanceStatus) {
        
        switch (maintenanceStatus) {
            case IN_PROGRESS:
                return Status.UNDER_MAINTENANCE;
                
            case COMPLETED:
                // Determine new status based on maintenance result
                Result result = em.getResult();
                
                if (result == null || result == Result.NORMALLY || result == Result.NEED_REPAIR) {
                    return Status.ACTIVE;
                } else if (result == Result.BROKEN || result == Result.NEEDS_DISPOSAL) {
                    return Status.BROKEN;
                }
                
                // Default to previous status if result is not handled
                return equipment.getStatus();
                
            case CANCELLED:
                // For cancelled maintenance, revert to ACTIVE if currently under maintenance
                if (equipment.getStatus() == Status.UNDER_MAINTENANCE) {
                    return Status.ACTIVE;
                }
                // Otherwise keep current status
                return equipment.getStatus();
                
            default:
                // For other statuses, don't change equipment status
                return equipment.getStatus();
        }
    }
    
    /**
     * Updates equipment status in the database
     */
    private void updateEquipmentStatus(Equipment equipment, Status newStatus) throws SQLException {
        equipment.setStatus(newStatus);
        
        // Use direct SQL update for efficiency
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE equipment SET status = ? WHERE id = ?")) {
            stmt.setInt(1, newStatus.getId());
            stmt.setInt(2, equipment.getId());
            stmt.executeUpdate();
        }
    }
}