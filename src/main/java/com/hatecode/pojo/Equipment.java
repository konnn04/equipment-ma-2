/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class Equipment {
    private int id;
    private String code;
    private String name;
    private Date importDate;
    private Status status;
    private Category category;
    private String statusName;
    private String description;

    public Equipment() {
    }

    public Equipment(int id, String code, String name, Date importDate, Status status, Category category, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.importDate = importDate;
        this.status = status;
        this.category = category;
        this.description = description;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getImportDate() {
        return importDate;
    }

    public void setImport_date(Date import_date) {
        this.importDate = import_date;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
