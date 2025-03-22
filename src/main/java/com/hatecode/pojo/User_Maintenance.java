package com.hatecode.pojo;

public class User_Maintenance {
    private int id;
    private int userId;
    private int maintenanceId;

    public User_Maintenance() {
    }

    public User_Maintenance(int userId, int maintenanceId) {
        this.userId = userId;
        this.maintenanceId = maintenanceId;
    }

    public User_Maintenance(int id, int userId, int maintenanceId) {
        this.id = id;
        this.userId = userId;
        this.maintenanceId = maintenanceId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(int maintenanceId) {
        this.maintenanceId = maintenanceId;
    }
}