package com.example.myapplication.model;

public class FavoriteCard {
    private int userId; // Foreign key to the User table
    private int cardId; // Foreign key to the Flashcard table

    // Constructors
    public FavoriteCard() {
    }

    public FavoriteCard(int userId, int cardId) {
        this.userId = userId;
        this.cardId = cardId;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
