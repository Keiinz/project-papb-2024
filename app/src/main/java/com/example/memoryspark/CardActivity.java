package com.example.memoryspark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    private FirestoreHelper firestoreHelper;
    private List<Card> cardList;
    private CardAdapter cardAdapter;
    private String deckId;
    private String deckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get intent extras
        deckId = getIntent().getStringExtra("deckId");
        deckName = getIntent().getStringExtra("deckName");

        if (deckId == null || deckId.isEmpty() || deckName == null || deckName.isEmpty()) {
            Toast.makeText(this, "Invalid deck. Returning to main screen.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(deckName);

        firestoreHelper = new FirestoreHelper();
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList);

        // Setup RecyclerView
        RecyclerView cardRecyclerView = findViewById(R.id.cardRecyclerView);
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardRecyclerView.setAdapter(cardAdapter);

        // Setup FloatingActionButton
        FloatingActionButton addCardBtn = findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(view -> showAddCardDialog());

        // Fetch Cards from Firestore
        fetchCards();
    }

    // Handle Toolbar back button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Show Add Card Dialog
    private void showAddCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Card");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_card, null);
        final TextInputEditText questionInput = view.findViewById(R.id.questionInput);
        final TextInputEditText answerInput = view.findViewById(R.id.answerInput);
        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String question = questionInput.getText().toString().trim();
            String answer = answerInput.getText().toString().trim();
            if (!question.isEmpty() && !answer.isEmpty()) {
                Card newCard = new Card(null, question, answer, deckId);
                firestoreHelper.addCardToDeck(deckId, newCard, aVoid -> {
                    fetchCards();
                    Toast.makeText(CardActivity.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(CardActivity.this, "Please enter both question and answer", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Fetch cards from the Firestore database
    private void fetchCards() {
        firestoreHelper.getCardsFromDeck(deckId, task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                cardList.clear();
                cardList.addAll(snapshot.toObjects(Card.class));
                cardAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(CardActivity.this, "Failed to fetch cards", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
