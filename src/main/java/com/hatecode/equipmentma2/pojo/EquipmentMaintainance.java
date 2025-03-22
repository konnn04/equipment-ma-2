/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.equipmentma2.pojo;

/**
 *
 * @author ADMIN
 */
public class EquipmentMaintainance {
    private int id;
    private int equipment_id;
    private String description;
    private int maintainance_type_id;
    private float price;
    private int maintainance_id;
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
     * @return the equipment_id
     */
    public int getEquipment_id() {
        return equipment_id;
    }

    /**
     * @param equipment_id the equipment_id to set
     */
    public void setEquipment_id(int equipment_id) {
        this.equipment_id = equipment_id;
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
     * @return the maintainance_type_id
     */
    public int getMaintainance_type_id() {
        return maintainance_type_id;
    }

    /**
     * @param maintainance_type_id the maintainance_type_id to set
     */
    public void setMaintainance_type_id(int maintainance_type_id) {
        this.maintainance_type_id = maintainance_type_id;
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
     * @return the maintainance_id
     */
    public int getMaintainance_id() {
        return maintainance_id;
    }

    /**
     * @param maintainance_id the maintainance_id to set
     */
    public void setMaintainance_id(int maintainance_id) {
        this.maintainance_id = maintainance_id;
    }
    
}
