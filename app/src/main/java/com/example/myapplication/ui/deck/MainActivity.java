package com.example.myapplication.ui.deck;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DeckAdapter;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Deck;
import com.example.myapplication.ui.favorite.FavoritesActivity;
import com.example.myapplication.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DeckAdapter.OnDeckEditListener {

    private RecyclerView decksRecyclerView;
    private DeckAdapter deckAdapter;
    private DatabaseHelper dbHelper;
    private List<Deck> deckList;

    private final ActivityResultLauncher<Intent> addDeckLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadDecks();
                }
            });

    private final ActivityResultLauncher<Intent> editDeckLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadDecks();
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

        FloatingActionButton fab = findViewById(R.id.fab_add_deck);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddDeckActivity.class);
            addDeckLauncher.launch(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(getApplicationContext(), FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDecks();
    }

    private void loadDecks() {
        int currentUserId = 1; 
        deckList = dbHelper.getAllDecks(currentUserId);
        deckAdapter = new DeckAdapter(this, deckList, this);
        decksRecyclerView.setAdapter(deckAdapter);
    }

    @Override
    public void onEditDeck(int deckId) {
        Intent intent = new Intent(this, EditDeckActivity.class);
        intent.putExtra(EditDeckActivity.EXTRA_DECK_ID, deckId);
        editDeckLauncher.launch(intent);
    }
}
