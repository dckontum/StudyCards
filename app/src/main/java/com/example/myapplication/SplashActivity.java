package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_StudyCards);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // After the splash duration, start the LoginActivity
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish(); // Close this activity so the user can't go back to it
        }, SPLASH_DURATION);
    }
}
