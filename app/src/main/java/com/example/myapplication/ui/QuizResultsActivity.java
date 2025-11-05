package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.QuizQuestion;

import java.util.ArrayList;

public class QuizResultsActivity extends AppCompatActivity {

    private TextView scoreText;
    private Button reviewAnswersButton, doneButton;
    private ArrayList<QuizQuestion> quizQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        scoreText = findViewById(R.id.quiz_score_text);
        reviewAnswersButton = findViewById(R.id.review_answers_button);
        doneButton = findViewById(R.id.done_button);

        // Get data from QuizActivity
        int correctAnswers = getIntent().getIntExtra("correct_answers", 0);
        int totalQuestions = getIntent().getIntExtra("total_questions", 0);
        quizQuestions = getIntent().getParcelableArrayListExtra("quiz_questions");

        // Display score
        scoreText.setText(correctAnswers + "/" + totalQuestions);

        // Set button listeners
        doneButton.setOnClickListener(v -> {
            // Finish and go back to the deck detail screen
            finish();
        });

        reviewAnswersButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultsActivity.this, ReviewQuizActivity.class);
            intent.putParcelableArrayListExtra("quiz_questions", quizQuestions);
            startActivity(intent);
        });
    }
}
