package com.hatecode.security;

import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SecurityManager {
    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new EnumMap<>(Role.class);
    
    static {
        // Admin có tất cả quyền
        Set<Permission> adminPermissions = new HashSet<>(Arrays.asList(Permission.values()));
        ROLE_PERMISSIONS.put(Role.ADMIN, adminPermissions);
        
        // Technician có quyền giới hạn
        Set<Permission> technicianPermissions = new HashSet<>();
        technicianPermissions.add(Permission.EQUIPMENT_VIEW);
        technicianPermissions.add(Permission.MAINTENANCE_VIEW);
        technicianPermissions.add(Permission.MAINTENANCE_COMPLETE);
        ROLE_PERMISSIONS.put(Role.TECHNICIAN, technicianPermissions);
        
        // Manager có quyền trung gian
        Set<Permission> managerPermissions = new HashSet<>();
        managerPermissions.add(Permission.EQUIPMENT_VIEW);
        managerPermissions.add(Permission.EQUIPMENT_CREATE);
        managerPermissions.add(Permission.EQUIPMENT_EDIT);
        managerPermissions.add(Permission.MAINTENANCE_VIEW);
        managerPermissions.add(Permission.MAINTENANCE_SCHEDULE);
        managerPermissions.add(Permission.MAINTENANCE_COMPLETE);
        managerPermissions.add(Permission.REPORT_VIEW);
        ROLE_PERMISSIONS.put(Role.MANAGER, managerPermissions);
    }
    
    public static boolean hasPermission(User user, Permission permission) {
        if (user == null || !user.isActive()) {
            return false;
        }
        
        // Admin luôn có tất cả quyền
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        
        Set<Permission> permissions = ROLE_PERMISSIONS.getOrDefault(user.getRole(), Collections.emptySet());
        return permissions.contains(permission);
    }
}