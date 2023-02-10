package com.example.zadatak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements DescFragment.UpdateTextViewListener {

    private int counter;
    private FloatingActionButton back_btn;
    private Button createTiredFingerHandlersButton;
    private Button createFurledFingerHandlersButton;
    private Button createWizenedFingerHandlersButton;
    private Button createBloodyFingerHandlersButton;
    private Button createRecusantFingerHandlersButton;
    private HashMap<String, Integer> handlerCountersMap;
    private DatabaseHelper mDatabaseHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //assigns
        mDatabaseHelper = new DatabaseHelper(this);
        handlerCountersMap = new HashMap<>();
        createTiredFingerHandlersButton = findViewById(R.id.shopItem1Buy);
        createFurledFingerHandlersButton = findViewById(R.id.shopItem2Buy);
        createWizenedFingerHandlersButton = findViewById(R.id.shopItem3Buy);
        createBloodyFingerHandlersButton = findViewById(R.id.shopItem4Buy);
        createRecusantFingerHandlersButton = findViewById(R.id.shopItem5Buy);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ArrayList<ArrayList<TextView>> shopItems=new ArrayList<>(List.of(
                new ArrayList<>(List.of((TextView)findViewById(R.id.shopItem1), (TextView)findViewById(R.id.shopItem1Cost), (TextView)findViewById(R.id.shopItem1Owned))),
                new ArrayList<>(List.of((TextView)findViewById(R.id.shopItem2), (TextView)findViewById(R.id.shopItem2Cost), (TextView)findViewById(R.id.shopItem2Owned))),
                new ArrayList<>(List.of((TextView)findViewById(R.id.shopItem3), (TextView)findViewById(R.id.shopItem3Cost), (TextView)findViewById(R.id.shopItem3Owned))),
                new ArrayList<>(List.of((TextView)findViewById(R.id.shopItem4), (TextView)findViewById(R.id.shopItem4Cost), (TextView)findViewById(R.id.shopItem4Owned))),
                new ArrayList<>(List.of((TextView)findViewById(R.id.shopItem5), (TextView)findViewById(R.id.shopItem5Cost), (TextView)findViewById(R.id.shopItem5Owned)))
        ));


        //getting data
        counter = getIntent().getIntExtra("counter", 0);
        handlerCountersMap = getFingerOwnedAmounts();

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String selectQuery = "SELECT " + DatabaseHelper.KEY_FINGER_NAME + "," +
                DatabaseHelper.KEY_FINGER_COST + "," + DatabaseHelper.KEY_FINGER_OWNED +
                " FROM " + DatabaseHelper.TABLE_FINGERS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        for (List<TextView> textViewList : shopItems) {
            if (cursor.moveToNext()) {
                String fingerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FINGER_NAME));
                int fingerCost = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FINGER_COST));
                int fingerOwned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FINGER_OWNED));
                textViewList.get(0).setText(fingerName);
                textViewList.get(1).setText(String.valueOf(fingerCost));
                textViewList.get(2).setText(String.valueOf(fingerOwned));
            }
        }
        cursor.close();

        //Close button
        back_btn=findViewById(R.id.backButton);
        back_btn.setOnClickListener(view -> onBackPressed());

        //summon tired finger button disable
        if(handlerCountersMap.get("Tired Finger")==5){
            createTiredFingerHandlersButton.setEnabled(false);
        }
        if(handlerCountersMap.get("Furled Finger")==5){
            createFurledFingerHandlersButton.setEnabled(false);
        }
        if(handlerCountersMap.get("Wizened Finger")==5){
            createWizenedFingerHandlersButton.setEnabled(false);
        }
        if(handlerCountersMap.get("Bloody Finger")==3){
            createBloodyFingerHandlersButton.setEnabled(false);
        }
        if(handlerCountersMap.get("Recusant Finger")==2){
            createRecusantFingerHandlersButton.setEnabled(false);
        }

        //fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DescFragment fragment = new DescFragment();
        fragmentTransaction.add(R.id.descFragmentContainer, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("counter", counter);
        startActivity(intent);
        finish();
    }

    public void createTiredFingerHandlers(View v){
        int amountTF=handlerCountersMap.get("Tired Finger");
        int cost=mDatabaseHelper.getCostByName("Tired Finger");
        if (amountTF <5 && counter>=cost) {
            counter=counter-cost;
            amountTF++;
            mDatabaseHelper.incrementOwnedByName("Tired Finger");
            TextView finger1Owned=findViewById(R.id.shopItem1Owned);
            finger1Owned.setText(String.valueOf(amountTF));
            if(amountTF>=5){
                createTiredFingerHandlersButton.setEnabled(false);
            }
        }else if(counter<cost){
            createTiredFingerHandlersButton.setEnabled(false);

        }
        handlerCountersMap=getFingerOwnedAmounts();
        String text = mDatabaseHelper.getDescriptionByName("Tired Finger");
        updateText(text);
    }

    public void createFurledFingerHandlers(View v){
        int amountTF=handlerCountersMap.get("Furled Finger");
        int cost=mDatabaseHelper.getCostByName("Furled Finger");
        if (amountTF <5 && counter>=cost) {
            counter=counter-cost;
            amountTF++;
            mDatabaseHelper.incrementOwnedByName("Furled Finger");
            TextView finger2Owned=findViewById(R.id.shopItem2Owned);
            finger2Owned.setText(String.valueOf(amountTF));
            if(amountTF>=5){
                createFurledFingerHandlersButton.setEnabled(false);
            }
        }else if(counter<cost){
            createFurledFingerHandlersButton.setEnabled(false);

        }
        handlerCountersMap=getFingerOwnedAmounts();
        String text = mDatabaseHelper.getDescriptionByName("Furled Finger");
        updateText(text);
    }

    public void createWizenedFingerHandlers(View v){
        int amountTF=handlerCountersMap.get("Wizened Finger");
        int cost=mDatabaseHelper.getCostByName("Wizened Finger");
        if (amountTF <5 && counter>=cost) {
            counter=counter-cost;
            amountTF++;
            mDatabaseHelper.incrementOwnedByName("Wizened Finger");
            TextView finger3Owned=findViewById(R.id.shopItem3Owned);
            finger3Owned.setText(String.valueOf(amountTF));
            if(amountTF>=5){
                createWizenedFingerHandlersButton.setEnabled(false);
            }
        }else if(counter<cost){
            createWizenedFingerHandlersButton.setEnabled(false);

        }
        handlerCountersMap=getFingerOwnedAmounts();
        String text = mDatabaseHelper.getDescriptionByName("Wizened Finger");
        updateText(text);
    }

    public void createBloodyFingerHandlers(View v){
        int amountTF=handlerCountersMap.get("Bloody Finger");
        int cost=mDatabaseHelper.getCostByName("Bloody Finger");
        if (amountTF <3 && counter>=cost) {
            counter=counter-cost;
            amountTF++;
            mDatabaseHelper.incrementOwnedByName("Bloody Finger");
            TextView finger4Owned=findViewById(R.id.shopItem4Owned);
            finger4Owned.setText(String.valueOf(amountTF));
            if(amountTF>=3){
                createBloodyFingerHandlersButton.setEnabled(false);
            }
        }else if(counter<cost){
            createBloodyFingerHandlersButton.setEnabled(false);

        }
        handlerCountersMap=getFingerOwnedAmounts();
        String text = mDatabaseHelper.getDescriptionByName("Bloody Finger");
        updateText(text);
    }

    public void createRecusantFingerHandlers(View v){
        int amountTF=handlerCountersMap.get("Recusant Finger");
        int cost=mDatabaseHelper.getCostByName("Recusant Finger");
        if (amountTF <2 && counter>=cost) {
            counter=counter-cost;
            amountTF++;
            mDatabaseHelper.incrementOwnedByName("Recusant Finger");
            TextView finger5Owned=findViewById(R.id.shopItem5Owned);
            finger5Owned.setText(String.valueOf(amountTF));
            if(amountTF>=2){
                createRecusantFingerHandlersButton.setEnabled(false);
            }
        }else if(counter<cost){
            createRecusantFingerHandlersButton.setEnabled(false);

        }
        handlerCountersMap=getFingerOwnedAmounts();
        String text = mDatabaseHelper.getDescriptionByName("Recusant Finger");
        updateText(text);
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

    @Override
    public void updateText(String text) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.descFragmentContainer);
        if (fragment != null) {
            TextView textView = fragment.getView().findViewById(R.id.itemDescription);
            textView.setText(text);
        }
    }
}