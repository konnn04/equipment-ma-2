/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

import java.util.Date;
/**
 * @author ADMIN
 */
public class Equipment {
    private int id;
    private String code;
    private String name;
    private Status status;
    private int categoryId;
    private Date createdDate;
    private int imageId;
    private int regularMaintenanceDay;
    private Date lastMaintenanceTime;
    private String description;
    private boolean isActive;

    public Equipment(
            int id,
            String code,
            String name,
            Status status,
            int categoryId,
            int imageId,
            int regularMaintenanceDay,
            String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.categoryId = categoryId;
        this.imageId = imageId;
        this.regularMaintenanceDay = regularMaintenanceDay;
        this.description = description;
    }

    public Equipment(
            String code,
            String name,
            Status status,
            int categoryId,
            int imageId,
            int regularMaintenanceDay,
            String description) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.categoryId = categoryId;
        this.imageId = imageId;
        this.regularMaintenanceDay = regularMaintenanceDay;
        this.description = description;
    }

    public Equipment(
            int id,
            String code,
            String name,
            Status status,
            int categoryId,
            Date createdDate,
            int imageId,
            int regularMaintenanceDay,
            Date lastMaintenanceTime,
            String description,
            boolean isActive) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.categoryId = categoryId;
        this.createdDate = createdDate;
        this.imageId = imageId;
        this.regularMaintenanceDay = regularMaintenanceDay;
        this.lastMaintenanceTime = lastMaintenanceTime;
        this.description = description;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getRegularMaintenanceDay() {
        return regularMaintenanceDay;
    }

    public void setRegularMaintenanceDay(int regularMaintenanceDay) {
        this.regularMaintenanceDay = regularMaintenanceDay;
    }

    public Date getLastMaintenanceTime() {
        return lastMaintenanceTime;
    }

    public void setLastMaintenanceTime(Date lastMaintenanceTime) {
        this.lastMaintenanceTime = lastMaintenanceTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setId(int id) {
        this.id = id;
    }
}
