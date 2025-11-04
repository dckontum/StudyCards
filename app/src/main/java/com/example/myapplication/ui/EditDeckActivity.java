package com.example.myapplication.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Deck;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

public class EditDeckActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "extra_deck_id";

    private Toolbar toolbar;
    private EditText deckNameEditText, deckDescriptionEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private Deck currentDeck;

    // Preview Views
    private MaterialCardView previewCard;
    private TextView previewDeckName, previewDeckDescription;
    private ImageView previewDeckIcon;

    // Icon Picker
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<String> iconKeys = Arrays.asList("ic_book", "ic_science", "ic_translate", "ic_map", "ic_code", "ic_add");
    private String selectedIconKey;

    // Color Picker
    private LinearLayout colorContainer;
    private List<String> colorHexes = Arrays.asList("#7C4DFF", "#E91E63", "#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#F44336", "#00BCD4");
    private String selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deck); // Reusing the same layout

        dbHelper = new DatabaseHelper(this);
        
        int deckId = getIntent().getIntExtra(EXTRA_DECK_ID, -1);
        if (deckId == -1) {
            Toast.makeText(this, "Error: Deck not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentDeck = dbHelper.getDeck(deckId);
        if (currentDeck == null) {
            Toast.makeText(this, "Error: Could not load deck", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        selectedIconKey = currentDeck.getIconKey();
        selectedColor = currentDeck.getColor();

        setupViews();
        setupToolbar();
        populateData();
        setupListeners();
        setupIconPicker();
        setupColorPicker();
        updatePreview();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.add_deck_toolbar);
        deckNameEditText = findViewById(R.id.edit_text_deck_name);
        deckDescriptionEditText = findViewById(R.id.edit_text_deck_description);
        saveButton = findViewById(R.id.button_save_deck);
        
        previewCard = findViewById(R.id.preview_deck_item);
        previewDeckName = previewCard.findViewById(R.id.deck_name);
        previewDeckDescription = previewCard.findViewById(R.id.deck_description);
        previewDeckIcon = previewCard.findViewById(R.id.deck_icon);
        
        iconRecyclerView = findViewById(R.id.icon_recycler_view);
        colorContainer = findViewById(R.id.color_container);
    }

    private void setupToolbar() {
        toolbar.setTitle("Edit Deck");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void populateData() {
        deckNameEditText.setText(currentDeck.getName());
        deckDescriptionEditText.setText(currentDeck.getDescription());
        saveButton.setText("Update Deck");
        saveButton.setEnabled(true);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> updateDeck());

        deckNameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewDeckName.setText(s.toString());
                saveButton.setEnabled(!s.toString().trim().isEmpty());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        deckDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewDeckDescription.setText(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupIconPicker() {
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        iconAdapter = new IconAdapter();
        iconRecyclerView.setAdapter(iconAdapter);
    }

    private void setupColorPicker() {
        colorContainer.removeAllViews();
        for (String colorHex : colorHexes) {
            View colorViewLayout = LayoutInflater.from(this).inflate(R.layout.item_color_choice, colorContainer, false);
            View colorView = colorViewLayout.findViewById(R.id.color_view);
            ImageView checkMark = colorViewLayout.findViewById(R.id.color_selection_check);

            colorView.setBackgroundColor(Color.parseColor(colorHex));
            checkMark.setVisibility(colorHex.equals(selectedColor) ? View.VISIBLE : View.GONE);

            colorViewLayout.setOnClickListener(v -> {
                selectedColor = colorHex;
                updatePreview();
                setupColorPicker();
            });
            colorContainer.addView(colorViewLayout);
        }
    }

    private void updatePreview() {
        previewDeckName.setText(deckNameEditText.getText().toString());
        previewDeckDescription.setText(deckDescriptionEditText.getText().toString());
        
        int drawableId = getResources().getIdentifier(selectedIconKey, "drawable", getPackageName());
        previewDeckIcon.setImageResource(drawableId);

        previewCard.setCardBackgroundColor(Color.parseColor(selectedColor));
    }

    private void updateDeck() {
        String name = deckNameEditText.getText().toString().trim();
        String description = deckDescriptionEditText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Deck name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentDeck.setName(name);
        currentDeck.setDescription(description);
        currentDeck.setIconKey(selectedIconKey);
        currentDeck.setColor(selectedColor);

        dbHelper.updateDeck(currentDeck);
        Toast.makeText(this, "Deck updated successfully!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    // --- IconAdapter Inner Class ---
    private class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EditDeckActivity.this).inflate(R.layout.item_icon_choice, parent, false);
            return new IconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
            String iconKey = iconKeys.get(position);
            int drawableId = getResources().getIdentifier(iconKey, "drawable", getPackageName());
            holder.iconImage.setImageResource(drawableId);
            
            holder.selectionBorder.setVisibility(iconKey.equals(selectedIconKey) ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                selectedIconKey = iconKey;
                updatePreview();
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return iconKeys.size();
        }

        class IconViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImage;
            View selectionBorder;

            IconViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.icon_image);
                selectionBorder = itemView.findViewById(R.id.icon_selection_border);
            }
        }
    }
}