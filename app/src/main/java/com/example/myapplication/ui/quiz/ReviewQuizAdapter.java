package com.example.myapplication.ui.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.QuizQuestion;

import java.util.List;

public class ReviewQuizAdapter extends RecyclerView.Adapter<ReviewQuizAdapter.ReviewViewHolder> {

    private final List<QuizQuestion> quizQuestions;
    private final Context context;

    public ReviewQuizAdapter(Context context, List<QuizQuestion> quizQuestions) {
        this.context = context;
        this.quizQuestions = quizQuestions;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        QuizQuestion question = quizQuestions.get(position);

        holder.questionNumber.setText("Question " + (position + 1));
        holder.questionText.setText(question.getQuestionText());
        holder.yourAnswerText.setText(question.getUserAnswer());
        holder.correctAnswerText.setText(question.getCorrectAnswer());

        if (question.wasCorrect()) {
            holder.yourAnswerIcon.setImageResource(R.drawable.ic_check);
            holder.yourAnswerIcon.setColorFilter(ContextCompat.getColor(context, R.color.correct_green));
        } else {
            holder.yourAnswerIcon.setImageResource(R.drawable.ic_close);
            holder.yourAnswerIcon.setColorFilter(ContextCompat.getColor(context, R.color.incorrect_red));
        }
    }

    @Override
    public int getItemCount() {
        return quizQuestions.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumber, questionText, yourAnswerText, correctAnswerText;
        ImageView yourAnswerIcon;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumber = itemView.findViewById(R.id.review_question_number);
            questionText = itemView.findViewById(R.id.review_question_text);
            yourAnswerText = itemView.findViewById(R.id.review_your_answer_text);
            correctAnswerText = itemView.findViewById(R.id.review_correct_answer_text);
            yourAnswerIcon = itemView.findViewById(R.id.review_your_answer_icon);
        }
    }
}
