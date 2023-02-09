package com.example.zadatak;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mfDatabase;

    public FirebaseDatabaseHelper() {
        mfDatabase = FirebaseDatabase.getInstance("https://rune-clicker-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public DatabaseReference getDatabaseReference() {
        return mfDatabase.getReference();
    }
}
