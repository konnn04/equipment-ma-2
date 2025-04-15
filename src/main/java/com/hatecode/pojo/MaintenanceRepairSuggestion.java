package com.hatecode.pojo;

import java.util.Date;

public class MaintenanceRepairSuggestion {
    private int id;
    private String name;
    private String description;
    private float suggestPrice;
    private Date createdAt;

    public MaintenanceRepairSuggestion() {
    }

    public MaintenanceRepairSuggestion(
            int id,
            String name,
            String description,
            float suggestPrice,
            Date createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestPrice = suggestPrice;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getSuggestPrice() {
        return suggestPrice;
    }

    public void setSuggestPrice(float suggestPrice) {
        this.suggestPrice = suggestPrice;
    }

    public Date getcreatedAt() {
        return createdAt;
    }

    public void setcreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}