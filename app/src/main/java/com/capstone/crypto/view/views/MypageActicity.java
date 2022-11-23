package com.capstone.crypto.view.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.capstone.crypto.view.utils.DBHelper;

public class MypageActicity extends AppCompatActivity {

    Button chooseBtn;
    Button confirmBtn;
    Button imageChooseBtn;
    EditText nicknameEdit;
    String nickname;
    Integer preference;
    String preferenceTxt;
    String ogTxt;
    String[] items = new String[]{"Etherium", "bitcoin"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_layout);
        Intent intent = getIntent();
        ogTxt = intent.getStringExtra("name");
        preferenceTxt = ogTxt;
        imageChooseBtn = (Button) findViewById(R.id.imageBtn);
        chooseBtn = (Button) findViewById(R.id.jobBtn);
        confirmBtn = (Button) findViewById(R.id.regBtn);
        nicknameEdit = (EditText) findViewById(R.id.idRegTxt);
        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(MypageActicity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = 'leeyoungshin'", null);
        while(cursor.moveToNext()){
            nickname = cursor.getString(3);
            preference = Integer.parseInt(cursor.getString(5));
        }

        nicknameEdit.setText(nickname);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"Etherium", "bitcoin"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MypageActicity.this);
                dialog.setTitle("Choose Your Preferred CryptoCurrency")
                        .setSingleChoiceItems(items
                                , preference-1
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        preference = i+1;
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MypageActicity.this, "관심 분야가 선택되었습니다", Toast.LENGTH_SHORT).show();
                                if(preference != -1)
                                    preferenceTxt = (preference == 1 ? "etherium": "bitcoin");
                                    chooseBtn.setText("You have Chosen :" + preferenceTxt);
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MypageActicity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                                preference = -1;
                                chooseBtn.setText("Choose CryptoCurrency");
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
        imageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"Etherium", "bitcoin"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MypageActicity.this);
                dialog.setTitle("Choose Your Preferred CryptoCurrency")
                        .setSingleChoiceItems(items
                                , preference-1
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        preference = i+1;
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MypageActicity.this, "관심 분야가 선택되었습니다", Toast.LENGTH_SHORT).show();
                                if(preference != -1)
                                    preferenceTxt = (preference == 1 ? "etherium": "bitcoin");
                                chooseBtn.setText("You have Chosen :" + preferenceTxt);
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MypageActicity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                                preference = -1;
                                chooseBtn.setText("Choose CryptoCurrency");
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
        confirmBtn.setOnClickListener(view -> {
            String newNickname = nicknameEdit.getText().toString();
            String query = "UPDATE USERS SET nickname = '" + newNickname + "', preference = " + preference;
            db.execSQL(query);
            Intent confirmIntent = new Intent(MypageActicity.this, PriceActivity.class);
            confirmIntent.putExtra("name", preferenceTxt);
            startActivity(confirmIntent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MypageActicity.this, PriceActivity.class);
        intent.putExtra("name", ogTxt);
        startActivity(intent);
    }
}
