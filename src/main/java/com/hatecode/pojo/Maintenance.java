package com.hatecode.pojo;

import java.time.LocalDateTime;

public class Maintenance {
    private int id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean isActive;
    private MaintenanceStatus maintenanceStatus;
    private LocalDateTime createdAt;

    public Maintenance() {
        super();
    }

    public Maintenance(
            int id,
            String title,
            String description,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            MaintenanceStatus maintenanceStatus,
            boolean isActive,
            LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDatetime;
        this.endDateTime = endDatetime;
        this.maintenanceStatus = maintenanceStatus;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Maintenance(
            String title,
            String description,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            MaintenanceStatus maintenanceStatus){
        this.title = title;
        this.description = description;
        this.startDateTime = startDatetime;
        this.endDateTime = endDatetime;
        this.maintenanceStatus = maintenanceStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public MaintenanceStatus getMaintenanceStatus() {
        return maintenanceStatus;
    }

    public void setMaintenanceStatus(MaintenanceStatus maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }

}
