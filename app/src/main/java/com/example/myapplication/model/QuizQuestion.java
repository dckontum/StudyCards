package com.example.myapplication.model;

import java.util.List;

public class QuizQuestion {
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String userAnswer;

    public QuizQuestion(String questionText, List<String> options, String correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean wasCorrect() {
        return correctAnswer.equals(userAnswer);
    }
}
