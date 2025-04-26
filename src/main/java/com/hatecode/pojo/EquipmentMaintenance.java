/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;


import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
public class EquipmentMaintenance {
    private int id;
    private int equipmentId;
    private String equipmentCode;
    private String equipmentName;
    private int maintenanceId;
    private int technicianId;
    private String description;
    private Result result;
    private float repairPrice;
    private LocalDateTime inspectionDate;
    private LocalDateTime createdAt;
    private boolean isActive;

    public EquipmentMaintenance() {
    }

    public EquipmentMaintenance(
            int id,
            int equipmentId,
            int maintenanceId,
            int technicianId,
            String equipmentCode,
            String equipmentName,
            String description,
            Result result,
            float repairPrice,
            LocalDateTime inspectionDate,
            LocalDateTime createdAt,
            boolean isActive) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.maintenanceId = maintenanceId;
        this.technicianId = technicianId;
        this.equipmentCode = equipmentCode;
        this.equipmentName = equipmentName;
        this.description = description;
        this.result = result;
        this.repairPrice = repairPrice;
        this.inspectionDate = inspectionDate;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public EquipmentMaintenance(
            int equipmentId,
            int maintenanceId,
            int technicianId,
            String equipmentCode,
            String equipmentName,
            String description) {
        this.equipmentId = equipmentId;
        this.maintenanceId = maintenanceId;
        this.technicianId = technicianId;
        this.equipmentCode = equipmentCode;
        this.equipmentName = equipmentName;
        this.description = description;
    }

    public EquipmentMaintenance(
            int equipmentId,
            int maintenanceId,
            int technicianId,
            String equipmentCode,
            String equipmentName,
            String description,
            Result result,
            float repairPrice,
            LocalDateTime inspectionDate,
            LocalDateTime createdAt,
            boolean isActive) {
        this.equipmentId = equipmentId;
        this.maintenanceId = maintenanceId;
        this.technicianId = technicianId;
        this.equipmentCode = equipmentCode;
        this.equipmentName = equipmentName;
        this.description = description;
        this.result = result;
        this.repairPrice = repairPrice;
        this.inspectionDate = inspectionDate;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDateTime inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public float getRepairPrice() {
        return repairPrice;
    }

    public void setRepairPrice(float repairPrice) {
        this.repairPrice = repairPrice;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }

    public int getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(int maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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




}
