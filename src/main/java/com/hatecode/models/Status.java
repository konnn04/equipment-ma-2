package com.hatecode.models;

public class Status extends BaseObject{
    private String description;

    public Status() {
    }

    public Status(int id, String name) {
        super(id, name);
    }

    public Status(String name, String description) {
        super(name);
        this.description = description;
    }

    public Status(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}