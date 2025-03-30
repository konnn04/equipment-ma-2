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
    private int status;
    private int category;

    public Equipment() {
    }

    public Equipment(int id, String code, String name, Date importDate, int status, int category) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.importDate = importDate;
        this.status = status;
        this.category = category;
    }




    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the category
     */
    public int getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(int category) {
        this.category = category;
    }


    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the import_date
     */
    public Date getImportDate() {
        return importDate;
    }

    /**
     * @param import_date the import_date to set
     */
    public void setImport_date(Date import_date) {
        this.importDate = import_date;
    }



}
