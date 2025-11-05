package com.example.myapplication.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.model.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> implements Filterable {

    private List<Deck> deckList;
    private List<Deck> deckListFull;
    private Context context;
    private DatabaseHelper dbHelper;

    public DeckAdapter(Context context, List<Deck> deckList) {
        this.context = context;
        this.deckList = deckList;
        this.deckListFull = new ArrayList<>(deckList);
        this.dbHelper = new DatabaseHelper(context);
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

        int cardCount = dbHelper.getCardCountForDeck(deck.getId());
        holder.cardCount.setText(cardCount + " Cards");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeckDetailActivity.class);
            intent.putExtra(QuizActivity.EXTRA_DECK_ID, deck.getId());
            intent.putExtra(StudyDeckActivity.EXTRA_DECK_NAME, deck.getName());
            context.startActivity(intent);
        });

        holder.optionsMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.optionsMenu);
            popup.inflate(R.menu.deck_item_menu);

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit_deck) {
                    Intent intent = new Intent(context, EditDeckActivity.class);
                    intent.putExtra(EditDeckActivity.EXTRA_DECK_ID, deck.getId());
                    context.startActivity(intent);
                    return true;
                } else if (itemId == R.id.action_delete_deck) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Deck")
                            .setMessage("Are you sure you want to delete this deck?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                int currentPosition = holder.getAdapterPosition();
                                if (currentPosition != RecyclerView.NO_POSITION) {
                                    dbHelper.deleteDeck(deck.getId());
                                    deckList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    notifyItemRangeChanged(currentPosition, deckList.size());
                                    Toast.makeText(context, "Deck deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    @Override
    public Filter getFilter() {
        return deckFilter;
    }

    private final Filter deckFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Deck> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(deckListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Deck item : deckListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
            deckList.clear();
            deckList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckName, cardCount;
        ImageView optionsMenu;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.deck_name);
            cardCount = itemView.findViewById(R.id.deck_card_count);
            optionsMenu = itemView.findViewById(R.id.deck_options_menu);
        }
    }
}
