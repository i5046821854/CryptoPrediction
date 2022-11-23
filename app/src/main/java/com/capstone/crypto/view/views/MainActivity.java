package com.capstone.crypto.view.views;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.capstone.crypto.view.utils.DBHelper;

public class MainActivity extends AppCompatActivity {

    private String id;
    private int preference;
    private int image;
    private String nickname;
    private String crypto;
    private TextView cryptoTxt;
    private Button searchBtn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent getIntent = getIntent();
        id = getIntent.getStringExtra("name");
        id = (id == null ? "leeyoungshin" : id);
        System.out.println(id);
        cryptoTxt = (TextView) findViewById(R.id.cryptoNameTxt);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        imageView = (ImageView) findViewById(R.id.imageView3);

        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(MainActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = '" + id + "'", null);
        while(cursor.moveToNext()){
            id = cursor.getString(1);
            nickname = cursor.getString(3);
            image = Integer.parseInt(cursor.getString(4));
            preference = Integer.parseInt(cursor.getString(5));
        }
        searchBtn.setText("Start With " + nickname);
        cryptoTxt.setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    public void search(View v){
        String crypto = cryptoTxt.getText().toString();
//        Intent intent = new Intent(MainActivity.this, PriceActivity.class);
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        crypto = (preference == 1? "etherium" : "bitcoin");
        intent.putExtra("name", crypto);
        intent.putExtra("id", id);
        System.out.println(crypto);
        startActivity(intent);
    }

}

