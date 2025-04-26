package com.hatecode.equipmentma2;

import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.User;
import com.hatecode.services.MailServices;
import com.hatecode.services.MaintenanceService;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.MailServicesImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.utils.FormatDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Listener class that sends email notifications when maintenance operations occur
 */
public class MaintenanceEmailNotifier implements MaintenanceManagerController.MaintenanceChangeListener {
    private static final Logger LOGGER = Logger.getLogger(MaintenanceEmailNotifier.class.getName());
    
    private final MailServices mailServices;
    private final MaintenanceService maintenanceService;
    private final UserService userService;
    private final ExecutorService emailExecutor;

    public MaintenanceEmailNotifier() {
        this.mailServices = new MailServicesImpl();
        this.maintenanceService = new MaintenanceServiceImpl();
        this.userService = new UserServiceImpl();
        this.emailExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onMaintenanceChanged(MaintenanceManagerController.OperationType operationType, Maintenance maintenance) {
        if (maintenance == null) return;
        
        // Create a copy of the maintenance object to avoid potential concurrent modification issues
        final Maintenance maintenanceCopy = new Maintenance(
            maintenance.getId(),
            maintenance.getTitle(),
            maintenance.getDescription(),
            maintenance.getStartDateTime(),
            maintenance.getEndDateTime(),
            maintenance.getMaintenanceStatus(),
            maintenance.isActive(),
            maintenance.getCreatedAt()
        );
        
        // Execute email sending in a separate thread
        emailExecutor.submit(() -> {
            try {
                LOGGER.info("Starting async email sending for maintenance ID: " + maintenanceCopy.getId());
                sendEnhancedEmailNotifications(operationType, maintenanceCopy);
                LOGGER.info("Completed async email sending for maintenance ID: " + maintenanceCopy.getId());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to send notification emails asynchronously", e);
            }
        });
        
        // Log that we've queued the email notifications
        LOGGER.info("Queued email notifications for maintenance ID: " + maintenance.getId());
    }
    
    /**
     * Sends enhanced email notifications based on maintenance operation type
     */
    private void sendEnhancedEmailNotifications(MaintenanceManagerController.OperationType operationType, Maintenance maintenance) {
        try {
            // Get email subject based on operation type
            String subject = getEmailSubject(operationType, maintenance);
            
            // For CREATE and UPDATE, we need to send emails to technicians
            if (operationType == MaintenanceManagerController.OperationType.CREATE || 
                operationType == MaintenanceManagerController.OperationType.UPDATE) {
                
                // Get equipment maintenance records for this maintenance
                List<EquipmentMaintenance> allEquipmentMaintenances = 
                    maintenanceService.getEquipmentMaintenancesByMaintenance(maintenance.getId());
                
                // Group equipment maintenance records by technician
                Map<Integer, List<EquipmentMaintenance>> technicianEquipmentMap = new HashMap<>();
                for (EquipmentMaintenance em : allEquipmentMaintenances) {
                    int technicianId = em.getTechnicianId();
                    if (technicianId > 0) {
                        if (!technicianEquipmentMap.containsKey(technicianId)) {
                            technicianEquipmentMap.put(technicianId, new ArrayList<>());
                        }
                        technicianEquipmentMap.get(technicianId).add(em);
                    }
                }
                
                // Send email to each technician with their equipment list
                for (Map.Entry<Integer, List<EquipmentMaintenance>> entry : technicianEquipmentMap.entrySet()) {
                    int technicianId = entry.getKey();
                    List<EquipmentMaintenance> equipmentMaintenances = entry.getValue();
                    
                    try {
                        // Get technician email
                        User technician = userService.getUserById(technicianId);
                        if (technician != null && technician.getEmail() != null && !technician.getEmail().isEmpty()) {
                            // Enhanced email with detailed maintenance and equipment information
                            mailServices.sendEnhancedMaintenanceEmail(
                                technician.getEmail(),
                                subject,
                                maintenance,
                                equipmentMaintenances
                            );
                            
                            LOGGER.info("Sent enhanced maintenance notification to technician: " + 
                                      technician.getEmail() + " for " + equipmentMaintenances.size() + " equipment items");
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Failed to get technician info", e);
                    }
                }
            }
            
            // For DELETE, notify all technicians about the canceled maintenance
            else if (operationType == MaintenanceManagerController.OperationType.DELETE) {
                // Get all technicians who were assigned to this maintenance
                List<EquipmentMaintenance> allEquipmentMaintenances = 
                    maintenanceService.getEquipmentMaintenancesByMaintenance(maintenance.getId());
                
                // Group equipment maintenance records by technician
                Map<Integer, List<EquipmentMaintenance>> technicianEquipmentMap = new HashMap<>();
                for (EquipmentMaintenance em : allEquipmentMaintenances) {
                    int technicianId = em.getTechnicianId();
                    if (technicianId > 0) {
                        if (!technicianEquipmentMap.containsKey(technicianId)) {
                            technicianEquipmentMap.put(technicianId, new ArrayList<>());
                        }
                        technicianEquipmentMap.get(technicianId).add(em);
                    }
                }
                
                // Send cancellation email to each technician
                for (Map.Entry<Integer, List<EquipmentMaintenance>> entry : technicianEquipmentMap.entrySet()) {
                    int technicianId = entry.getKey();
                    List<EquipmentMaintenance> equipmentMaintenances = entry.getValue();
                    
                    try {
                        // Get technician email
                        User technician = userService.getUserById(technicianId);
                        if (technician != null && technician.getEmail() != null && !technician.getEmail().isEmpty()) {
                            // Enhanced email with detailed maintenance and equipment information
                            mailServices.sendEnhancedMaintenanceEmail(
                                technician.getEmail(),
                                subject,
                                maintenance,
                                equipmentMaintenances
                            );
                            
                            LOGGER.info("Sent enhanced maintenance cancellation notification to technician: " + 
                                      technician.getEmail());
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Failed to get technician info for cancellation email", e);
                    }
                }
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send maintenance notification emails", e);
        }
    }
    
    private String getEmailSubject(MaintenanceManagerController.OperationType operationType, Maintenance maintenance) {
        String dateInfo = "";
        if (maintenance.getStartDateTime() != null) {
            dateInfo = " - " + FormatDate.formatDate(maintenance.getStartDateTime().toLocalDate());
        }
        
        switch (operationType) {
            case CREATE:
                return "New Maintenance Schedule: " + maintenance.getTitle() + dateInfo;
            case UPDATE:
                return "Updated Maintenance Schedule: " + maintenance.getTitle() + dateInfo;
            case DELETE:
                return "CANCELED - Maintenance Schedule: " + maintenance.getTitle() + dateInfo;
            default:
                return "Maintenance Schedule Notification: " + maintenance.getTitle() + dateInfo;
        }
    }
    
    /**
     * Shuts down the email executor service when the application is closing
     */
    public void shutdown() {
        emailExecutor.shutdown();
    }
}