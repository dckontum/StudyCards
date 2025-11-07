package com.example.myapplication.ui.quiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;
import com.example.myapplication.model.QuizQuestion;
import com.example.myapplication.ui.quiz.QuizResultsActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_DECK_ID = "extra_deck_id";
    private static final int MIN_CARDS_FOR_QUIZ = 4;

    private TextView progressText, questionText;
    private MaterialButton optionA, optionB, optionC, optionD, submitButton;

    private ArrayList<QuizQuestion> quizQuestions;
    private int currentQuestionIndex = 0;
    private MaterialButton selectedOptionButton = null;
    private int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int deckId = getIntent().getIntExtra(EXTRA_DECK_ID, -1);

        if (deckId == -1) {
            Toast.makeText(this, "Error: Deck not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Flashcard> flashcards = dbHelper.getFlashcardsForDeck(deckId);

        if (flashcards.size() < MIN_CARDS_FOR_QUIZ) {
            Toast.makeText(this, "You need at least 4 cards in this deck to start a quiz.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        quizQuestions = generateQuizQuestions(flashcards);
        if (quizQuestions.isEmpty()) {
            Toast.makeText(this, "Not enough unique answers. You need at least 4 different answers in your cards.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupViews();
        displayQuestion();
    }

    private void setupViews() {
        progressText = findViewById(R.id.quiz_progress_text);
        questionText = findViewById(R.id.quiz_question_text);
        ImageView closeButton = findViewById(R.id.quiz_close_button);

        optionA = findViewById(R.id.quiz_option_a);
        optionB = findViewById(R.id.quiz_option_b);
        optionC = findViewById(R.id.quiz_option_c);
        optionD = findViewById(R.id.quiz_option_d);
        submitButton = findViewById(R.id.quiz_submit_button);

        optionA.setOnClickListener(this);
        optionB.setOnClickListener(this);
        optionC.setOnClickListener(this);
        optionD.setOnClickListener(this);

        closeButton.setOnClickListener(v -> finish());
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private ArrayList<QuizQuestion> generateQuizQuestions(List<Flashcard> flashcards) {
        ArrayList<QuizQuestion> generatedQuestions = new ArrayList<>();
        Set<String> uniqueAnswers = new HashSet<>();
        for (Flashcard fc : flashcards) {
            uniqueAnswers.add(fc.getBackContent());
        }

        if (uniqueAnswers.size() < MIN_CARDS_FOR_QUIZ) {
            return new ArrayList<>(); // Not enough unique answers
        }

        List<String> allUniqueAnswers = new ArrayList<>(uniqueAnswers);
        Collections.shuffle(flashcards);

        for (Flashcard card : flashcards) {
            String questionText = card.getFrontContent();
            String correctAnswer = card.getBackContent();

            List<String> options = new ArrayList<>();
            options.add(correctAnswer);

            List<String> wrongAnswerPool = new ArrayList<>(allUniqueAnswers);
            wrongAnswerPool.remove(correctAnswer);
            Collections.shuffle(wrongAnswerPool);

            for (int i = 0; i < 3; i++) {
                options.add(wrongAnswerPool.get(i));
            }

            Collections.shuffle(options);
            generatedQuestions.add(new QuizQuestion(questionText, options, correctAnswer));
        }

        return generatedQuestions;
    }

    private void displayQuestion() {
        resetOptionStyles();
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);

        progressText.setText("Question " + (currentQuestionIndex + 1) + " of " + quizQuestions.size());
        questionText.setText(currentQuestion.getQuestionText());

        optionA.setText(currentQuestion.getOptions().get(0));
        optionB.setText(currentQuestion.getOptions().get(1));
        optionC.setText(currentQuestion.getOptions().get(2));
        optionD.setText(currentQuestion.getOptions().get(3));

        submitButton.setText("Submit");
        submitButton.setEnabled(false);
        selectedOptionButton = null;
    }

    @Override
    public void onClick(View v) {
        resetOptionStyles();
        selectedOptionButton = (MaterialButton) v;
        selectedOptionButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#D0BCFF"))); // Highlight selected
        selectedOptionButton.setStrokeWidth(4);
        submitButton.setEnabled(true);
    }

    private void handleSubmit() {
        if (selectedOptionButton == null) return;

        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        String selectedAnswer = selectedOptionButton.getText().toString();
        currentQuestion.setUserAnswer(selectedAnswer);

        // Disable all option buttons
        optionA.setEnabled(false);
        optionB.setEnabled(false);
        optionC.setEnabled(false);
        optionD.setEnabled(false);

        if (currentQuestion.wasCorrect()) {
            selectedOptionButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#A5D6A7"))); // Green for correct
            correctAnswers++;
        } else {
            selectedOptionButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#F2B8B5"))); // Red for incorrect
            // Highlight the correct answer
            if (optionA.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                optionA.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#A5D6A7")));
            } else if (optionB.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                optionB.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#A5D6A7")));
            } else if (optionC.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                optionC.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#A5D6A7")));
            } else if (optionD.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                optionD.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#A5D6A7")));
            }
        }

        // Move to the next question or finish quiz
        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < quizQuestions.size()) {
                displayQuestion();
            } else {
                finishQuiz();
            }
        }, 2000); // 2-second delay to show feedback
    }

    private void finishQuiz() {
        Intent intent = new Intent(this, QuizResultsActivity.class);
        intent.putExtra("correct_answers", correctAnswers);
        intent.putExtra("total_questions", quizQuestions.size());
        intent.putParcelableArrayListExtra("quiz_questions", quizQuestions);
        startActivity(intent);
        finish();
    }

    private void resetOptionStyles() {
        MaterialButton[] options = {optionA, optionB, optionC, optionD};
        for (MaterialButton btn : options) {
            btn.setEnabled(true);
            btn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#49454F")));
            btn.setStrokeWidth(1);
        }
    }
}
