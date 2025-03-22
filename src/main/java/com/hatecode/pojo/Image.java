package com.hatecode.pojo;

import java.time.LocalDateTime;

public class Image {
    private int id;
    private String filename;
    private LocalDateTime createDate;
    private String path;

    public Image() {
    }

    public Image(String filename, LocalDateTime createDate, String path) {
        this.filename = filename;
        this.createDate = createDate;
        this.path = path;
    }

    public Image(int id, String filename, LocalDateTime createDate, String path) {
        this.id = id;
        this.filename = filename;
        this.createDate = createDate;
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

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}