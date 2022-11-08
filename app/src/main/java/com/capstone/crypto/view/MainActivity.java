package com.capstone.crypto.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText cryptoTxt = (EditText) findViewById(R.id.cryptoNameTxt);
        Button searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(view ->{
            String crypto = cryptoTxt.getText().toString();
            Intent intent = new Intent(MainActivity.this, PriceActivity.class);
            intent.putExtra("name", crypto);
            System.out.println(crypto);
            startActivity(intent);
        });
    }
}

