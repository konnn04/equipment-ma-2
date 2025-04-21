package com.hatecode.pojo;

import java.time.LocalDateTime;

public class Image {
    private int id;
    private String filename;
    private LocalDateTime createdAt;
    private String path;

    public Image() {
    }

    public Image(String filename, String path) {
        this.filename = filename;
        this.path = path;
    }

    public Image(String filename, LocalDateTime createdAt, String path) {
        this.filename = filename;
        this.createdAt = createdAt;
        this.path = path;
    }

    public Image(int id, String filename, LocalDateTime createdAt, String path) {
        this.id = id;
        this.filename = filename;
        this.createdAt = createdAt;
        this.path = path;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}