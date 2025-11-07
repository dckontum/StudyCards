package com.example.myapplication.ui.deck;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FlashcardAdapter;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;
import com.example.myapplication.ui.flashcard.AddFlashcardsActivity;
import com.example.myapplication.ui.quiz.QuizActivity;
import com.example.myapplication.ui.study.StudyDeckActivity;

import java.util.ArrayList;
import java.util.List;

public class DeckDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "extra_deck_id";
    public static final String EXTRA_DECK_NAME = "extra_deck_name";

    private TextView deckDetailTitle;
    private ImageView backButton, searchButton, addFlashcardButton;
    private RecyclerView flashcardsRecyclerView;
    private Button studyButton, quizButton;
    private SearchView flashcardSearchView;

    private DatabaseHelper dbHelper;
    private List<Flashcard> flashcardList;
    private FlashcardAdapter adapter;
    private int deckId;

    private final ActivityResultLauncher<Intent> addFlashcardsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshFlashcardList();
                }
            });

    private final ActivityResultLauncher<Intent> editFlashcardLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshFlashcardList();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);

        deckId = getIntent().getIntExtra(EXTRA_DECK_ID, -1);
        String deckName = getIntent().getStringExtra(EXTRA_DECK_NAME);

        dbHelper = new DatabaseHelper(this);

        // Initialize Views
        deckDetailTitle = findViewById(R.id.deck_detail_title);
        backButton = findViewById(R.id.back_button_detail);
        searchButton = findViewById(R.id.search_button);
        addFlashcardButton = findViewById(R.id.add_flashcard_button);
        flashcardsRecyclerView = findViewById(R.id.flashcards_recycler_view);
        studyButton = findViewById(R.id.study_button);
        quizButton = findViewById(R.id.quiz_button);
        flashcardSearchView = findViewById(R.id.flashcard_search_view);

        deckDetailTitle.setText(deckName);

        // Setup RecyclerView
        flashcardList = dbHelper.getFlashcardsForDeck(deckId);
        flashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlashcardAdapter(this, new ArrayList<>(flashcardList), dbHelper, editFlashcardLauncher);
        flashcardsRecyclerView.setAdapter(adapter);

        setupSearchView();
        setupListeners();
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        searchButton.setOnClickListener(v -> {
            deckDetailTitle.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            addFlashcardButton.setVisibility(View.GONE);
            flashcardSearchView.setVisibility(View.VISIBLE);
            flashcardSearchView.setIconified(false); // Expands the SearchView
            flashcardSearchView.requestFocus();
        });

        flashcardSearchView.setOnCloseListener(() -> {
            deckDetailTitle.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            addFlashcardButton.setVisibility(View.VISIBLE);
            flashcardSearchView.setVisibility(View.GONE);
            return false;
        });

        addFlashcardButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeckDetailActivity.this, AddFlashcardsActivity.class);
            intent.putExtra(EXTRA_DECK_ID, deckId);
            addFlashcardsLauncher.launch(intent);
        });

        studyButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeckDetailActivity.this, StudyDeckActivity.class);
            intent.putExtra(StudyDeckActivity.EXTRA_DECK_ID, deckId);
            intent.putExtra(StudyDeckActivity.EXTRA_DECK_NAME, deckDetailTitle.getText().toString());
            startActivity(intent);
        });

        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeckDetailActivity.this, QuizActivity.class);
            intent.putExtra(QuizActivity.EXTRA_DECK_ID, deckId);
            startActivity(intent);
        });
    }

    private void setupSearchView() {
        // Customize the text color
        EditText searchEditText = flashcardSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.GRAY);

        // Customize the close button color
        ImageView closeButton = flashcardSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeButton != null) {
            closeButton.setColorFilter(Color.WHITE);
        }

        flashcardSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFlashcardList();
    }

    private void refreshFlashcardList() {
        if (dbHelper != null) {
            flashcardList = dbHelper.getFlashcardsForDeck(deckId);
            if (adapter != null) {
                adapter.updateData(new ArrayList<>(flashcardList));
            }
        }
    }
}
