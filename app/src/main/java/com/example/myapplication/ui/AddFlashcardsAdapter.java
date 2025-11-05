package com.example.myapplication.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Flashcard;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddFlashcardsAdapter extends RecyclerView.Adapter<AddFlashcardsAdapter.AddCardViewHolder> {

    private List<Flashcard> flashcardForms;

    public AddFlashcardsAdapter(List<Flashcard> flashcardForms) {
        this.flashcardForms = flashcardForms;
    }

    @NonNull
    @Override
    public AddCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_flashcard_form, parent, false);
        return new AddCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddCardViewHolder holder, int position) {
        Flashcard flashcard = flashcardForms.get(position);

        if (holder.questionWatcher != null) {
            holder.questionEditText.removeTextChangedListener(holder.questionWatcher);
        }
        if (holder.answerWatcher != null) {
            holder.answerEditText.removeTextChangedListener(holder.answerWatcher);
        }

        holder.questionEditText.setText(flashcard.getFrontContent());
        holder.answerEditText.setText(flashcard.getBackContent());
        holder.favoriteSwitch.setChecked(flashcard.isFavorite());

        holder.questionWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    flashcardForms.get(holder.getAdapterPosition()).setFrontContent(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        holder.answerWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    flashcardForms.get(holder.getAdapterPosition()).setBackContent(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        holder.questionEditText.addTextChangedListener(holder.questionWatcher);
        holder.answerEditText.addTextChangedListener(holder.answerWatcher);
        holder.favoriteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                flashcardForms.get(holder.getAdapterPosition()).setFavorite(isChecked);
            }
        });

        holder.deleteButton.setVisibility(flashcardForms.size() > 1 ? View.VISIBLE : View.GONE);
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                flashcardForms.remove(currentPosition);
                notifyDataSetChanged(); // Refresh the entire list to update delete buttons
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashcardForms.size();
    }

    public List<Flashcard> getFlashcards() {
        return flashcardForms;
    }

    static class AddCardViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText questionEditText, answerEditText;
        SwitchMaterial favoriteSwitch;
        ImageView deleteButton;
        TextWatcher questionWatcher, answerWatcher;

        public AddCardViewHolder(@NonNull View itemView) {
            super(itemView);
            questionEditText = itemView.findViewById(R.id.edit_question);
            answerEditText = itemView.findViewById(R.id.edit_answer);
            favoriteSwitch = itemView.findViewById(R.id.favorite_switch);
            deleteButton = itemView.findViewById(R.id.delete_card_button);
        }
    }
}
