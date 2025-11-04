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

// Implement the listener interface
public class MainActivity extends AppCompatActivity implements DeckAdapter.OnDeckEditListener {

    private RecyclerView decksRecyclerView;
    private DeckAdapter deckAdapter;
    private DatabaseHelper dbHelper;
    private List<Deck> deckList;

    // Launcher for adding a deck
    private final ActivityResultLauncher<Intent> addDeckLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadDecks(); // Refresh list on successful add
                }
            });

    // New launcher specifically for editing a deck
    private final ActivityResultLauncher<Intent> editDeckLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadDecks(); // Refresh list on successful edit
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        decksRecyclerView = findViewById(R.id.decks_recycler_view);
        decksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadDecks();

        FloatingActionButton fab = findViewById(R.id.fab_add_deck);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddDeckActivity.class);
            addDeckLauncher.launch(intent);
        });
    }

    private void loadDecks() {
        int currentUserId = 1; 
        deckList = dbHelper.getAllDecks(currentUserId);
        // Pass 'this' as the listener when creating the adapter
        deckAdapter = new DeckAdapter(this, deckList, this);
        decksRecyclerView.setAdapter(deckAdapter);
    }

    // This method is called by the adapter when the edit button is clicked
    @Override
    public void onEditDeck(int deckId) {
        Intent intent = new Intent(this, EditDeckActivity.class);
        intent.putExtra(EditDeckActivity.EXTRA_DECK_ID, deckId);
        editDeckLauncher.launch(intent); // Use the new launcher
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
