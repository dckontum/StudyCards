package com.example.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;

import java.io.Serializable;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Flashcard> favorites;
    private Context context;
    private DatabaseHelper dbHelper;

    public FavoritesAdapter(Context context, List<Flashcard> favorites) {
        this.context = context;
        this.favorites = favorites;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_card, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Flashcard favorite = favorites.get(position);
        holder.questionText.setText(favorite.getFrontContent());
        holder.answerText.setText(favorite.getBackContent());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudyFlashcardActivity.class);
            intent.putExtra(StudyFlashcardActivity.EXTRA_FLASHCARDS, (Serializable) favorites);
            intent.putExtra(StudyFlashcardActivity.EXTRA_CURRENT_POSITION, position);
            context.startActivity(intent);
        });

        holder.favoriteIcon.setOnClickListener(v -> {
            dbHelper.setFlashcardFavoriteStatus(favorite.getId(), false);
            favorites.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favorites.size());
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView answerText;
        ImageView favoriteIcon;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            answerText = itemView.findViewById(R.id.answer_text);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }
    }
}
