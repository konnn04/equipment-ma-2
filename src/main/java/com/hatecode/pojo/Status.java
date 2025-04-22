package com.hatecode.pojo;

import java.util.List;

public enum Status {
    INACTIVE (1, "Inactive"),
    ACTIVE (2, "Inactive"),
    UNDER_MAINTENANCE (3, "Under maintenance"),
    BROKEN (4, "Broken"),
    LIQUIDATED (5, "Liquidated");

    private int id;
    private String description;

    Status(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static Status fromId(int id) {
        for (Status status : Status.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

    public static  List<Status> getAllStatus() {
        return List.of(Status.values());
    }
}