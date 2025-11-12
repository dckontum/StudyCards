package com.example.myapplication.ui.quiz;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.model.QuizQuestion;
import com.example.myapplication.ui.deck.DeckDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_CORRECT_ANSWERS = "EXTRA_CORRECT_ANSWERS";
    public static final String EXTRA_TOTAL_QUESTIONS = "EXTRA_TOTAL_QUESTIONS";
    public static final String EXTRA_DECK_ID = "EXTRA_DECK_ID";
    public static final String EXTRA_QUIZ_QUESTIONS = "EXTRA_QUIZ_QUESTIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        int correctAnswers = getIntent().getIntExtra(EXTRA_CORRECT_ANSWERS, 0);
        int totalQuestions = getIntent().getIntExtra(EXTRA_TOTAL_QUESTIONS, 1);
        int deckId = getIntent().getIntExtra(EXTRA_DECK_ID, -1);
        ArrayList<QuizQuestion> quizQuestions = getIntent().getParcelableArrayListExtra(EXTRA_QUIZ_QUESTIONS);

        TextView scorePercentageText = findViewById(R.id.score_percentage_text);
        TextView correctAnswersText = findViewById(R.id.correct_answers_text);
        TextView incorrectAnswersText = findViewById(R.id.incorrect_answers_text);
        ProgressBar correctProgressBar = findViewById(R.id.correct_progress_bar);
        MaterialButton reviewButton = findViewById(R.id.retry_quiz_button);
        MaterialButton backToDeckButton = findViewById(R.id.back_to_decks_button);

        reviewButton.setText("Review Answers");

        int incorrectAnswers = totalQuestions - correctAnswers;
        int correctPercentage = (totalQuestions > 0) ? (int) (((double) correctAnswers / totalQuestions) * 100) : 0;

        scorePercentageText.setText(String.format("%d%%", correctPercentage));
        correctAnswersText.setText(String.valueOf(correctAnswers));
        incorrectAnswersText.setText(String.valueOf(incorrectAnswers));

        animateProgressBar(correctProgressBar, 0, correctPercentage);

        reviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, ReviewAnswersActivity.class);
            intent.putParcelableArrayListExtra(ReviewAnswersActivity.EXTRA_QUIZ_QUESTIONS, quizQuestions);
            startActivity(intent);
        });

        backToDeckButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, DeckDetailActivity.class);
            intent.putExtra(DeckDetailActivity.EXTRA_DECK_ID, deckId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void animateProgressBar(ProgressBar progressBar, int start, int end) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", start, end);
        progressAnimator.setDuration(1500);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // By default, back press will go back to the QuizResult screen from Review screen.
        // If we want to go all the way back to deck details, we'd add similar logic to backToDeckButton.
        super.onBackPressed();
    }
}
