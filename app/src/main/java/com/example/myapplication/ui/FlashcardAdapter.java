package com.example.myapplication.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> implements Filterable {

    private List<Flashcard> flashcards;
    private List<Flashcard> flashcardsFull;
    private DatabaseHelper dbHelper;
    private final Context context;
    private final ActivityResultLauncher<Intent> editFlashcardLauncher;

    public FlashcardAdapter(Context context, List<Flashcard> flashcards, DatabaseHelper dbHelper, ActivityResultLauncher<Intent> editFlashcardLauncher) {
        this.context = context;
        this.flashcards = flashcards;
        this.flashcardsFull = new ArrayList<>(flashcards);
        this.dbHelper = dbHelper;
        this.editFlashcardLauncher = editFlashcardLauncher;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        Flashcard flashcard = flashcards.get(position);
        holder.questionTextView.setText(flashcard.getFrontContent());
        holder.answerTextView.setText(flashcard.getBackContent());

        updateFavoriteIcon(flashcard.isFavorite(), holder.favoriteIcon);

        holder.favoriteIcon.setOnClickListener(v -> {
            boolean isFavorite = !flashcard.isFavorite();
            flashcard.setFavorite(isFavorite);
            dbHelper.setFlashcardFavoriteStatus(flashcard.getId(), isFavorite);
            updateFavoriteIcon(isFavorite, holder.favoriteIcon);
        });

        holder.editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditFlashcardActivity.class);
            intent.putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_ID, flashcard.getId());
            intent.putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_QUESTION, flashcard.getFrontContent());
            intent.putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_ANSWER, flashcard.getBackContent());
            editFlashcardLauncher.launch(intent);
        });

        holder.deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Flashcard")
                    .setMessage("Are you sure you want to delete this flashcard?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        dbHelper.deleteFlashcard(flashcard.getId());
                        flashcards.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, flashcards.size());
                        Toast.makeText(context, "Flashcard deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    private void updateFavoriteIcon(boolean isFavorite, ImageView favoriteIcon) {
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite);
            favoriteIcon.setColorFilter(Color.rgb(255, 105, 180)); // Hot Pink
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
            favoriteIcon.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    @Override
    public Filter getFilter() {
        return flashcardFilter;
    }

    private final Filter flashcardFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Flashcard> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(flashcardsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Flashcard item : flashcardsFull) {
                    if (item.getFrontContent().toLowerCase().contains(filterPattern) || item.getBackContent().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            flashcards.clear();
            flashcards.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateData(List<Flashcard> newFlashcards) {
        this.flashcards.clear();
        this.flashcards.addAll(newFlashcards);
        this.flashcardsFull.clear();
        this.flashcardsFull.addAll(newFlashcards);
        notifyDataSetChanged();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, answerTextView;
        ImageView favoriteIcon, editIcon, deleteIcon;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.flashcard_question);
            answerTextView = itemView.findViewById(R.id.flashcard_answer);
            favoriteIcon = itemView.findViewById(R.id.flashcard_favorite_icon);
            editIcon = itemView.findViewById(R.id.flashcard_edit_icon);
            deleteIcon = itemView.findViewById(R.id.flashcard_delete_icon);
        }
    }
}
