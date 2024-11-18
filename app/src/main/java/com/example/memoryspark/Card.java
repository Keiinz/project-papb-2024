package com.example.memoryspark;

// Remove Room annotations
public class Card {
    private String id; // Firestore document ID
    private String question;
    private String answer;
    private String deckId; // Foreign key referencing Deck (using String ID)

    // No-argument constructor required for Firestore
    public Card() {
    }

    // Constructor
    public Card(String question, String answer, String deckId) {
        this.question = question;
        this.answer = answer;
        this.deckId = deckId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) { // Firestore sets the ID
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }
}
