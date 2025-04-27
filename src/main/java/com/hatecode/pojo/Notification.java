package com.hatecode.pojo;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int equipmentId;
    private String equipmentName;
    private String equipmentCode;
    private LocalDateTime maintenanceDueDate;
    private LocalDateTime createdAt;
    private boolean isRead;
    private NotificationType type;
    
    public enum NotificationType {
        MAINTENANCE_DUE(1, "Maintenance Due"),
        EQUIPMENT_ISSUE(2, "Equipment Issue");
        
        private final int code;
        private final String description;
        
        NotificationType(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public Notification() {
    }
    
    public Notification(int id, int equipmentId, String equipmentName, String equipmentCode, 
                        LocalDateTime maintenanceDueDate, LocalDateTime createdAt, 
                        boolean isRead, NotificationType type) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentCode = equipmentCode;
        this.maintenanceDueDate = maintenanceDueDate;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.type = type;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEquipmentId() {
        return equipmentId;
    }
    
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    public String getEquipmentName() {
        return equipmentName;
    }
    
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }
    
    public String getEquipmentCode() {
        return equipmentCode;
    }
    
    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }
    
    public LocalDateTime getMaintenanceDueDate() {
        return maintenanceDueDate;
    }
    
    public void setMaintenanceDueDate(LocalDateTime maintenanceDueDate) {
        this.maintenanceDueDate = maintenanceDueDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
}