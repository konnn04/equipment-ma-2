package com.hatecode.security;

public enum Permission {
    // Equipment permissions
    EQUIPMENT_VIEW,
    EQUIPMENT_CREATE,
    EQUIPMENT_EDIT,
    EQUIPMENT_DELETE,
    
    // Maintenance permissions
    MAINTENANCE_VIEW,
    MAINTENANCE_SCHEDULE,
    MAINTENANCE_COMPLETE,
    
    // User management permissions
    USER_VIEW,
    USER_CREATE,
    USER_EDIT,
    USER_DELETE,
    
    // Report permissions
    REPORT_VIEW,
    
    // System permissions
    SYSTEM_SETTINGS
}