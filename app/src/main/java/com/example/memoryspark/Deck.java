package com.example.memoryspark;

// Remove Room annotations
public class Deck {
    private String id; // Firestore document ID
    private String name;

    // No-argument constructor required for Firestore
    public Deck() {
    }

    // Constructor
    public Deck(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) { // Firestore sets the ID
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
