package com.example.myapplication.model;

import java.io.Serializable;

public class Flashcard implements Serializable {
    private int id;
    private String frontContent;
    private String backContent;
    private int deckId;
    private boolean isFavorite;

    // Constructors
    public Flashcard() {
    }

    public Flashcard(int id, String frontContent, String backContent, int deckId, boolean isFavorite) {
        this.id = id;
        this.frontContent = frontContent;
        this.backContent = backContent;
        this.deckId = deckId;
        this.isFavorite = isFavorite;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
