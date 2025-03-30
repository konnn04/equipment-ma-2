/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

/**
 *
 * @author ADMIN
 */
public class EquipmentImage {

    private int id;
    private int equipmentId;
    private int imageId;

    public EquipmentImage() {
    }

    public EquipmentImage(int id, int equipmentId, int imageId) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.imageId = imageId;
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
     * @return the imageId
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * @param imageId the imageId to set
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    
    
}
