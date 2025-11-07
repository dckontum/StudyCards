package com.example.myapplication.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText oldPasswordEditText, newPasswordEditText, reEnterPasswordEditText;
    private TextInputLayout oldPasswordLayout, newPasswordLayout, reEnterPasswordLayout;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.change_password_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        oldPasswordLayout = findViewById(R.id.old_password_layout);
        newPasswordLayout = findViewById(R.id.new_password_layout);
        reEnterPasswordLayout = findViewById(R.id.re_enter_password_layout);
        oldPasswordEditText = findViewById(R.id.edit_text_old_password);
        newPasswordEditText = findViewById(R.id.edit_text_new_password);
        reEnterPasswordEditText = findViewById(R.id.edit_text_re_enter_password);
        Button savePasswordButton = findViewById(R.id.button_save_password);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            currentUser = dbHelper.getUserById(userId);
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return; // Stop execution if user is not found
        }

        savePasswordButton.setOnClickListener(v -> {
            // Clear previous errors
            oldPasswordLayout.setError(null);
            newPasswordLayout.setError(null);
            reEnterPasswordLayout.setError(null);

            String oldPassword = oldPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String reEnterPassword = reEnterPasswordEditText.getText().toString().trim();

            boolean hasError = false;
            if (TextUtils.isEmpty(oldPassword)) {
                oldPasswordLayout.setError("Please fill this field");
                hasError = true;
            }
            if (TextUtils.isEmpty(newPassword)) {
                newPasswordLayout.setError("Please fill this field");
                hasError = true;
            }
            if (TextUtils.isEmpty(reEnterPassword)) {
                reEnterPasswordLayout.setError("Please fill this field");
                hasError = true;
            }

            if(hasError) return;

            if (!oldPassword.equals(currentUser.getPassword())) {
                oldPasswordLayout.setError("Incorrect old password");
                return;
            }

            if (!newPassword.equals(reEnterPassword)) {
                reEnterPasswordLayout.setError("New passwords do not match");
                return;
            }

            dbHelper.updatePassword(currentUser.getId(), newPassword);

            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
