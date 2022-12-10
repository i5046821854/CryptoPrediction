package com.capstone.crypto.view.views;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.capstone.crypto.view.utils.DBHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText idTxt;
    private EditText pwdTxt;
    private Button signUpBtn; //회원가입 버튼
    private Button loginBtn; //로그인 버튼
    private String id;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initVars();

        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(LoginActivity.this, "newdb.db", null, 1);
        db = helper.getReadableDatabase();

        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        loginBtn.setOnClickListener(view -> {
            id = idTxt.getText().toString();
            pwd = pwdTxt.getText().toString();
            Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = '" +id + "' AND password = '" + pwd + "'", null);
            if(cursor.getCount() == 0)
            {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        idTxt.setText("");
                        pwdTxt.setText("");
                        Toast.makeText(LoginActivity.this, "TRY AGAIN!", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("id" , id);
                startActivity(intent);
            }
        });
    }

    //initialize components
    void initVars(){
        idTxt = (EditText) findViewById(R.id.id);
        pwdTxt = (EditText) findViewById(R.id.password);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
    }
}
