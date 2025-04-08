package com.hatecode.pojo;

import java.util.List;

public enum Status {
    INACTIVE (1, "Không hoạt động"),
    ACTIVE (2, "Đang hoạt động"),
    UNDER_MAINTENANCE (3, "Đang bảo trì"),
    BROKEN (4, "Đã hỏng"),
    LIQUIDATED (5, "Đã thanh lý");

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