package com.hatecode.pojo;

import java.time.LocalDateTime;

public class MaintenanceRepairSuggestion {
    private int id;
    private String name;
    private String description;
    private float suggestPrice;
    private LocalDateTime createdDate;

    public MaintenanceRepairSuggestion() {
    }

    public MaintenanceRepairSuggestion(
            int id,
            String name,
            String description,
            float suggestPrice,
            LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestPrice = suggestPrice;
        this.createdDate = createdDate;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}