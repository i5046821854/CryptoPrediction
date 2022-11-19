package com.capstone.crypto.view.views;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.capstone.crypto.view.utils.DBHelper;

public class MainActivity extends AppCompatActivity {

    private String id;
    private int preference;
    private int image;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView cryptoTxt = (TextView) findViewById(R.id.cryptoNameTxt);
        Button searchBtn = (Button) findViewById(R.id.searchBtn);

        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(MainActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = 'leeyoungshin'", null);
        while(cursor.moveToNext()){
            id = cursor.getString(1);
            nickname = cursor.getString(3);
            image = Integer.parseInt(cursor.getString(4));
            preference = Integer.parseInt(cursor.getString(5));
        }
        searchBtn.setText("Start With " + nickname);
        searchBtn.setOnClickListener(view ->{
            String crypto = cryptoTxt.getText().toString();
            Intent intent = new Intent(MainActivity.this, PriceActivity.class);
            crypto = (preference == 1? "etherium" : "bitcoin");
            intent.putExtra("name", crypto);
            System.out.println(crypto);
            startActivity(intent);
        });

        cryptoTxt.setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

