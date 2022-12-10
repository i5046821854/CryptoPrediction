package com.capstone.crypto.view.views;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Integer imgArr[] = {R.drawable.lee, R.drawable.cr, R.drawable.alang, R.drawable.cha};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent getIntent = getIntent();
        id = getIntent.getStringExtra("id");
        id = (id == null ? "leeyoungshin" : id);
        initVars();

        //DB connection
        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(MainActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();

        //find user info in DB
        Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = '" + id + "'", null);
        while(cursor.moveToNext()){
            id = cursor.getString(1);
            nickname = cursor.getString(3);
            image = Integer.parseInt(cursor.getString(4));
            preference = Integer.parseInt(cursor.getString(5));
        }
        imageView.setImageResource(imgArr[image]);
        searchBtn.setText("Start With " + nickname);

        cryptoTxt.setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    //move intent to main page
    public void search(View v){
        String crypto = cryptoTxt.getText().toString();
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        crypto = (preference == 1? "ethereum" : "bitcoin");
        intent.putExtra("name", crypto);
        intent.putExtra("id", id);
        intent.putExtra("nickname", nickname);
        intent.putExtra("img", image);
        startActivity(intent);
    }

    //initialize components
    void initVars(){
        cryptoTxt = (TextView) findViewById(R.id.cryptoNameTxt);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        imageView = (ImageView) findViewById(R.id.imageView3);
    }

}

