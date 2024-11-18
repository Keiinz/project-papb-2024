package com.example.memoryspark;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class DeckFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference deckCollection;
    private List<Deck> deckList;
    private DeckAdapter deckAdapter;
    private ImageView profileImageView;
    private String profileImageUrl = "https://res.cloudinary.com/dxvcpxgzs/image/upload/v1730794317/WhatsApp_Image_2024-11-01_at_23.30.01_293b596f_wkb5ip.jpg"; // Replace with your actual URL

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        deckCollection = db.collection("decks");

        // Initialize deck list and adapter
        deckList = new ArrayList<>();
        deckAdapter = new DeckAdapter(deckList, new DeckAdapter.OnDeckSelectedListener() {
            @Override
            public void onDeckSelected(Deck deck) {
                openCardActivity(deck);
            }

            @Override
            public void onDeckEdit(Deck deck, int position) {
                showEditDeckDialog(deck);
            }

            @Override
            public void onDeckDelete(Deck deck, int position) {
                confirmDeleteDeck(deck);
            }
        });

        // Setup RecyclerView
        RecyclerView deckRecyclerView = view.findViewById(R.id.deckRecyclerView);
        deckRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deckRecyclerView.setAdapter(deckAdapter);

        // Setup FloatingActionButton
        FloatingActionButton addDeckFab = view.findViewById(R.id.addDeckFab);
        addDeckFab.setOnClickListener(v -> showAddDeckDialog());

        // Set up profile image
        profileImageView = view.findViewById(R.id.profileImageView);
        // Load image using Volley or any image loading library
        // Example using Volley:
        com.android.volley.toolbox.ImageRequest imageRequest = new com.android.volley.toolbox.ImageRequest(
                profileImageUrl,
                response -> profileImageView.setImageBitmap(response),
                0, // max width
                0, // max height
                ImageView.ScaleType.CENTER_CROP,
                null,
                error -> Toast.makeText(getContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show()
        );
        com.android.volley.RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(getContext());
        requestQueue.add(imageRequest);

        // Set OnClickListener for profile image
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        // Listen for real-time updates
        deckCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error loading decks.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    Deck deck = dc.getDocument().toObject(Deck.class);
                    deck.setId(dc.getDocument().getId());
                    switch (dc.getType()) {
                        case ADDED:
                            deckList.add(deck);
                            deckAdapter.notifyItemInserted(deckList.size() - 1);
                            break;
                        case MODIFIED:
                            int index = getDeckIndexById(deck.getId());
                            if (index != -1) {
                                deckList.set(index, deck);
                                deckAdapter.notifyItemChanged(index);
                            }
                            break;
                        case REMOVED:
                            index = getDeckIndexById(deck.getId());
                            if (index != -1) {
                                deckList.remove(index);
                                deckAdapter.notifyItemRemoved(index);
                            }
                            break;
                    }
                }
            }
        });

        return view;
    }

    private int getDeckIndexById(String id) {
        for (int i = 0; i < deckList.size(); i++) {
            if (deckList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
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
                addDeckToFirestore(newDeck);
            } else {
                Toast.makeText(getContext(), "Please enter a deck name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Add Deck to Firestore
    private void addDeckToFirestore(Deck deck) {
        deckCollection.add(deck)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Deck added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add deck", Toast.LENGTH_SHORT).show();
                });
    }

    // Show Edit Deck Dialog
    private void showEditDeckDialog(Deck deck) {
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
                updateDeckInFirestore(deck);
            } else {
                Toast.makeText(getContext(), "Please enter a deck name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Update Deck in Firestore
    private void updateDeckInFirestore(Deck deck) {
        deckCollection.document(deck.getId()).set(deck)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Deck updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update deck", Toast.LENGTH_SHORT).show();
                });
    }

    // Confirm Delete Deck
    private void confirmDeleteDeck(Deck deck) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Deck");
        builder.setMessage("Are you sure you want to delete this deck?");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteDeckFromFirestore(deck));
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Delete Deck from Firestore
    private void deleteDeckFromFirestore(Deck deck) {
        deckCollection.document(deck.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Deck deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete deck", Toast.LENGTH_SHORT).show();
                });
    }
}
