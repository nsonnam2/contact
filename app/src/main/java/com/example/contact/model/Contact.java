package com.example.contact.model;

import java.io.Serializable;

public class Contact implements Serializable {
    public enum Type{
        CONTACT, TITLE
    }
    private String id;
    private String name;
    private Type type;

    public Contact(String id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
