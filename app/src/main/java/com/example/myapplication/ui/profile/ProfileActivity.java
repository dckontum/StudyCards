package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private TextView profileName, profileEmail, deckCount, cardCount;
    private DatabaseHelper dbHelper;
    private int userId;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Refresh the user data
                    loadUserData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        deckCount = findViewById(R.id.deck_count);
        cardCount = findViewById(R.id.card_count);
        MaterialButton logoutButton = findViewById(R.id.logout_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MaterialCardView editProfileButton = findViewById(R.id.edit_profile_button);
        MaterialCardView changePasswordButton = findViewById(R.id.change_password_button);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            loadUserData();
        } else {
            // Handle user not logged in case
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("user_id");
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
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

    private void loadUserData() {
        User currentUser = dbHelper.getUserById(userId);
        if (currentUser != null) {
            profileName.setText(currentUser.getName());
            profileEmail.setText(currentUser.getEmail());

            int decks = dbHelper.getAllDecks(userId).size();
            deckCount.setText(String.valueOf(decks));

            int cards = dbHelper.getCardCountForUser(userId);
            cardCount.setText(String.valueOf(cards));
        }
    }
}
