package com.example.myapplication.ui.quiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final int DELAY_SHOW_FEEDBACK = 1000; // 1 second
    private static final int DELAY_NO_FEEDBACK = 300;    // 0.3 seconds

    private DatabaseHelper dbHelper;
    private int deckId;
    private String deckName;
    private int questionCount;
    private String reviewOption;
    private int timeLimit; // in seconds

    private ArrayList<QuizQuestion> quizQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    private Toolbar toolbar;
    private TextView progressText, questionText;
    private MaterialButtonToggleGroup optionsGroup;
    private MaterialButton optionA, optionB, optionC, optionD;
    private ProgressBar timeProgressBar;
    private CountDownTimer countDownTimer;

    private ColorStateList correctButtonColor;
    private ColorStateList incorrectButtonColor;
    private ColorStateList defaultOptionColor;
    private ColorStateList defaultStrokeColor;

    private final MaterialButtonToggleGroup.OnButtonCheckedListener buttonListener = (group, checkedId, isChecked) -> {
        if (isChecked) {
            handleAnswerSelection(checkedId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dbHelper = new DatabaseHelper(this);

        deckId = getIntent().getIntExtra(QuizSettingsActivity.EXTRA_DECK_ID, -1);
        deckName = getIntent().getStringExtra(QuizSettingsActivity.EXTRA_DECK_NAME);
        questionCount = getIntent().getIntExtra(QuizSettingsActivity.EXTRA_QUESTION_COUNT, 10);
        reviewOption = getIntent().getStringExtra(QuizSettingsActivity.EXTRA_REVIEW_OPTION);
        timeLimit = getIntent().getIntExtra(QuizSettingsActivity.EXTRA_TIME_LIMIT, 0);
        if (reviewOption == null) {
            reviewOption = "after_each"; // Default value
        }

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
            getSupportActionBar().setTitle(deckName != null ? deckName : "Quiz");
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
        timeProgressBar = findViewById(R.id.time_progress_bar);

        correctButtonColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.quiz_correct_answer));
        incorrectButtonColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.quiz_incorrect_answer));
        defaultOptionColor = ContextCompat.getColorStateList(this, R.color.quiz_option_background_default);
        defaultStrokeColor = ContextCompat.getColorStateList(this, R.color.quiz_option_stroke);
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

        startTimer();
    }

    private void handleAnswerSelection(int checkedId) {
        cancelTimer();
        optionsGroup.removeOnButtonCheckedListener(buttonListener);
        setOptionsEnabled(false);

        MaterialButton selectedButton = findViewById(checkedId);
        // Handle case where time runs out and no button is checked
        String selectedAnswer = (selectedButton != null) ? selectedButton.getText().toString() : ""; 

        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        currentQuestion.setUserAnswer(selectedAnswer);

        if (currentQuestion.wasCorrect()) {
            correctAnswers++;
        }

        if ("at_end".equals(reviewOption)) {
            new Handler(Looper.getMainLooper()).postDelayed(this::moveToNextQuestion, DELAY_NO_FEEDBACK);
        } else {
            if (selectedButton != null) {
                if (currentQuestion.wasCorrect()) {
                    selectedButton.setBackgroundTintList(correctButtonColor);
                } else {
                    selectedButton.setBackgroundTintList(incorrectButtonColor);
                    highlightCorrectAnswer(currentQuestion.getCorrectAnswer());
                }
            } // If timeout, no button is selected, so no color change
            
            new Handler(Looper.getMainLooper()).postDelayed(this::moveToNextQuestion, DELAY_SHOW_FEEDBACK);
        }
    }

    private void startTimer() {
        if (timeLimit > 0) {
            timeProgressBar.setVisibility(View.VISIBLE);
            timeProgressBar.setProgress(100);
            countDownTimer = new CountDownTimer(timeLimit * 1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int progress = (int) (((double) millisUntilFinished / (timeLimit * 1000)) * 100);
                    timeProgressBar.setProgress(progress);
                }

                @Override
                public void onFinish() {
                    timeProgressBar.setProgress(0);
                    handleAnswerSelection(View.NO_ID); // Indicate timeout
                }
            }.start();
        }
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer(); // Prevent memory leaks
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < quizQuestions.size()) {
            displayQuestion();
        } else {
            showQuizResult();
        }
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        MaterialButton correctButton = getButtonFromAnswer(correctAnswer);
        if (correctButton != null) {
            correctButton.setBackgroundTintList(correctButtonColor);
        }
    }

    private MaterialButton getButtonFromAnswer(String answer) {
        if (optionA.getText().toString().equals(answer)) return optionA;
        if (optionB.getText().toString().equals(answer)) return optionB;
        if (optionC.getText().toString().equals(answer)) return optionC;
        if (optionD.getText().toString().equals(answer)) return optionD;
        return null;
    }

    private void showQuizResult() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra(QuizResultActivity.EXTRA_CORRECT_ANSWERS, correctAnswers);
        intent.putExtra(QuizResultActivity.EXTRA_TOTAL_QUESTIONS, quizQuestions.size());
        intent.putExtra(QuizResultActivity.EXTRA_DECK_ID, deckId);
        intent.putParcelableArrayListExtra(QuizResultActivity.EXTRA_QUIZ_QUESTIONS, quizQuestions);
        startActivity(intent);
        finish();
    }

    private void setOptionsEnabled(boolean enabled) {
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            MaterialButton button = (MaterialButton) optionsGroup.getChildAt(i);
             if (!button.isChecked()) {
                button.setEnabled(enabled);
            }
        }
    }

    private void resetOptionStyles() {
        optionsGroup.clearChecked();
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            MaterialButton button = (MaterialButton) optionsGroup.getChildAt(i);
            button.setBackgroundTintList(defaultOptionColor);
            button.setStrokeColor(defaultStrokeColor);
            button.setEnabled(true);
        }
        optionsGroup.addOnButtonCheckedListener(buttonListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
