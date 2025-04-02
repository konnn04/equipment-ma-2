/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.models;

/**
 *
 * @author ADMIN
 */
public class EquipmentMaintainance {
    private int id;
    private int equipmentId;
    private String description;
    private int maintainanceTypeId;
    private float price;
    private int maintainanceId;

    public EquipmentMaintainance() {
    }

    public EquipmentMaintainance(int id, int equipmentId, String description, int maintainanceTypeId, float price, int maintainanceId) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.maintainanceTypeId = maintainanceTypeId;
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
    public int getEquipmentId() {
        return equipmentId;
    }

    /**
     * @param equipmentId the equipmentId to set
     */
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
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
     * @return the maintainanceTypeId
     */
    public int getMaintainanceTypeId() {
        return maintainanceTypeId;
    }

    /**
     * @param maintainanceTypeId the maintainanceTypeId to set
     */
    public void setMaintainanceTypeId(int maintainanceTypeId) {
        this.maintainanceTypeId = maintainanceTypeId;
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
