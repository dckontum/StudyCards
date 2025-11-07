package com.example.myapplication.ui.study;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class StudyDeckActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "extra_deck_id";
    public static final String EXTRA_DECK_NAME = "extra_deck_name";

    private TextView deckTitleTextView, cardCounterTextView;
    private ProgressBar studyProgressBar;
    private TextView cardFrontTextView, cardBackTextView;
    private MaterialCardView cardFront, cardBack;
    private Button prevButton, nextButton;
    private ImageView backButton, favoriteIconFront, favoriteIconBack;

    private DatabaseHelper dbHelper;
    private List<Flashcard> flashcards;
    private int currentCardIndex = 0;
    private boolean isFront = true;

    private AnimatorSet frontAnim, backAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_deck);

        int deckId = getIntent().getIntExtra(EXTRA_DECK_ID, -1);
        String deckName = getIntent().getStringExtra(EXTRA_DECK_NAME);

        dbHelper = new DatabaseHelper(this);
        flashcards = dbHelper.getFlashcardsForDeck(deckId);

        deckTitleTextView = findViewById(R.id.deck_title);
        studyProgressBar = findViewById(R.id.study_progress);
        cardFrontTextView = findViewById(R.id.card_front_text);
        cardBackTextView = findViewById(R.id.card_back_text);
        cardFront = findViewById(R.id.card_front);
        cardBack = findViewById(R.id.card_back);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        favoriteIconFront = findViewById(R.id.favorite_icon_front);
        favoriteIconBack = findViewById(R.id.favorite_icon_back);
        cardCounterTextView = findViewById(R.id.card_counter_text);

        deckTitleTextView.setText(deckName);

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        cardFront.setCameraDistance(8000 * scale);
        cardBack.setCameraDistance(8000 * scale);

        frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.card_flip_front);
        backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.card_flip_back);

        updateFlashcard();

        View.OnClickListener flipClickListener = v -> flipCard();
        cardFront.setOnClickListener(flipClickListener);
        cardBack.setOnClickListener(flipClickListener);

        prevButton.setOnClickListener(v -> {
            if (currentCardIndex > 0) {
                currentCardIndex--;
                updateFlashcard();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentCardIndex < flashcards.size() - 1) {
                currentCardIndex++;
                updateFlashcard();
            }
        });

        backButton.setOnClickListener(v -> finish());

        View.OnClickListener favoriteClickListener = v -> {
            Flashcard currentCard = flashcards.get(currentCardIndex);
            boolean isFavorite = !currentCard.isFavorite();
            currentCard.setFavorite(isFavorite);
            dbHelper.setFlashcardFavoriteStatus(currentCard.getId(), isFavorite);
            updateFavoriteIcon(isFavorite, favoriteIconFront);
            updateFavoriteIcon(isFavorite, favoriteIconBack);

            Animator pop = AnimatorInflater.loadAnimator(this, R.animator.heart_pop);
            pop.setTarget(v);
            pop.start();
        };

        favoriteIconFront.setOnClickListener(favoriteClickListener);
        favoriteIconBack.setOnClickListener(favoriteClickListener);
    }

    private void updateFlashcard() {
        if (flashcards != null && !flashcards.isEmpty()) {
            Flashcard currentCard = flashcards.get(currentCardIndex);
            cardFrontTextView.setText(currentCard.getFrontContent());
            cardBackTextView.setText(currentCard.getBackContent());
            studyProgressBar.setMax(flashcards.size());
            studyProgressBar.setProgress(currentCardIndex + 1);
            updateFavoriteIcon(currentCard.isFavorite(), favoriteIconFront);
            updateFavoriteIcon(currentCard.isFavorite(), favoriteIconBack);

            cardCounterTextView.setText((currentCardIndex + 1) + " of " + flashcards.size());

            if (!isFront) {
                flipCard();
            }
        }
    }

    private void updateFavoriteIcon(boolean isFavorite, ImageView favoriteIcon) {
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite);
            favoriteIcon.setColorFilter(Color.rgb(255, 105, 180)); // Hot Pink
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
            favoriteIcon.clearColorFilter();
        }
    }

    private void flipCard() {
        if (isFront) {
            frontAnim.setTarget(cardFront);
            backAnim.setTarget(cardBack);
            frontAnim.start();
            backAnim.start();
            isFront = false;
            cardBack.setVisibility(View.VISIBLE);
            cardFront.setVisibility(View.GONE);
        } else {
            frontAnim.setTarget(cardBack);
            backAnim.setTarget(cardFront);
            backAnim.start();
            frontAnim.start();
            isFront = true;
            cardFront.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.GONE);
        }
    }
}
