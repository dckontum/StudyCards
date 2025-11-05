package com.example.myapplication.data;

public class Favorite {
    private String question;
    private String answer;

    public Favorite(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
