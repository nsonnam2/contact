package com.example.contact.model;

import java.io.Serializable;

public class Contact implements Serializable {
    public enum Type{
        CONTACT, TITLE
    }
    private String id;
    private String name;
    private Type type;
    private boolean emoji;

    public Contact(String id, String name, Type type, boolean emoji) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.emoji = emoji;
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

    public boolean isEmoji() {
        return emoji;
    }

    public void setEmoji(boolean emoji) {
        this.emoji = emoji;
    }
}
