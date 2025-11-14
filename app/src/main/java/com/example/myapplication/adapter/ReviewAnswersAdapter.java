package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.QuizQuestion;

import java.util.ArrayList;

public class ReviewAnswersAdapter extends RecyclerView.Adapter<ReviewAnswersAdapter.ViewHolder> {

    private final ArrayList<QuizQuestion> quizQuestions;

    public ReviewAnswersAdapter(ArrayList<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizQuestion question = quizQuestions.get(position);
        Context context = holder.itemView.getContext();

        holder.questionNumberText.setText(String.format("Question %d", position + 1));
        holder.questionText.setText(question.getQuestionText());
        holder.yourAnswerText.setText(question.getUserAnswer());

        if (question.wasCorrect()) {
            holder.yourAnswerText.setTextColor(ContextCompat.getColor(context, R.color.quiz_correct_answer));
            holder.correctAnswerLabel.setVisibility(View.GONE);
            holder.correctAnswerText.setVisibility(View.GONE);
        } else {
            holder.yourAnswerText.setTextColor(ContextCompat.getColor(context, R.color.quiz_incorrect_answer));
            holder.correctAnswerLabel.setVisibility(View.VISIBLE);
            holder.correctAnswerText.setVisibility(View.VISIBLE);
            holder.correctAnswerText.setText(question.getCorrectAnswer());
        }
    }

    @Override
    public int getItemCount() {
        return quizQuestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumberText, questionText, yourAnswerLabel, yourAnswerText, correctAnswerLabel, correctAnswerText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumberText = itemView.findViewById(R.id.question_number_text);
            questionText = itemView.findViewById(R.id.question_text);
            yourAnswerLabel = itemView.findViewById(R.id.your_answer_label);
            yourAnswerText = itemView.findViewById(R.id.your_answer_text);
            correctAnswerLabel = itemView.findViewById(R.id.correct_answer_label);
            correctAnswerText = itemView.findViewById(R.id.correct_answer_text);
        }
    }
}
