package com.hatecode.pojo;

import java.util.Date;

public class Maintenance {
    private int id;
    private String title;
    private String description;
    private Date startDateTime;
    private Date endDateTime;
    private int quantity;

    private User technician;

    public Maintenance() {
        super();
    }
    public Maintenance(int id, String title, String description, Date startDatetime, Date endDatetime, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDatetime;
        this.endDateTime = endDatetime;
        this.quantity = quantity;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getStartDatetime() {
        return this.startDateTime;
    }

    public Date getEndDatetime() {
        return this.endDateTime;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getId() {
        return this.id;
    }

    public User getTechnician() {
        return technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }
}
