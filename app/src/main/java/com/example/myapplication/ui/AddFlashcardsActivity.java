package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class AddFlashcardsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddFlashcardsAdapter adapter;
    private Button addAnotherCardButton, cancelButton, saveAllButton;
    private ImageView backButton;
    private List<Flashcard> flashcardForms;
    private DatabaseHelper dbHelper;
    private int deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcards);

        deckId = getIntent().getIntExtra(StudyDeckActivity.EXTRA_DECK_ID, -1);
        if (deckId == -1) {
            Toast.makeText(this, "Error: Deck ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.add_cards_recycler_view);
        addAnotherCardButton = findViewById(R.id.add_another_card_button);
        cancelButton = findViewById(R.id.cancel_button);
        saveAllButton = findViewById(R.id.save_all_button);
        backButton = findViewById(R.id.back_button_add);

        flashcardForms = new ArrayList<>();
        flashcardForms.add(new Flashcard()); // Add the initial form

        adapter = new AddFlashcardsAdapter(flashcardForms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
        cancelButton.setOnClickListener(v -> finish());

        addAnotherCardButton.setOnClickListener(v -> {
            flashcardForms.add(new Flashcard());
            adapter.notifyItemInserted(flashcardForms.size() - 1);
        });

        saveAllButton.setOnClickListener(v -> saveAllFlashcards());
    }

    private void saveAllFlashcards() {
        List<Flashcard> flashcardsToSave = adapter.getFlashcards();
        boolean allValid = true;
        for (Flashcard card : flashcardsToSave) {
            if (card.getFrontContent() == null || card.getFrontContent().trim().isEmpty() ||
                card.getBackContent() == null || card.getBackContent().trim().isEmpty()) {
                allValid = false;
                break;
            }
        }

        if (allValid) {
            for (Flashcard card : flashcardsToSave) {
                dbHelper.addFlashcard(card, deckId);
            }
            Toast.makeText(this, "Flashcards saved successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Please fill out all questions and answers.", Toast.LENGTH_SHORT).show();
        }
    }
}
