package com.example.myapplication.ui.quiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;
import com.example.myapplication.model.QuizQuestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int deckId;
    private String deckName;
    private int questionCount;
    private int timeLimit; // in seconds
    private String reviewOption;

    private ArrayList<QuizQuestion> quizQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    private Toolbar toolbar;
    private TextView progressText, questionText;
    private MaterialButtonToggleGroup optionsGroup;
    private MaterialButton optionA, optionB, optionC, optionD;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dbHelper = new DatabaseHelper(this);

        // Get data from settings
        deckId = getIntent().getIntExtra(QuizSettingsActivity.EXTRA_DECK_ID, -1);
        deckName = getIntent().getStringExtra(QuizSettingsActivity.EXTRA_DECK_NAME);
        questionCount = getIntent().getIntExtra(QuizSettingsActivity.EXTRA_QUESTION_COUNT, 10);
        timeLimit = getIntent().getIntExtra(QuizSettingsActivity.EXTRA_TIME_LIMIT, 0);
        reviewOption = getIntent().getStringExtra(QuizSettingsActivity.EXTRA_REVIEW_OPTION);

        if (deckId == -1) {
            Toast.makeText(this, "Error: Deck not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Flashcard> allFlashcards = dbHelper.getFlashcardsForDeck(deckId);
        if (allFlashcards.size() < 4) {
            Toast.makeText(this, "Not enough cards for a quiz (minimum 4).", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        quizQuestions = generateQuizQuestions(allFlashcards, questionCount);

        setupViews();
        displayQuestion();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.quiz_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(deckName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressText = findViewById(R.id.quiz_progress_text);
        questionText = findViewById(R.id.quiz_question_text);
        optionsGroup = findViewById(R.id.answer_options_group);
        optionA = findViewById(R.id.option_a);
        optionB = findViewById(R.id.option_b);
        optionC = findViewById(R.id.option_c);
        optionD = findViewById(R.id.option_d);

        optionsGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                handleAnswerSelection(checkedId);
            }
        });
    }

    private ArrayList<QuizQuestion> generateQuizQuestions(List<Flashcard> flashcards, int count) {
        ArrayList<QuizQuestion> generatedQuestions = new ArrayList<>();
        Set<String> allAnswers = new HashSet<>();
        for (Flashcard fc : flashcards) {
            allAnswers.add(fc.getBackContent());
        }

        Collections.shuffle(flashcards);
        List<Flashcard> selectedFlashcards = flashcards.subList(0, Math.min(count, flashcards.size()));

        for (Flashcard card : selectedFlashcards) {
            String question = card.getFrontContent();
            String correctAnswer = card.getBackContent();

            List<String> options = new ArrayList<>();
            options.add(correctAnswer);

            List<String> wrongAnswerPool = new ArrayList<>(allAnswers);
            wrongAnswerPool.remove(correctAnswer);
            Collections.shuffle(wrongAnswerPool);

            for (int i = 0; i < 3 && i < wrongAnswerPool.size(); i++) {
                options.add(wrongAnswerPool.get(i));
            }

            Collections.shuffle(options);
            generatedQuestions.add(new QuizQuestion(question, options, correctAnswer));
        }

        return generatedQuestions;
    }

    private void displayQuestion() {
        resetOptionStyles();
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);

        progressText.setText("Question " + (currentQuestionIndex + 1) + " of " + quizQuestions.size());
        questionText.setText(currentQuestion.getQuestionText());

        List<String> options = currentQuestion.getOptions();
        optionA.setText(options.size() > 0 ? options.get(0) : "");
        optionB.setText(options.size() > 1 ? options.get(1) : "");
        optionC.setText(options.size() > 2 ? options.get(2) : "");
        optionD.setText(options.size() > 3 ? options.get(3) : "");
    }

    private void handleAnswerSelection(int checkedId) {
        setOptionsEnabled(false); // Disable all options immediately

        MaterialButton selectedButton = findViewById(checkedId);
        String selectedAnswer = selectedButton.getText().toString();

        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        currentQuestion.setUserAnswer(selectedAnswer);

        if (currentQuestion.wasCorrect()) {
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.quiz_correct_answer));
            correctAnswers++;
        } else {
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.quiz_incorrect_answer));
            highlightCorrectAnswer(currentQuestion.getCorrectAnswer());
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::moveToNextQuestion, 1200); // 1.2 second delay
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < quizQuestions.size()) {
            displayQuestion();
        } else {
            showFinalScore();
        }
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (optionA.getText().toString().equals(correctAnswer)) {
            optionA.setBackgroundColor(ContextCompat.getColor(this, R.color.quiz_correct_answer));
        } else if (optionB.getText().toString().equals(correctAnswer)) {
            optionB.setBackgroundColor(ContextCompat.getColor(this, R.color.quiz_correct_answer));
        } else if (optionC.getText().toString().equals(correctAnswer)) {
            optionC.setBackgroundColor(ContextCompat.getColor(this, R.color.quiz_correct_answer));
        } else if (optionD.getText().toString().equals(correctAnswer)) {
            optionD.setBackgroundColor(ContextCompat.getColor(this, R.color.quiz_correct_answer));
        }
    }

    private void showFinalScore() {
        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished!")
                .setMessage("Your score: " + correctAnswers + " / " + quizQuestions.size())
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void setOptionsEnabled(boolean enabled) {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    private void resetOptionStyles() {
        optionsGroup.clearChecked();
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            MaterialButton button = (MaterialButton) optionsGroup.getChildAt(i);
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            button.setEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
