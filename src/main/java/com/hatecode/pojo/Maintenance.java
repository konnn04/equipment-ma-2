package com.hatecode.pojo;

public class Maintenance {
    private int id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private int quantity;

    public Maintenance() {
        super();
    }
    public Maintenance(int id, String title, String description, String startDatetime, String endDatetime, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDatetime;
        this.endDate = endDatetime;
        this.quantity = quantity;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStartDatetime() {
        return this.startDate;
    }

    public String getEndDatetime() {
        return this.endDate;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getId() {
        return this.id;
    }
}
