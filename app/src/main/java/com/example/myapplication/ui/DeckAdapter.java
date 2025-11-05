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
import com.example.myapplication.model.Deck;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    public interface OnDeckEditListener {
        void onEditDeck(int deckId);
    }

    private List<Deck> deckList;
    private Context context;
    private OnDeckEditListener editListener;

    public DeckAdapter(Context context, List<Deck> deckList, OnDeckEditListener editListener) {
        this.context = context;
        this.deckList = deckList;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        Deck deck = deckList.get(position);
        holder.deckName.setText(deck.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudyDeckActivity.class);
            intent.putExtra(StudyDeckActivity.EXTRA_DECK_ID, deck.getId());
            intent.putExtra(StudyDeckActivity.EXTRA_DECK_NAME, deck.getName());
            context.startActivity(intent);
        });

        holder.optionsMenu.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditDeck(deck.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckName;
        ImageView optionsMenu;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.deck_name);
            optionsMenu = itemView.findViewById(R.id.deck_options_menu);
        }
    }
}
