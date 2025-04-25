package com.hatecode.utils;

public class ExceptionMessage {
    public static final String DATE_FORMAT_INVALID = "Date format is invalid";

    // Category
    public static final String CATEGORY_NAME_DUPLICATE = "Category name already exists";
    public static final String CATEGORY_NAME_EMPTY = "Category name cannot be empty";
    public static final String CATEGORY_NULL = "Category cannot be null";
    public static final String CATEGORY_ID_NULL = "Category ID cannot be null or empty";

    // Maintenance
    public static final String MAINTENANCE_INVALID_STATUS = "Invalid maintenance status";
    public static final String MAINTENANCE_START_DATE_INVALID = "Start date must be before end date";
    public static final String MAINTENANCE_NAME_EMPTY = "Title cannot be empty";
    public static final String MAINTENANCE_ID_NULL = "Maintenance ID cannot be null or empty";
    public static final String MAINTENANCE_NOT_FOUND = "Maintenance not found";
    public static final String MAINTENANCE_START_DATE_IN_FUTURE = "Start date cannot be in the future";
    public static final String MAINTENANCE_OVERLAP = "Maintenance dates overlap with another maintenance";
    public static final String MAINTENANCE_NOT_FOUND_BY_ID = "Maintenance not found by ID";
    public static final String MAINTENANCE_CANNOT_CHANGE_START_DATE = "Cannot change start date after maintenance has started";
    
    // EquipmentMaintenance
    public static final String EQUIPMENT_MAINTENANCE_ID_NULL = "EquipmentMaintenance ID cannot be null or empty";
    public static final String EQUIPMENT_MAINTENANCE_NOT_INVALID = "EquipmentMaintenance is invalid";
    public static final String ROLE_NOT_EXIST = "Role not found";
    public static final String PASSWORD_LESS_THAN_6 = "Password must be at least 6 characters";
}
