package com.example.memoryspark;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreHelper {
    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addDeck(Deck deck, OnSuccessListener<DocumentReference> onSuccessListener) {
        CollectionReference decks = db.collection("decks");
        decks.add(deck).addOnSuccessListener(documentReference -> {
            String deckId = documentReference.getId();
            deck.setId(deckId);
            documentReference.set(deck).addOnSuccessListener(aVoid -> {
                if (onSuccessListener != null) {
                    onSuccessListener.onSuccess(documentReference);
                }
            });
        });
    }

    public void addCardToDeck(String deckId, Card card, OnSuccessListener<Void> onSuccessListener) {
        DocumentReference cardRef = db.collection("decks")
                .document(deckId)
                .collection("cards")
                .document();

        card.setId(cardRef.getId());
        cardRef.set(card).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void getDecks(OnCompleteListener<QuerySnapshot> onComplete) {
        db.collection("decks").get().addOnCompleteListener(onComplete);
    }

    public void getCardsFromDeck(String deckId, OnCompleteListener<QuerySnapshot> onComplete) {
        db.collection("decks").document(deckId).collection("cards").get().addOnCompleteListener(onComplete);
    }
}