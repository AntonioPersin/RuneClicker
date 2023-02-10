package com.example.zadatak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_KEY";
    private static final String COUNTER_KEY = "COUNTER_KEY";
    private static final String MAP_KEY = "MAP_KEY";
    private int counter;
    private TextView counterTextView;
    private int[] colors;
    private int colorIndex = 0;
    private DatabaseHelper mDatabaseHelper;
    private HashMap<String, Integer> handlerCountersMap;
    private HashMap<String, List<Handler>> handlersMap;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //all assignments
        mDatabaseHelper = new DatabaseHelper(this);
        counterTextView = findViewById(R.id.counterText);
        Button shop_btn = findViewById(R.id.shopButton);
        Button close_btn = findViewById(R.id.exitButton);
        colors = new int[]{Color.RED, Color.GREEN, Color.YELLOW};
        handlerCountersMap = getFingerOwnedAmounts();
        handlersMap = new HashMap<>();


        //setting up counter
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter");
            counterTextView.setText(String.valueOf(counter));
        }
        counterTextView.setText(String.valueOf(counter));

        //Shop button - code implementation of a button
        shop_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            intent.putExtra("counter", counter);
            startActivity(intent);
        });

        //Close button - code implementation
        close_btn.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        counter = savedInstanceState.getInt("counter");
        counterTextView.setText(String.valueOf(counter));

        handlerCountersMap= getFingerOwnedAmounts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //refresh counter when coming from shop
        counter = getIntent().getIntExtra("counter", 0);
        counterTextView.setText(String.valueOf(counter));

        //bg colors
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(colors[colorIndex]);
        colorIndex = (colorIndex + 1) % colors.length;

        //recreate handlers
        handlerCountersMap.forEach((fingerType,amount)->{
            ArrayList<Handler> tmpFingerList=new ArrayList<>();
            for (int i=0; i<amount;i++){
                tmpFingerList.add(createHandler(fingerType));
            }
            handlersMap.put(fingerType,tmpFingerList);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //colors
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(colors[colorIndex]);
        colorIndex = (colorIndex + 1) % colors.length;

        //destroy all handlers
        handlersMap.forEach((fingerType,handlerList)->{
            destroyHandlers(handlerList);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", counter);
    }


    @SuppressLint("SetTextI18n")
    public void increaseCounter(View view) {
        counter++;
        counterTextView.setText(Integer.toString(counter));
    }

    public void openWebPage(View view) {
        String url = "https://orteil.dashnet.org/cookieclicker/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private Handler createHandler(String name) {
        Cursor cursor = mDatabaseHelper.getFingerByName(name);
        long miliseconds = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FINGER_MILISECONDS));

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                increaseCounter(null);
                handler.postDelayed(this, miliseconds);
            }
        };
        handler.post(runnable);
        return handler;
    }

    private void destroyHandlers(List<Handler> array) {
        if (array != null && array.size() > 0) {
            for (Handler handler : array) {
                handler.removeCallbacksAndMessages(null);
            }
            array.clear();
        }
    }

    public void saveCounter(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNTER_KEY, counter);

        String encoded = encodeMap(handlerCountersMap);
        editor.putString(MAP_KEY, encoded);

        editor.apply();
    }

    public void loadCounter(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        counter = sharedPreferences.getInt(COUNTER_KEY, 0);
        counterTextView.setText(String.valueOf(counter));

        String encoded = sharedPreferences.getString(MAP_KEY, null);
        if (encoded != null) {
            handlerCountersMap = decodeMap(encoded);
            handlerCountersMap.forEach((fingerType,amount)->{
                mDatabaseHelper.updateOwnedByName(fingerType, amount);
            });
        }
    }

    public void resetGame(View view){
        counter=0;

        handlerCountersMap.forEach((finger,amount)->{
            mDatabaseHelper.updateOwnedByName(finger,0);
        });
        handlerCountersMap=getFingerOwnedAmounts();
        saveCounter(view);
        counterTextView.setText(String.valueOf(counter));
    }

    private HashMap<String, Integer> getFingerOwnedAmounts() {
        HashMap<String, Integer> fingerData = new HashMap<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper.KEY_FINGER_NAME + "," + DatabaseHelper.KEY_FINGER_OWNED + " FROM " + DatabaseHelper.TABLE_FINGERS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            String fingerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FINGER_NAME));
            int fingerOwned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FINGER_OWNED));
            fingerData.put(fingerName, fingerOwned);
        }
        cursor.close();
        return fingerData;
    }


    private String encodeMap(HashMap<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            String line = key + "=" + value + ";";
            sb.append(line);
        }
        return Base64.encodeToString(sb.toString().getBytes(), Base64.DEFAULT);
    }

    private HashMap<String, Integer> decodeMap(String encoded) {
        HashMap<String, Integer> result = new HashMap<>();
        byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
        String[] lines = new String(decoded).split(";");
        for (String line : lines) {
            int index = line.indexOf("=");
            if (index == -1) {
                continue;
            }
            String key = line.substring(0, index);
            Integer value = Integer.valueOf(line.substring(index + 1));
            result.put(key, value);
        }
        return result;
    }

}