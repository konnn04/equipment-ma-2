package com.hatecode.pojo;

import java.util.Date;

public class Maintenance {
    private int id;
    private String title;
    private String description;
    private Date startDateTime;
    private Date endDateTime;
    private Date createdAt;

    public Maintenance() {
        super();
    }
    public Maintenance(
            int id,
            String title,
            String description,
            Date startDatetime,
            Date endDatetime,
            Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDatetime;
        this.endDateTime = endDatetime;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Date getcreatedAt() {
        return createdAt;
    }

    public void setcreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
