package com.hatecode.models;

public class BaseObject {
    protected int id;
    protected String name;

    public BaseObject() {
    }

    public BaseObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public BaseObject(String name) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
