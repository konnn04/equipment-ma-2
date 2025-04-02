package com.hatecode.models;

import java.util.Date;

public class Maintenance {
    private int id;
    private String title;
    private String description;
    private Date startDateTime;
    private Date endDateTime;

    public Maintenance() {
        super();
    }
    public Maintenance(int id, String title, String description, Date startDatetime, Date endDatetime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDatetime;
        this.endDateTime = endDatetime;
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
    
    public int getId() {
        return this.id;
    }
}
