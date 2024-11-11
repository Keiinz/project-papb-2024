package com.example.memoryspark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class DeckFragment extends Fragment {

    private FirestoreHelper firestoreHelper;
    private List<Deck> deckList;
    private DeckAdapter deckAdapter;
    private ImageView profileImageView;
    private RequestQueue requestQueue;
    private static final String IMAGE_URL = "https://res.cloudinary.com/dxvcpxgzs/image/upload/v1730794317/WhatsApp_Image_2024-11-01_at_23.30.01_293b596f_wkb5ip.jpg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck, container, false);

        firestoreHelper = new FirestoreHelper();
        deckList = new ArrayList<>();
        deckAdapter = new DeckAdapter(deckList, this::onDeckSelected);

        RecyclerView deckRecyclerView = view.findViewById(R.id.deckRecyclerView);
        deckRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deckRecyclerView.setAdapter(deckAdapter);

        FloatingActionButton addDeckFab = view.findViewById(R.id.addDeckFab);
        addDeckFab.setOnClickListener(v -> showAddDeckDialog());

        // Initialize RequestQueue for Volley
        requestQueue = Volley.newRequestQueue(getContext());

        // Load profile image from URL
        profileImageView = view.findViewById(R.id.profileImageView);
        loadImageFromUrl(IMAGE_URL);

        // Set OnClickListener for profile image
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        // Fetch data from Firestore
        fetchDecks();

        return view;
    }

    private void onDeckSelected(Deck deck) {
        Intent intent = new Intent(getActivity(), CardActivity.class);
        intent.putExtra("deckId", deck.getId());
        intent.putExtra("deckName", deck.getName());
        startActivity(intent);
    }

    private void showAddDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Deck");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_deck, null);
        final TextInputEditText input = view.findViewById(R.id.deckNameInput);
        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                Deck newDeck = new Deck(null, deckName);
                firestoreHelper.addDeck(newDeck, documentReference -> {
                    String deckId = documentReference.getId();
                    newDeck.setId(deckId);
                    deckList.add(newDeck);
                    deckAdapter.notifyDataSetChanged();
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchDecks() {
        firestoreHelper.getDecks(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                deckList.clear();
                if (snapshot != null && !snapshot.isEmpty()) {
                    deckList.addAll(snapshot.toObjects(Deck.class));
                    Log.d("Firestore", "Decks fetched successfully: " + deckList.size());
                } else {
                    Log.d("Firestore", "No decks found in Firestore.");
                }
                deckAdapter.notifyDataSetChanged();
            } else {
                Log.e("Firestore", "Error fetching decks", task.getException());
                Toast.makeText(getContext(), "Failed to fetch decks", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
}
