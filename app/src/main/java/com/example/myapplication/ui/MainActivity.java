package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // --- RecyclerView Setup (for later use) ---
        RecyclerView decksRecyclerView = findViewById(R.id.decks_recycler_view);
        decksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set an adapter for the RecyclerView to show real data

        // --- FAB Setup ---
        FloatingActionButton fab = findViewById(R.id.fab_add_deck);
        fab.setOnClickListener(view -> {
            // TODO: Navigate to a new screen to add a deck
            Toast.makeText(MainActivity.this, "Add new deck clicked", Toast.LENGTH_SHORT).show();
        });
    }

    // Handle menu item clicks (Search, Settings)
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
