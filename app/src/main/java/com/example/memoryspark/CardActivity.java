package com.example.memoryspark;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import javax.annotation.Nullable;

public class CardActivity extends AppCompatActivity implements CardAdapter.OnCardSelectedListener {

    private FirebaseFirestore db;
    private CollectionReference cardCollection;
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

        if (deckId == null || deckName == null || deckName.isEmpty()) {
            Toast.makeText(this, "Invalid deck. Returning to main screen.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(deckName);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        cardCollection = db.collection("decks").document(deckId).collection("cards");

        // Initialize card list and adapter
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList, this); // Pass 'this' as the listener

        // Setup RecyclerView
        RecyclerView cardRecyclerView = findViewById(R.id.cardRecyclerView);
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardRecyclerView.setAdapter(cardAdapter);

        // Setup FloatingActionButton
        FloatingActionButton addCardBtn = findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(view -> showAddCardDialog());

        // Listen for real-time updates
        cardCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(CardActivity.this, "Error loading cards.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    Card card = dc.getDocument().toObject(Card.class);
                    card.setId(dc.getDocument().getId());
                    switch (dc.getType()) {
                        case ADDED:
                            cardList.add(card);
                            cardAdapter.notifyItemInserted(cardList.size() - 1);
                            break;
                        case MODIFIED:
                            int index = getCardIndexById(card.getId());
                            if (index != -1) {
                                cardList.set(index, card);
                                cardAdapter.notifyItemChanged(index);
                            }
                            break;
                        case REMOVED:
                            index = getCardIndexById(card.getId());
                            if (index != -1) {
                                cardList.remove(index);
                                cardAdapter.notifyItemRemoved(index);
                            }
                            break;
                    }
                }
            }
        });
    }

    private int getCardIndexById(String id) {
        for (int i = 0; i < cardList.size(); i++) {
            if (cardList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
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
                Card newCard = new Card(question, answer, deckId);
                addCardToFirestore(newCard);
            } else {
                Toast.makeText(CardActivity.this, "Please enter both question and answer", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Add Card to Firestore
    private void addCardToFirestore(Card card) {
        cardCollection.add(card)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CardActivity.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CardActivity.this, "Failed to add card", Toast.LENGTH_SHORT).show();
                });
    }

    // Show Edit Card Dialog
    private void showEditCardDialog(Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Card");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_card, null);
        final TextInputEditText questionInput = view.findViewById(R.id.questionInput);
        final TextInputEditText answerInput = view.findViewById(R.id.answerInput);
        questionInput.setText(card.getQuestion());
        answerInput.setText(card.getAnswer());
        builder.setView(view);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String question = questionInput.getText().toString().trim();
            String answer = answerInput.getText().toString().trim();
            if (!question.isEmpty() && !answer.isEmpty()) {
                card.setQuestion(question);
                card.setAnswer(answer);
                updateCardInFirestore(card);
            } else {
                Toast.makeText(CardActivity.this, "Please enter both question and answer", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Update Card in Firestore
    private void updateCardInFirestore(Card card) {
        cardCollection.document(card.getId()).set(card)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CardActivity.this, "Card updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CardActivity.this, "Failed to update card", Toast.LENGTH_SHORT).show();
                });
    }

    // Confirm Delete Card
    private void confirmDeleteCard(Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Card");
        builder.setMessage("Are you sure you want to delete this card?");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteCardFromFirestore(card));
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Delete Card from Firestore
    private void deleteCardFromFirestore(Card card) {
        cardCollection.document(card.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CardActivity.this, "Card deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CardActivity.this, "Failed to delete card", Toast.LENGTH_SHORT).show();
                });
    }

    // Implementation of the new interface method to handle single clicks
    @Override
    public void onCardClicked(Card card) {
        showCardDetailsDialog(card);
    }

    // Method to display card details in a dialog
    private void showCardDetailsDialog(Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Card Details");

        // Inflate a custom layout for the dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_card_details, null);
        TextView questionTextView = view.findViewById(R.id.detailQuestionTextView);
        TextView answerTextView = view.findViewById(R.id.detailAnswerTextView);

        // Set the question and answer
        questionTextView.setText(card.getQuestion());
        answerTextView.setText(card.getAnswer());

        builder.setView(view);

        // Add an OK button
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Implementation of the interface methods
    @Override
    public void onCardEdit(Card card, int position) {
        showEditCardDialog(card);
    }

    @Override
    public void onCardDelete(Card card, int position) {
        confirmDeleteCard(card);
    }
}
