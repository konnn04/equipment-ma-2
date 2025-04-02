package com.hatecode.models;

public class Role extends BaseObject{
    private String description;

    public Role() {
    }

    public Role(String name, String description) {
        super(name);
        this.description = description;
    }

    public Role(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
