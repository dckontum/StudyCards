package com.example.myapplication.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Deck;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    // Interface to communicate with MainActivity
    public interface OnDeckEditListener {
        void onEditDeck(int deckId);
    }

    private List<Deck> deckList;
    private Context context;
    private DatabaseHelper dbHelper;
    private OnDeckEditListener editListener; // Listener instance

    // Updated constructor to accept the listener
    public DeckAdapter(Context context, List<Deck> deckList, OnDeckEditListener listener) {
        this.context = context;
        this.deckList = deckList;
        this.dbHelper = new DatabaseHelper(context);
        this.editListener = listener;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        Deck deck = deckList.get(position);
        holder.deckName.setText(deck.getName());
        holder.deckDescription.setText(deck.getDescription());

        int cardCount = dbHelper.getCardCountForDeck(deck.getId());
        holder.deckCardCount.setText(cardCount + " Cards");

        try {
            int drawableId = context.getResources().getIdentifier(deck.getIconKey(), "drawable", context.getPackageName());
            Drawable icon = AppCompatResources.getDrawable(context, drawableId);
            int color = Color.parseColor(deck.getColor());
            holder.cardView.setCardBackgroundColor(color);
            holder.deckIcon.setImageDrawable(getTintedDrawable(icon, Color.WHITE));
        } catch (Exception e) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#7C4DFF"));
            holder.deckIcon.setImageResource(R.drawable.ic_book);
        }

        holder.optionsMenu.setOnClickListener(v -> showPopupMenu(v, deck, position));
    }

    private void showPopupMenu(View view, Deck deck, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.deck_options_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit_deck) {
                // Instead of starting activity, call the listener
                if (editListener != null) {
                    editListener.onEditDeck(deck.getId());
                }
                return true;
            } else if (itemId == R.id.action_delete_deck) {
                showDeleteConfirmationDialog(deck, position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showDeleteConfirmationDialog(Deck deck, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Deck")
                .setMessage("Are you sure you want to delete the deck '" + deck.getName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteDeck(deck.getId());
                    deckList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, deckList.size());
                    Toast.makeText(context, "Deck deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private Drawable getTintedDrawable(Drawable drawable, int color) {
        if (drawable == null) return null;
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckName, deckDescription, deckCardCount;
        ImageView deckIcon, optionsMenu;
        MaterialCardView cardView;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            deckName = itemView.findViewById(R.id.deck_name);
            deckDescription = itemView.findViewById(R.id.deck_description);
            deckCardCount = itemView.findViewById(R.id.deck_card_count);
            deckIcon = itemView.findViewById(R.id.deck_icon);
            optionsMenu = itemView.findViewById(R.id.deck_options_menu);
        }
    }
}
