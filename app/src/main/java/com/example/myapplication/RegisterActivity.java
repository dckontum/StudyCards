package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the theme to be consistent with the other screens
        setTheme(R.style.Theme_StudyCards);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView signInText = findViewById(R.id.text_login_prompt_2);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close RegisterActivity to avoid a stack of screens
            }
        });

        // TODO: Add logic for sign up button
    }
}
