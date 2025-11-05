package com.example.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuizQuestion implements Parcelable {
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String userAnswer;
    private boolean wasCorrect;

    public QuizQuestion(String questionText, List<String> options, String correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getUserAnswer() { return userAnswer; }
    public boolean wasCorrect() { return wasCorrect; }

    // Setters
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        this.wasCorrect = correctAnswer.equals(userAnswer);
    }

    // --- Parcelable Implementation ---

    protected QuizQuestion(Parcel in) {
        questionText = in.readString();
        options = in.createStringArrayList();
        correctAnswer = in.readString();
        userAnswer = in.readString();
        wasCorrect = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionText);
        dest.writeStringList(options);
        dest.writeString(correctAnswer);
        dest.writeString(userAnswer);
        dest.writeByte((byte) (wasCorrect ? 1 : 0));
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
