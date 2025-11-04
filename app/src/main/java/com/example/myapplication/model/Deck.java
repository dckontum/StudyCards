package com.example.myapplication.model;

public class Deck {
    private int id;
    private String name;
    private String description;
    private int userId; // Foreign key to link with the User table
    private String iconKey; // To identify the icon drawable
    private String color;   // To store the hex color code

    // Constructors
    public Deck() {
    }

    public Deck(int id, String name, String description, int userId, String iconKey, String color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.iconKey = iconKey;
        this.color = color;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
