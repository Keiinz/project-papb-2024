package com.example.memoryspark;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DeckFragment extends Fragment {

    private AppDatabase db;
    private List<Deck> deckList;
    private DeckAdapter deckAdapter;
    private ImageView profileImageView;
    private RequestQueue requestQueue;
    private static final String IMAGE_URL = "https://res.cloudinary.com/dxvcpxgzs/image/upload/v1730794317/WhatsApp_Image_2024-11-01_at_23.30.01_293b596f_wkb5ip.jpg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck, container, false);

        // Initialize Room Database
        db = AppDatabase.getInstance(getContext());

        // Initialize deck list and adapter
        deckList = new ArrayList<>();
        deckAdapter = new DeckAdapter(deckList, new DeckAdapter.OnDeckSelectedListener() {
            @Override
            public void onDeckSelected(Deck deck) {
                openCardActivity(deck);
            }

            @Override
            public void onDeckEdit(Deck deck, int position) {
                showEditDeckDialog(deck, position);
            }

            @Override
            public void onDeckDelete(Deck deck, int position) {
                confirmDeleteDeck(deck, position);
            }
        });

        // Setup RecyclerView
        RecyclerView deckRecyclerView = view.findViewById(R.id.deckRecyclerView);
        deckRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deckRecyclerView.setAdapter(deckAdapter);

        // Setup FloatingActionButton
        FloatingActionButton addDeckFab = view.findViewById(R.id.addDeckFab);
        addDeckFab.setOnClickListener(v -> showAddDeckDialog());

        // Initialize Volley Request Queue
        requestQueue = Volley.newRequestQueue(getContext());

        // Load profile image from URL
        profileImageView = view.findViewById(R.id.profileImageView);
        loadImageFromUrl(IMAGE_URL);

        // Set OnClickListener for profile image
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        // Fetch decks from Room database
        fetchDecks();

        return view;
    }

    private void openCardActivity(Deck deck) {
        Intent intent = new Intent(getActivity(), CardActivity.class);
        intent.putExtra("deckId", deck.getId());
        intent.putExtra("deckName", deck.getName());
        startActivity(intent);
    }

    // Show Add Deck Dialog
    private void showAddDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Deck");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_deck, null);
        final TextInputEditText input = view.findViewById(R.id.deckNameInput);
        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                Deck newDeck = new Deck(deckName);
                new InsertDeckTask().execute(newDeck);
            } else {
                Toast.makeText(getContext(), "Please enter a deck name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Show Edit Deck Dialog
    private void showEditDeckDialog(Deck deck, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Deck");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_deck, null);
        final TextInputEditText input = view.findViewById(R.id.deckNameInput);
        input.setText(deck.getName()); // Pre-fill with existing deck name
        builder.setView(view);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                deck.setName(deckName);
                new UpdateDeckTask(deck, position).execute();
            } else {
                Toast.makeText(getContext(), "Please enter a deck name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Confirm Delete Deck
    private void confirmDeleteDeck(Deck deck, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Deck");
        builder.setMessage("Are you sure you want to delete this deck?");
        builder.setPositiveButton("Yes", (dialog, which) -> new DeleteDeckTask(deck, position).execute());
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Fetch decks from Room database
    private void fetchDecks() {
        new FetchDecksTask().execute();
    }

    // Load Image from URL using Volley
    private void loadImageFromUrl(String url) {
        ImageRequest imageRequest = new ImageRequest(url,
                response -> profileImageView.setImageBitmap(response),
                0, // Width
                0, // Height
                ImageView.ScaleType.CENTER_CROP,
                null, // Bitmap config
                error -> Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show());

        requestQueue.add(imageRequest);
    }

    // AsyncTask to fetch decks from Room
    private class FetchDecksTask extends AsyncTask<Void, Void, List<Deck>> {
        @Override
        protected List<Deck> doInBackground(Void... voids) {
            return db.deckDao().getAllDecks();
        }

        @Override
        protected void onPostExecute(List<Deck> decks) {
            deckList.clear();
            deckList.addAll(decks);
            deckAdapter.notifyDataSetChanged();
        }
    }

    // AsyncTask to insert a new deck into Room
    private class InsertDeckTask extends AsyncTask<Deck, Void, Void> {
        @Override
        protected Void doInBackground(Deck... decks) {
            db.deckDao().insertDeck(decks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fetchDecks();
            Toast.makeText(getContext(), "Deck added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // AsyncTask to update an existing deck in Room
    private class UpdateDeckTask extends AsyncTask<Void, Void, Boolean> {
        private Deck deck;
        private int position;

        public UpdateDeckTask(Deck deck, int position) {
            this.deck = deck;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                db.deckDao().updateDeck(deck);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                deckList.set(position, deck);
                deckAdapter.notifyItemChanged(position);
                Toast.makeText(getContext(), "Deck updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update deck", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask to delete a deck from Room
    private class DeleteDeckTask extends AsyncTask<Void, Void, Boolean> {
        private Deck deck;
        private int position;

        public DeleteDeckTask(Deck deck, int position) {
            this.deck = deck;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                db.deckDao().deleteDeck(deck);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                deckList.remove(position);
                deckAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Deck deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete deck", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
