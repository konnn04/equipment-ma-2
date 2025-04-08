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
    private Category category;
    private LocalDateTime createdDate;
    private Image image;
    private int regularMaintenanceDay;
    private LocalDateTime lastMaintenanceTime;
    private String description;
    private boolean isActive;

    public Equipment() {
    }

    public Equipment(
            String code,
            String name,
            Status status,
            Category category,
            Image image,
            int regularMaintenanceDay,
            String description) {
        this.code = code;
        this.name = name;
        this.status = status;
        this.category = category;
        this.image = image;
        this.regularMaintenanceDay = regularMaintenanceDay;
        this.description = description;
    }

    public Equipment(
            int id,
            String code,
            String name,
            Status status,
            Category category,
            LocalDateTime createdDate,
            Image image,
            int regularMaintenanceDay,
            LocalDateTime lastMaintenanceTime,
            String description,
            boolean isActive) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.category = category;
        this.createdDate = createdDate;
        this.image = image;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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
}
