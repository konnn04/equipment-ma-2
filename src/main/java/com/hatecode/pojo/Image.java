package com.hatecode.pojo;

import java.util.Date;

public class Image {
    private int id;
    private String filename;
    private Date createDate;
    private String path;

    public Image() {
    }

    public Image(String filename, String path) {
        this.filename = filename;
        this.path = path;
    }

    public Image(String filename, Date createDate, String path) {
        this.filename = filename;
        this.createDate = createDate;
        this.path = path;
    }

    public Image(int id, String filename, Date createDate, String path) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}