package com.hatecode.pojo;

import java.util.List;

public enum Role {
    ADMIN(1, "Administrator"),
    USER(2, "Technician"),
    MANAGER(3, "Manager");

    private int id;
    private String name;

    Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Role fromId(int id) {
        for (Role role : Role.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role found with id: " + id);
    }

    public static List<Role> getAllRoles() {
        return List.of(Role.values());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
