package com.example.memoryspark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private List<Deck> deckList;
    private OnDeckSelectedListener listener;

    public interface OnDeckSelectedListener {
        void onDeckSelected(Deck deck);
    }

    public DeckAdapter(List<Deck> deckList, OnDeckSelectedListener listener) {
        this.deckList = deckList;
        this.listener = listener;
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
        holder.deckNameTextView.setText(deck.getName());
        holder.cardView.setOnClickListener(v -> listener.onDeckSelected(deck));
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView deckNameTextView;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.deckCardView);
            deckNameTextView = itemView.findViewById(R.id.deckNameTextView);
        }
    }
}