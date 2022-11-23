package com.capstone.crypto.view.views;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.capstone.crypto.view.utils.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText idTxt;
    EditText pwdTxt;
    EditText nicknameTxt;
    Button validCheckBtn;
    Button chooseBtn;
    Button signupBtn;
    int idFlag = 0;
    int preference = -1;
    String preferenceTxt;
    DBHelper helper;
    SQLiteDatabase db;
    String confirmed;

    void initView(){
        idTxt = findViewById(R.id.idRegTxt);
        pwdTxt = findViewById(R.id.pwdRegTxt);
        nicknameTxt = findViewById(R.id.nicknameEditTxt);
        validCheckBtn = findViewById(R.id.idCheckBtn);
        chooseBtn = findViewById(R.id.jobBtn);
        signupBtn = findViewById(R.id.singupBtn);
        helper = new DBHelper(RegisterActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();

        validCheckBtn.setOnClickListener(view -> {
            String id = idTxt.getText().toString();
            Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = '" + id + "'", null);
            if(cursor.getCount() != 0){
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        idTxt.setText("");
                        Toast.makeText(RegisterActivity.this, "ID has already been used", Toast.LENGTH_SHORT);
                        idFlag = 0;
                    }
                });
            }else {
                confirmed = id;
                idFlag = 1;
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"Etherium", "bitcoin"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                dialog.setTitle("Choose Your Preferred CryptoCurrency")
                        .setSingleChoiceItems(items
                                , 0
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        preference = i+1;
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(RegisterActivity.this, "관심 분야가 선택되었습니다", Toast.LENGTH_SHORT).show();
                                if(preference != -1)
                                    preferenceTxt = (preference == 1 ? "etherium": "bitcoin");
                                chooseBtn.setText("You have Chosen :" + preferenceTxt);
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(RegisterActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                                preference = -1;
                                chooseBtn.setText("Choose CryptoCurrency");
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });

        signupBtn.setOnClickListener(view -> {
            if(idFlag == 0)
            {
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        idTxt.setText("");
                        Toast.makeText(RegisterActivity.this, "You must check validity of your ID", Toast.LENGTH_SHORT);
                        idFlag = 0;
                    }
                });
            }
            else{
                preference = (preference == -1? 1 : preference);
                ContentValues cv = new ContentValues();
                cv.put("username", confirmed);
                cv.put("password", pwdTxt.getText().toString());
                cv.put("nickname", nicknameTxt.getText().toString());
                cv.put("image", 1);
                cv.put("preference", preference);
                db.insertWithOnConflict("USERS", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("id", confirmed);
                startActivity(intent);
            }
        });
    }

}
