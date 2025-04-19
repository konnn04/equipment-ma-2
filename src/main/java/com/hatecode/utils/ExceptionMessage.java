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

    public static final String PASSWORD_LENGTH = "Password must be at least 6 characters";
    public static final String USERNAME_INCLUDING_NUMBER = "Username must include at least one number";
    public static final String PASSWORD_INCLUDING_ALPHA = "Password must include at least one letter";
    public static final String PASSWORD_INCLUDING_SPECIAL = "Password must include at least one special character";
    public static final String PASSWORD_INCLUDING_UPPERCASE = "Password must include at least one uppercase letter";
}
