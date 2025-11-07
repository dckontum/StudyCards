package com.example.myapplication.ui.quiz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.QuizQuestion;

import java.util.ArrayList;

public class ReviewQuizActivity extends AppCompatActivity {

    private RecyclerView reviewRecyclerView;
    private ReviewQuizAdapter adapter;
    private Toolbar toolbar;
    private ArrayList<QuizQuestion> quizQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_quiz);

        toolbar = findViewById(R.id.review_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        quizQuestions = getIntent().getParcelableArrayListExtra("quiz_questions");

        reviewRecyclerView = findViewById(R.id.review_recycler_view);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewQuizAdapter(this, quizQuestions);
        reviewRecyclerView.setAdapter(adapter);
    }
}
