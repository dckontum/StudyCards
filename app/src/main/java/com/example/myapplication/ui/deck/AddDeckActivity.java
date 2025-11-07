package com.example.myapplication.ui.deck;

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
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

public class AddDeckActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText deckNameEditText, deckDescriptionEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;

    // Preview Views
    private MaterialCardView previewCard;
    private TextView previewDeckName, previewDeckDescription;
    private ImageView previewDeckIcon;

    // Icon Picker
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<String> iconKeys = Arrays.asList("ic_book", "ic_science", "ic_translate", "ic_map", "ic_code", "ic_add");
    private String selectedIconKey = "ic_book"; // Default

    // Color Picker
    private LinearLayout colorContainer;
    private List<String> colorHexes = Arrays.asList("#7C4DFF", "#E91E63", "#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#F44336", "#00BCD4");
    private String selectedColor = "#7C4DFF"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deck);

        dbHelper = new DatabaseHelper(this);
        setupViews();
        setupToolbar();
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

        // --- Correctly find preview views ---
        previewCard = findViewById(R.id.preview_deck_item);
        previewDeckName = previewCard.findViewById(R.id.deck_name);
        previewDeckDescription = previewCard.findViewById(R.id.deck_description);
        previewDeckIcon = previewCard.findViewById(R.id.deck_icon);

        // Pickers
        iconRecyclerView = findViewById(R.id.icon_recycler_view);
        colorContainer = findViewById(R.id.color_container);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveDeck());

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

    private void saveDeck() {
        String name = deckNameEditText.getText().toString().trim();
        String description = deckDescriptionEditText.getText().toString().trim();

        int currentUserId = 1;
        dbHelper.addDeck(name, description, currentUserId, selectedIconKey, selectedColor);

        Toast.makeText(this, "Deck saved successfully!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    // Adapter for Icon Picker
    private class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AddDeckActivity.this).inflate(R.layout.item_icon_choice, parent, false);
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
