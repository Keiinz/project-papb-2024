package com.example.memoryspark;

public class Card {
    private String id;
    private String question;
    private String answer;
    private String deckId;

    public Card() {
        // Default constructor required for Firestore
    }

    public Card(String id, String question, String answer, String deckId) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.deckId = deckId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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