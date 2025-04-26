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
    public static final String MAINTENANCE_CANNOT_UPDATE_COMPLETED = "Cannot update completed maintenance";
    
    // EquipmentMaintenance
    public static final String EQUIPMENT_MAINTENANCE_ID_NULL = "EquipmentMaintenance ID cannot be null or empty";
    public static final String EQUIPMENT_MAINTENANCE_NOT_INVALID = "EquipmentMaintenance is invalid";
    // Thêm vào ExceptionMessage class
    public static final String EQUIPMENT_MAINTENANCE_TIME_CONFLICT = 
        "Cannot schedule maintenance because one or more equipment already have maintenance scheduled during this time";

    public static final String ROLE_NOT_EXIST = "Role not found";
    public static final String PASSWORD_LESS_THAN_6 = "Password must be at least 6 characters";

    // User
    public static final String EMAIL_FORMAT_ERROR = "The email address you entered is not valid.\n Please enter a valid email address (e.g., example@domain.com)";
    public static final String PASSWORD_REQUIREMENTS =  "Password must contain:\n" +
                                                        "- At least 8 characters\n" +
                                                        "- At least 1 lowercase letter\n" +
                                                        "- At least 1 uppercase letter\n" +
                                                        "- At least 1 digit\n" +
                                                        "- At least 1 special character (!@#$%^&*()_+-=[]{};':\"|,.<>/?)\n";
    public static final String PHONE_FORMAT_ERROR =   "Please enter a valid 10-digit phone number\n"
                                                    + "- Must contain exactly 10 digits\n"
                                                    + "- No spaces or special characters allowed\n"
                                                    + "Example: 0987654321";
}
