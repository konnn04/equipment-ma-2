package com.hatecode.pojo;

public enum MaintenanceStatus {
    PENDING(0, "Pending"),
    IN_PROGRESS(1, "In Progress"),
    COMPLETED(2, "Completed"),
    CANCELLED(3, "Cancelled");

    private final int code;
    private final String name;

    MaintenanceStatus(int code, String description) {
        this.code = code;
        this.name = description;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MaintenanceStatus fromCode(int code) {
        for (MaintenanceStatus status : MaintenanceStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static MaintenanceStatus fromName(String name) {
        for (MaintenanceStatus status : MaintenanceStatus.values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }

    @Override
    public String toString() {
        return name;
    }
}


