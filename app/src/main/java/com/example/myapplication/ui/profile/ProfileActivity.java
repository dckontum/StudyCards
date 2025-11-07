package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.User;
import com.example.myapplication.ui.auth.LoginActivity;
import com.example.myapplication.ui.deck.MainActivity;
import com.example.myapplication.ui.favorite.FavoritesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        TextView profileName = findViewById(R.id.profile_name);
        TextView profileEmail = findViewById(R.id.profile_email);
        TextView deckCount = findViewById(R.id.deck_count);
        TextView cardCount = findViewById(R.id.card_count);
        MaterialButton logoutButton = findViewById(R.id.logout_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MaterialCardView editProfileButton = findViewById(R.id.edit_profile_button);
        MaterialCardView changePasswordButton = findViewById(R.id.change_password_button);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // --- Load User Data ---
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            User currentUser = dbHelper.getUserById(userId);
            if (currentUser != null) {
                profileName.setText(currentUser.getName());
                profileEmail.setText(currentUser.getEmail());

                int decks = dbHelper.getAllDecks(userId).size();
                deckCount.setText(String.valueOf(decks));

                int cards = dbHelper.getCardCountForUser(userId);
                cardCount.setText(String.valueOf(cards));
            }
        } else {
            // Handle user not logged in case
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        logoutButton.setOnClickListener(v -> {
            // Clear SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("user_id");
            editor.apply();

            // Navigate to Login Screen
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        editProfileButton.setOnClickListener(v -> {
            // Navigate to Edit Profile screen (You need to create this activity)
            Toast.makeText(this, "This feature is under development", Toast.LENGTH_SHORT).show();
        });

        changePasswordButton.setOnClickListener(v -> {
            // Navigate to Change Password screen (You need to create this activity)
            Toast.makeText(this, "This feature is under development", Toast.LENGTH_SHORT).show();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(getApplicationContext(), FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}
