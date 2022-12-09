package com.capstone.crypto.view.views;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    Button profileBtn;
    int idFlag = 0;
    int preference = -1;
    String preferenceTxt;
    DBHelper helper;
    SQLiteDatabase db;
    String confirmed;
    int imgIdx = 0;

    void initView(){
        idTxt = findViewById(R.id.idRegTxt);
        pwdTxt = findViewById(R.id.pwdRegTxt);
        nicknameTxt = findViewById(R.id.nicknameEditTxt);
        validCheckBtn = findViewById(R.id.idCheckBtn);
        chooseBtn = findViewById(R.id.jobBtn);
        signupBtn = findViewById(R.id.singupBtn);
        profileBtn = findViewById(R.id.profileBtn);
        helper = new DBHelper(RegisterActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(RegisterActivity.this);
                dialog.setContentView(R.layout.imgdialog_layout);
                dialog.setTitle("custom dialog !!");
                ImageView img1 = (ImageView) dialog.findViewById(R.id.image1);
                ImageView img2 = (ImageView) dialog.findViewById(R.id.image2);
                ImageView img3 = (ImageView) dialog.findViewById(R.id.image3);
                ImageView img4 = (ImageView) dialog.findViewById(R.id.image4);
                dialog.show();
                img1.setOnClickListener(thisView -> {
                    imgIdx = 0;
                    profileBtn.setText("lee.jpg");
                    dialog.dismiss();
                });
                img2.setOnClickListener(thisView -> {
                    imgIdx = 1;
                    profileBtn.setText("ronaldo.jpg");
                    dialog.dismiss();
                });
                img3.setOnClickListener(thisView -> {
                    imgIdx = 2;
                    profileBtn.setText("delon.jpg");
                    dialog.dismiss();
                });
                img4.setOnClickListener(thisView -> {
                    imgIdx = 3;
                    profileBtn.setText("cha.jpg");
                    dialog.dismiss();
                });
            }

        });


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
                final String[] items = new String[]{"Ethereum", "Bitcoin"};
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
                                Toast.makeText(RegisterActivity.this, "Selection Completed!", Toast.LENGTH_SHORT).show();
                                if(preference != -1)
                                    preferenceTxt = (preference == 1 ? "ethereum": "bitcoin");
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
                cv.put("image", imgIdx);
                cv.put("preference", preference);
                db.insertWithOnConflict("USERS", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("id", confirmed);
                startActivity(intent);
            }
        });
    }

}
