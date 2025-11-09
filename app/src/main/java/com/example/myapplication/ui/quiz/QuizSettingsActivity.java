package com.example.myapplication.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class QuizSettingsActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "extra_deck_id";
    public static final String EXTRA_DECK_NAME = "extra_deck_name";
    public static final String EXTRA_QUESTION_COUNT = "question_count";
    public static final String EXTRA_TIME_LIMIT = "time_limit";
    public static final String EXTRA_REVIEW_OPTION = "review_option";

    private DatabaseHelper dbHelper;
    private int deckId;
    private String deckName;
    private int maxQuestions;

    // Settings values
    private int selectedQuestions;
    private int timeLimit = 0; // 0 for off, otherwise in seconds
    private String reviewOption = "after_each"; // "after_each" or "at_end"

    // Views
    private TextView deckNameText, deckCardCountText, selectedQuestionsText;
    private TextInputLayout questionCountLayout;
    private TextInputEditText questionCountInput;
    private ChipGroup questionCountChips, timeLimitChips;
    private RadioGroup reviewOptionsGroup;
    private MaterialButton startQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_settings);

        dbHelper = new DatabaseHelper(this);

        deckId = getIntent().getIntExtra(EXTRA_DECK_ID, -1);
        deckName = getIntent().getStringExtra(EXTRA_DECK_NAME);

        if (deckId == -1) {
            Toast.makeText(this, "Deck not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        maxQuestions = dbHelper.getCardCountForDeck(deckId);

        setupViews();
        updateUiWithDeckInfo();
        setupListeners();
    }

    private void setupViews() {
        Toolbar toolbar = findViewById(R.id.quiz_settings_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quiz Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        deckNameText = findViewById(R.id.deck_name_text);
        deckCardCountText = findViewById(R.id.deck_card_count_text);
        selectedQuestionsText = findViewById(R.id.selected_questions_text);
        questionCountLayout = findViewById(R.id.question_count_layout);
        questionCountInput = findViewById(R.id.question_count_input);
        questionCountChips = findViewById(R.id.question_count_chips);
        timeLimitChips = findViewById(R.id.time_limit_chips);
        reviewOptionsGroup = findViewById(R.id.review_options_group);
        startQuizButton = findViewById(R.id.button_start_quiz);
    }

    private void updateUiWithDeckInfo() {
        deckNameText.setText(deckName);
        deckCardCountText.setText(maxQuestions + " Cards");
        questionCountLayout.setHint("Custom (Max: " + maxQuestions + ")");
        selectedQuestions = Math.min(10, maxQuestions);
        updateSelectedQuestionsText();
    }

    private void setupListeners() {
        // Question Count Chips
        questionCountChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip selectedChip = findViewById(checkedIds.get(0));
            if (selectedChip == null) return;
            String text = selectedChip.getText().toString();
            selectedQuestions = text.equalsIgnoreCase("All Cards") ? maxQuestions : Integer.parseInt(text);
            questionCountInput.setText("");
            questionCountInput.clearFocus(); // Clear focus and hide keyboard
            updateSelectedQuestionsText();
        });

        // Time Limit Chips
        timeLimitChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                timeLimit = 0;
                return;
            }
            Chip selectedChip = findViewById(checkedIds.get(0));
            if (selectedChip == null) return;
            String text = selectedChip.getText().toString().replace("s", "");
            timeLimit = text.equalsIgnoreCase("Off") ? 0 : Integer.parseInt(text);
        });

        // Review Options
        reviewOptionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.review_after_question) {
                reviewOption = "after_each";
            } else if (checkedId == R.id.review_at_end) {
                reviewOption = "at_end";
            }
        });

        // Start Button
        startQuizButton.setOnClickListener(v -> {
            if (selectedQuestions <= 0) {
                Toast.makeText(this, "Please select a valid number of questions.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(QuizSettingsActivity.this, QuizActivity.class);
            intent.putExtra(EXTRA_DECK_ID, deckId);
            intent.putExtra(EXTRA_DECK_NAME, deckName);
            intent.putExtra(EXTRA_QUESTION_COUNT, selectedQuestions);
            intent.putExtra(EXTRA_TIME_LIMIT, timeLimit);
            intent.putExtra(EXTRA_REVIEW_OPTION, reviewOption);
            startActivity(intent);
        });
    }

    private void updateSelectedQuestionsText() {
        selectedQuestionsText.setText("Selected: " + selectedQuestions + " Questions");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
