package com.betterise.maladiecorona.model;

public class Province {
    private String id,name;

    public Province() {
    }

    public Province(String id, String name) {
        this.id = id;
        this.name = name;
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
    // Constructor, getters, and setters for the object here

    @Override
    public String toString() {
        return getName(); // You can add anything else like maybe getDrinkType()
    }
}
