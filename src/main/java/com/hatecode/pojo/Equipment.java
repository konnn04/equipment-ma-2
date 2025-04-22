/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

import java.time.LocalDateTime;
/**
 * @author ADMIN
 */
public class Equipment {
    private int id;
    private String code;
    private String name;
    private Status status;
    private int categoryId;
    private LocalDateTime createdAt;
    private int imageId;
    private int regularMaintenanceDay;
    private LocalDateTime lastMaintenanceTime;
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
            String code,
            String name,
            Status status,
            int categoryId,
            int imageId,
            int regularMaintenanceDay,
            String description,
            LocalDateTime lastMaintenanceTime) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.categoryId = categoryId;
        this.imageId = imageId;
        this.regularMaintenanceDay = regularMaintenanceDay;
        this.description = description;
        this.lastMaintenanceTime = lastMaintenanceTime;
    }

    public Equipment(
            int id,
            String code,
            String name,
            Status status,
            int categoryId,
            LocalDateTime createdAt,
            int imageId,
            int regularMaintenanceDay,
            LocalDateTime lastMaintenanceTime,
            String description,
            boolean isActive) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
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

    public LocalDateTime getcreatedAt() {
        return createdAt;
    }

    public void setcreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getRegularMaintenanceDay() {
        return regularMaintenanceDay;
    }

    public void setRegularMaintenanceDay(int regularMaintenanceDay) {
        this.regularMaintenanceDay = regularMaintenanceDay;
    }

    public LocalDateTime getLastMaintenanceTime() {
        return lastMaintenanceTime;
    }

    public void setLastMaintenanceTime(LocalDateTime lastMaintenanceTime) {
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
