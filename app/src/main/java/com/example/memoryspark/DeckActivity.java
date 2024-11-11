package com.example.memoryspark;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class DeckActivity extends AppCompatActivity {

    private static final String TAG = "DeckActivity";
    private FirebaseFirestore db;
    private EditText deckNameInput;
    private Button addDeckButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck); // Ensure this layout file exists

        db = FirebaseFirestore.getInstance();
        deckNameInput = findViewById(R.id.deckNameInput); // Your EditText for deck name
        addDeckButton = findViewById(R.id.addDeckButton); // Your button for adding deck

        addDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDeck();
            }
        });
    }

    private void createDeck() {
        String deckName = deckNameInput.getText().toString().trim();

        if (deckName.isEmpty()) {
            deckNameInput.setError("Deck name is required");
            return;
        }

        CollectionReference decksRef = db.collection("decks");
        DocumentReference newDeckRef = decksRef.document();
        String deckId = newDeckRef.getId();

        // Create a new deck object
        Map<String, Object> deck = new HashMap<>();
        deck.put("name", deckName);
        deck.put("id", deckId);  // Set the document ID

        // Save the deck to Firestore
        newDeckRef.set(deck)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Deck added with ID: " + deckId);
                        // Optionally clear the input field or show a success message
                        deckNameInput.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding deck", e);
                    }
                });
    }
}
