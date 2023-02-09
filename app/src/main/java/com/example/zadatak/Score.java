package com.example.zadatak;

public class Score {
    private String phoneIdentifier;
    private int counter;

    public Score() {
        // Default constructor required for calls to DataSnapshot.getValue(Score.class)
    }

    public Score(String phoneIdentifier, int counter) {
        this.phoneIdentifier = phoneIdentifier;
        this.counter = counter;
    }

    public String getPhoneIdentifier() {
        return phoneIdentifier;
    }

    public int getCounter() {
        return counter;
    }
}

