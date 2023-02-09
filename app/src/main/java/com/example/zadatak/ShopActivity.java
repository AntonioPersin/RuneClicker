package com.example.zadatak;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

public class ShopActivity extends AppCompatActivity {

    private int counter;
    private FloatingActionButton back_btn;
    private TextView finger1Name;
    private TextView finger1Cost;
    private TextView finger1Owned;
    private Button createTiredFingerHandlersButton;

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
        finger1Name = findViewById(R.id.shopItem1);
        finger1Cost = findViewById(R.id.shopItem1Cost);
        finger1Owned = findViewById(R.id.shopItem1Owned);
        createTiredFingerHandlersButton = findViewById(R.id.shopItem1Buy);

        //getting data
        counter = getIntent().getIntExtra("counter", 0);
        handlerCountersMap = getFingerOwnedAmounts();


        //TODO autofill from list? also update rest of the code
        // Tired finger count
        finger1Name.setText("Tired Finger");
        finger1Cost.setText(Integer.toString(mDatabaseHelper.getCostByName("Tired Finger")));
        finger1Owned.setText(Integer.toString(mDatabaseHelper.getOwnedByName("Tired Finger")));

        //Close button
        back_btn=findViewById(R.id.backButton);
        back_btn.setOnClickListener(view -> onBackPressed());

        //summon tired finger button disable
        if(handlerCountersMap.get("Tired Finger")==5){
            createTiredFingerHandlersButton.setEnabled(false);
        }

        //fragment
        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DescFragment fragment = new DescFragment();
        fragmentTransaction.add(R.id.descFragmentContainer, fragment);
        fragmentTransaction.commit();

        DescFragment fragment1 = (DescFragment) getSupportFragmentManager().findFragmentById(R.id.descFragment);
        TextView textViewF = fragment1.getView().findViewById(R.id.itemDescription);
        textViewF.setText("not gonna work");*/
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
            finger1Owned.setText(String.valueOf(amountTF));
            if(amountTF>=5){
                createTiredFingerHandlersButton.setEnabled(false);
            }
        }else if(counter<cost){
            //TODO fragment message "You do not have enough runes."

        }
        handlerCountersMap=getFingerOwnedAmounts();
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

    /*@Override
    public void onButtonClick(String text) {
        DescFragment fragment = (DescFragment) getSupportFragmentManager().findFragmentById(R.id.descFragment);
        TextView textView = fragment.getView().findViewById(R.id.itemDescription);
        textView.setText(text);
    }*/
}