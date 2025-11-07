package com.example.myapplication.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, phoneEditText;
    private TextInputLayout nameLayout, emailLayout, phoneLayout;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        nameEditText = findViewById(R.id.edit_text_name);
        emailEditText = findViewById(R.id.edit_text_email);
        phoneEditText = findViewById(R.id.edit_text_phone);
        Button saveButton = findViewById(R.id.button_save);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            currentUser = dbHelper.getUserById(userId);
            if (currentUser != null) {
                nameEditText.setText(currentUser.getName());
                emailEditText.setText(currentUser.getEmail());
                phoneEditText.setText(currentUser.getPhoneNumber());
            }
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return; // Stop execution if user is not found
        }

        saveButton.setOnClickListener(v -> {
            // Clear previous errors
            nameLayout.setError(null);
            emailLayout.setError(null);
            phoneLayout.setError(null);

            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            boolean hasError = false;
            if (TextUtils.isEmpty(name)) {
                nameLayout.setError("Please fill this field");
                hasError = true;
            }
            if (TextUtils.isEmpty(email)) {
                emailLayout.setError("Please fill this field");
                hasError = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.setError("Invalid email address");
                hasError = true;
            }

            if (hasError) return;

            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhoneNumber(phone);

            dbHelper.updateUser(currentUser);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
