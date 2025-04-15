/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class EquipmentMaintenance {
    private int id;
    private int equipmentId;
    private int maintenanceId;
    private int technicianId;
    private String description;
    private Result result;
    private String repairName;
    private float repairPrice;
    private Date inspectionDate;
    private Date createdAt;

    public EquipmentMaintenance() {
    }

    public EquipmentMaintenance(
            int id,
            int equipmentId,
            int maintenanceId,
            int technicianId,
            String description,
            Result result,
            String repairName,
            float repairPrice,
            Date inspectionDate,
            Date createdAt) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.maintenanceId = maintenanceId;
        this.technicianId = technicianId;
        this.description = description;
        this.result = result;
        this.repairName = repairName;
        this.repairPrice = repairPrice;
        this.inspectionDate = inspectionDate;
        this.createdAt = createdAt;
    }

    public Date getcreatedAt() {
        return createdAt;
    }

    public void setcreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public float getRepairPrice() {
        return repairPrice;
    }

    public void setRepairPrice(float repairPrice) {
        this.repairPrice = repairPrice;
    }

    public String getRepairName() {
        return repairName;
    }

    public void setRepairName(String repairName) {
        this.repairName = repairName;
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
}
