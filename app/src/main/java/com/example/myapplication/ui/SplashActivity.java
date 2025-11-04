package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_StudyCards);
        super.onCreate(savedInstanceState);

        // Force database creation and mock data insertion on app start
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_DURATION);
    }
}
