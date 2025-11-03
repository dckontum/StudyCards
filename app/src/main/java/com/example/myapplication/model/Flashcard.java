package com.example.myapplication.model;

public class Flashcard {
    private int id;
    private String frontContent;
    private String backContent;
    private int deckId; // Foreign key to link with the Deck table

    // Constructors
    public Flashcard() {
    }

    public Flashcard(int id, String frontContent, String backContent, int deckId) {
        this.id = id;
        this.frontContent = frontContent;
        this.backContent = backContent;
        this.deckId = deckId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrontContent() {
        return frontContent;
    }

    public void setFrontContent(String frontContent) {
        this.frontContent = frontContent;
    }

    public String getBackContent() {
        return backContent;
    }

    public void setBackContent(String backContent) {
        this.backContent = backContent;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }
}
