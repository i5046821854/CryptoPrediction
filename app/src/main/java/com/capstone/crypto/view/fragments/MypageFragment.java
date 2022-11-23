package com.capstone.crypto.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.crypto.R;
import com.capstone.crypto.view.utils.DBHelper;
import com.capstone.crypto.view.views.MenuActivity;
import com.capstone.crypto.view.views.MypageActicity;
import com.capstone.crypto.view.views.PriceActivity;


public class MypageFragment extends Fragment {

    Button chooseBtn;
    Button confirmBtn;
    EditText nicknameEdit;
    String nickname;
    Integer preference;
    String preferenceTxt;
    String ogTxt;
    String[] items = new String[]{"Etherium", "bitcoin"};
    Context context;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        context = container.getContext();
        preferenceTxt = ogTxt;
        chooseBtn = (Button) view.findViewById(R.id.jobBtn);
        confirmBtn = (Button) view.findViewById(R.id.regBtn);
        nicknameEdit = (EditText) view.findViewById(R.id.idRegTxt);
        userId = getArguments().getString("id");
        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(context, "newdb.db", null, 1);
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE username = '" + userId + "'", null);
        while(cursor.moveToNext()){
            nickname = cursor.getString(3);
            preference = Integer.parseInt(cursor.getString(5));
            ogTxt = (preference == 1 ? "ehterium" : "bitcoin");
        }

        nicknameEdit.setText(nickname);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"Etherium", "bitcoin"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
                                Toast.makeText(context, "관심 분야가 선택되었습니다", Toast.LENGTH_SHORT).show();
                                if(preference != -1)
                                    preferenceTxt = (preference == 1 ? "etherium": "bitcoin");
                                chooseBtn.setText("You have Chosen :" + preferenceTxt);
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "취소되었습니다", Toast.LENGTH_SHORT).show();
                                preference = -1;
                                chooseBtn.setText("Choose CryptoCurrency");
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
        confirmBtn.setOnClickListener(thisView -> {
            String newNickname = nicknameEdit.getText().toString();
            String query = "UPDATE USERS SET nickname = '" + newNickname + "', preference = " + preference;
            db.execSQL(query);
            Bundle bundle= new Bundle();
            bundle.putString("preference", preferenceTxt);
            System.out.println("changed into " + preferenceTxt);
            bundle.putString("id", userId);
            ((MenuActivity)getActivity()).changeFrag(1, bundle);
//            Intent confirmIntent = new Intent(context, MenuActivity.class);
//            confirmIntent.putExtra("name", preferenceTxt);
//            startActivity(confirmIntent);
        });
        return view;
    }
}