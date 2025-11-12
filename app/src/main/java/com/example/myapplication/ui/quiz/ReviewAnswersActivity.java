package com.example.myapplication.ui.quiz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.QuizQuestion;

import java.util.ArrayList;

public class ReviewAnswersActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ_QUESTIONS = "EXTRA_QUIZ_QUESTIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_answers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.review_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<QuizQuestion> quizQuestions = getIntent().getParcelableArrayListExtra(EXTRA_QUIZ_QUESTIONS);

        if (quizQuestions != null) {
            ReviewAnswersAdapter adapter = new ReviewAnswersAdapter(quizQuestions);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
