package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class EditFlashcardActivity extends AppCompatActivity {

    public static final String EXTRA_FLASHCARD_ID = "com.example.myapplication.ui.EXTRA_FLASHCARD_ID";
    public static final String EXTRA_FLASHCARD_QUESTION = "com.example.myapplication.ui.EXTRA_FLASHCARD_QUESTION";
    public static final String EXTRA_FLASHCARD_ANSWER = "com.example.myapplication.ui.EXTRA_FLASHCARD_ANSWER";

    private TextInputEditText questionEditText, answerEditText;
    private Button saveButton;
    private ImageView backButton;
    private DatabaseHelper dbHelper;
    private int flashcardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flashcard);

        dbHelper = new DatabaseHelper(this);

        questionEditText = findViewById(R.id.edit_flashcard_question);
        answerEditText = findViewById(R.id.edit_flashcard_answer);
        saveButton = findViewById(R.id.save_flashcard_button);
        backButton = findViewById(R.id.back_button_edit_flashcard);

        Intent intent = getIntent();
        flashcardId = intent.getIntExtra(EXTRA_FLASHCARD_ID, -1);
        String currentQuestion = intent.getStringExtra(EXTRA_FLASHCARD_QUESTION);
        String currentAnswer = intent.getStringExtra(EXTRA_FLASHCARD_ANSWER);

        if (flashcardId == -1) {
            Toast.makeText(this, "Error: Flashcard not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        questionEditText.setText(currentQuestion);
        answerEditText.setText(currentAnswer);

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String newQuestion = questionEditText.getText().toString().trim();
        String newAnswer = answerEditText.getText().toString().trim();

        if (newQuestion.isEmpty() || newAnswer.isEmpty()) {
            Toast.makeText(this, "Please fill out both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateFlashcard(flashcardId, newQuestion, newAnswer);
        Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
