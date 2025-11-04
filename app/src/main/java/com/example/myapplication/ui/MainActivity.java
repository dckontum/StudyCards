package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView decksRecyclerView;
    private DeckAdapter deckAdapter;
    private DatabaseHelper dbHelper;
    private List<Deck> deckList;

    // Launcher to handle the result from AddDeckActivity
    private final ActivityResultLauncher<Intent> addDeckLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Refresh the deck list if a new deck was added
                    loadDecks();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // --- Database and RecyclerView Setup ---
        dbHelper = new DatabaseHelper(this);
        decksRecyclerView = findViewById(R.id.decks_recycler_view);
        decksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadDecks();

        // --- FAB Setup ---
        FloatingActionButton fab = findViewById(R.id.fab_add_deck);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddDeckActivity.class);
            addDeckLauncher.launch(intent);
        });
    }

    private void loadDecks() {
        int currentUserId = 1; 
        deckList = dbHelper.getAllDecks(currentUserId);
        deckAdapter = new DeckAdapter(this, deckList);
        decksRecyclerView.setAdapter(deckAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
