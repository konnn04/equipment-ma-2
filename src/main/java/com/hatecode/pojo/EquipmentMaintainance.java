/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

/**
 *
 * @author ADMIN
 */
public class EquipmentMaintainance {
    private int id;
    private Equipment equipment;
    private String description;
    private MaintenanceType maintenanceType;
    private float price;
    private int maintainanceId;

    public EquipmentMaintainance() {
    }

    public EquipmentMaintainance(int id, Equipment equipment, String description, MaintenanceType maintenanceType, float price, int maintainanceId) {
        this.id = id;
        this.equipment = equipment;
        this.description = description;
        this.maintenanceType = maintenanceType;
        this.price = price;
        this.maintainanceId = maintainanceId;
    }
    
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the equipmentId
     */
    public Equipment getEquipment() {
        return equipment;
    }

    /**
     * @param equipment the equipmentId to set
     */
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the maintainanceType
     */
    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    /**
     * @param maintenanceType the maintainanceType to set
     */
    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    /**
     * @return the price
     */
    public float getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * @return the maintainanceId
     */
    public int getMaintainanceId() {
        return maintainanceId;
    }

    /**
     * @param maintainanceId the maintainanceId to set
     */
    public void setMaintainanceId(int maintainanceId) {
        this.maintainanceId = maintainanceId;
    }

}
