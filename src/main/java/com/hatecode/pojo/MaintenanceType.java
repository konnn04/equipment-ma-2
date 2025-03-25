package com.hatecode.pojo;

public class MaintenanceType {
    private int id;
    private String name;
    private String description;
    private float suggestPrice;

    public MaintenanceType() {
    }

    public MaintenanceType(String name, String description, float suggestPrice) {
        this.name = name;
        this.description = description;
        this.suggestPrice = suggestPrice;
    }

    public MaintenanceType(int id, String name, String description, float suggestPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestPrice = suggestPrice;
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
}