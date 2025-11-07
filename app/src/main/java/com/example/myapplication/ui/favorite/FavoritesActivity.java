package com.example.myapplication.ui.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FavoritesAdapter;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;
import com.example.myapplication.ui.deck.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private DatabaseHelper dbHelper;
    private List<Flashcard> favoriteList;
    private TextView noFavoritesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        dbHelper = new DatabaseHelper(this);
        favoritesRecyclerView = findViewById(R.id.favorites_recycler_view);
        noFavoritesTextView = findViewById(R.id.no_favorites_text);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFavorites();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);
        // Use the new, non-deprecated listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                return true;
            }
//            else if (itemId == R.id.nav_profile) {
//                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                overridePendingTransition(0, 0);
//                return true;
//            }
            return false;
        });
    }

    private void loadFavorites() {
        // Assuming user ID is 1 for now
        int currentUserId = 1;
        favoriteList = dbHelper.getFavoriteFlashcards(currentUserId);
        if (favoriteList.isEmpty()) {
            favoritesRecyclerView.setVisibility(View.GONE);
            noFavoritesTextView.setVisibility(View.VISIBLE);
        } else {
            favoritesRecyclerView.setVisibility(View.VISIBLE);
            noFavoritesTextView.setVisibility(View.GONE);
            favoritesAdapter = new FavoritesAdapter(this, favoriteList);
            favoritesRecyclerView.setAdapter(favoritesAdapter);
        }
    }
}
