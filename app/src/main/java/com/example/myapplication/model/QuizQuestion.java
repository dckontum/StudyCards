package com.example.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuizQuestion implements Parcelable {
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

    // Parcelable implementation
    protected QuizQuestion(Parcel in) {
        questionText = in.readString();
        options = in.createStringArrayList();
        correctAnswer = in.readString();
        userAnswer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionText);
        dest.writeStringList(options);
        dest.writeString(correctAnswer);
        dest.writeString(userAnswer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuizQuestion> CREATOR = new Creator<QuizQuestion>() {
        @Override
        public QuizQuestion createFromParcel(Parcel in) {
            return new QuizQuestion(in);
        }

        @Override
        public QuizQuestion[] newArray(int size) {
            return new QuizQuestion[size];
        }
    };
}
