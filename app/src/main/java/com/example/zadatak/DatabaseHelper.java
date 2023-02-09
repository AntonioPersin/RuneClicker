package com.example.zadatak;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "fingersDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_FINGERS = "fingers";

    // Fingers Table Columns
    public static final String KEY_FINGER_ID = "id";
    public static final String KEY_FINGER_NAME = "fingerName";
    public static final String KEY_FINGER_COST = "fingerCost";
    public static final String KEY_FINGER_OWNED = "fingerOwned";
    public static final String KEY_FINGER_SECONDS = "fingerSeconds";
    public static final String KEY_FINGER_DESC = "fingerDesc";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FINGERS_TABLE = "CREATE TABLE " + TABLE_FINGERS +
                "(" +
                KEY_FINGER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_FINGER_NAME + " TEXT," +
                KEY_FINGER_COST + " INTEGER," +
                KEY_FINGER_OWNED + " INTEGER," +
                KEY_FINGER_SECONDS + " INTEGER," +
                KEY_FINGER_DESC + " TEXT" +
                ")";

        db.execSQL(CREATE_FINGERS_TABLE);

        //tiredFinger
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FINGER_NAME, "Tired Finger");
        contentValues.put(KEY_FINGER_COST, 10);
        contentValues.put(KEY_FINGER_OWNED, 0);
        contentValues.put(KEY_FINGER_SECONDS, 10);
        contentValues.put(KEY_FINGER_DESC, "This finger will click once every 10 seconds. You can have a maximum of 5 Tired Fingers.");
        db.insert(TABLE_FINGERS, null, contentValues);

    }

    // Called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINGERS);
            onCreate(db);
        }
    }

    public Cursor getFingerByName(String fingerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FINGERS,
                new String[]{KEY_FINGER_ID, KEY_FINGER_NAME, KEY_FINGER_COST, KEY_FINGER_OWNED, KEY_FINGER_SECONDS, KEY_FINGER_DESC},
                KEY_FINGER_NAME + "=?",
                new String[]{fingerName},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }
    public int updateOwnedByName(String fingerName, int newOwned) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FINGER_OWNED, newOwned);

        return db.update(TABLE_FINGERS, values, KEY_FINGER_NAME + " = ?",
                new String[]{fingerName});
    }
    public int incrementOwnedByName(String fingerName) {
        Cursor cursor = getFingerByName(fingerName);
        int currentOwnedValue = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FINGER_OWNED));
        int newOwnedValue = currentOwnedValue + 1;
        updateOwnedByName(fingerName, newOwnedValue);
        return newOwnedValue;
    }

    public int getOwnedByName(String fingerName) {
        Cursor cursor = getFingerByName(fingerName);
        return cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FINGER_OWNED));
    }
    public int getCostByName(String fingerName) {
        Cursor cursor = getFingerByName(fingerName);
        return cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FINGER_COST));
    }
    public String getDescriptionByName(String fingerName) {
        Cursor cursor = getFingerByName(fingerName);
        return cursor.getString(cursor.getColumnIndexOrThrow(KEY_FINGER_DESC));
    }
}
