package com.example.memoryspark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirestoreHelper firestoreHelper;
    private List<Deck> deckList;
    private DeckAdapter deckAdapter;

    // Profile ImageView
    private ImageView profileImageView;
    private RequestQueue requestQueue;
    private static final String IMAGE_URL = "https://res.cloudinary.com/dxvcpxgzs/image/upload/v1730794317/WhatsApp_Image_2024-11-01_at_23.30.01_293b596f_wkb5ip.jpg"; // Sample Image URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firestoreHelper = new FirestoreHelper();
        deckList = new ArrayList<>();
        deckAdapter = new DeckAdapter(deckList, this::onDeckSelected);

        RecyclerView deckRecyclerView = findViewById(R.id.deckRecyclerView);
        deckRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deckRecyclerView.setAdapter(deckAdapter);

        FloatingActionButton addDeckFab = findViewById(R.id.addDeckFab);
        addDeckFab.setOnClickListener(view -> showAddDeckDialog());

        // Initialize RequestQueue for Volley
        requestQueue = Volley.newRequestQueue(this);

        // Load profile image from URL
        profileImageView = findViewById(R.id.profileImageView);
        loadImageFromUrl(IMAGE_URL);

        // Set OnClickListener for profile image
        profileImageView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Fetch data from Firestore
        fetchDecks();
    }

    private void onDeckSelected(Deck deck) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("deckId", deck.getId());
        intent.putExtra("deckName", deck.getName());
        startActivity(intent);
    }

    private void showAddDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                Toast.makeText(MainActivity.this, "Failed to fetch decks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageFromUrl(String url) {
        ImageRequest imageRequest = new ImageRequest(url,
                response -> {
                    profileImageView.setImageBitmap(response);
                },
                0, // Width
                0, // Height
                ImageView.ScaleType.CENTER_CROP,
                null, // Bitmap config
                error -> {
                    Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(imageRequest);
    }
}
