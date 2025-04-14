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
public class Category {
    private int id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public Category() {
    }

    public Category(
            int id,
            String name,
            Boolean isActive,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Category(int category, String categoryName) {
        this.id = category;
        this.name = categoryName;
    }

    public Category(String name, Boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public Category(String name) {
        this.name = name;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
