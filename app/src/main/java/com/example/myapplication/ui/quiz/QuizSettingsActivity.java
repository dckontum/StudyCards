package com.example.myapplication.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    private int selectedQuestions;
    private int timeLimit = 0;
    private String reviewOption = "after_each"; // Default value

    private TextView deckNameText, deckCardCountText, selectedQuestionsText;
    private TextInputLayout questionCountLayout;
    private TextInputEditText questionCountInput;
    private ChipGroup questionCountChips, timeLimitChips, reviewOptionsChips;
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
        validateQuestionChips();
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
        reviewOptionsChips = findViewById(R.id.review_options_chips);
        startQuizButton = findViewById(R.id.button_start_quiz);
    }

    private void updateUiWithDeckInfo() {
        deckNameText.setText(deckName);
        deckCardCountText.setText(maxQuestions + " Cards");
        questionCountLayout.setHint("Custom (Max: " + maxQuestions + ")");
        selectedQuestions = Math.min(5, maxQuestions);
        updateSelectedQuestionsText();
    }

    private void validateQuestionChips() {
        for (int i = 0; i < questionCountChips.getChildCount(); i++) {
            Chip chip = (Chip) questionCountChips.getChildAt(i);
            String chipText = chip.getText().toString();
            if (chipText.equalsIgnoreCase("All Cards")) {
                chip.setEnabled(maxQuestions > 0);
                continue;
            }
            try {
                int questionValue = Integer.parseInt(chipText);
                chip.setEnabled(questionValue <= maxQuestions);
            } catch (NumberFormatException e) {
                chip.setEnabled(false);
            }
        }

        Chip defaultChip = findChipByText(questionCountChips, String.valueOf(selectedQuestions));
        if (defaultChip != null && defaultChip.isEnabled()) {
            questionCountChips.check(defaultChip.getId());
        } else {
            Chip largestValidChip = null;
            for (int i = questionCountChips.getChildCount() - 1; i >= 0; i--) {
                Chip chip = (Chip) questionCountChips.getChildAt(i);
                if (chip.isEnabled()) {
                    largestValidChip = chip;
                    break;
                }
            }
            if (largestValidChip != null) {
                questionCountChips.check(largestValidChip.getId());
                String text = largestValidChip.getText().toString();
                selectedQuestions = text.equalsIgnoreCase("All Cards") ? maxQuestions : Integer.parseInt(text);
                updateSelectedQuestionsText();
            }
        }
    }

    private void setupListeners() {
        questionCountChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip selectedChip = findViewById(checkedIds.get(0));
            if (selectedChip == null) return;
            String text = selectedChip.getText().toString();
            selectedQuestions = text.equalsIgnoreCase("All Cards") ? maxQuestions : Integer.parseInt(text);
            questionCountInput.setText("");
            questionCountInput.clearFocus();
            updateSelectedQuestionsText();
        });

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

        reviewOptionsChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            if (checkedIds.get(0) == R.id.chip_review_at_end) {
                reviewOption = "at_end";
            } else {
                reviewOption = "after_each";
            }
        });

        startQuizButton.setOnClickListener(v -> {
            String customInput = questionCountInput.getText().toString();
            if (!customInput.isEmpty()) {
                try {
                    int customValue = Integer.parseInt(customInput);
                    if (customValue > maxQuestions || customValue <= 0) {
                        Toast.makeText(this, "Please enter a number between 1 and " + maxQuestions, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedQuestions = customValue;
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number format.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (selectedQuestions <= 0 || selectedQuestions > maxQuestions) {
                Toast.makeText(this, "Please select a valid number of questions.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (maxQuestions < 4) {
                Toast.makeText(this, "You need at least 4 cards in this deck to start a quiz.", Toast.LENGTH_LONG).show();
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

    private Chip findChipByText(ChipGroup group, String text) {
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.getText().toString().equals(text)) {
                return chip;
            }
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
