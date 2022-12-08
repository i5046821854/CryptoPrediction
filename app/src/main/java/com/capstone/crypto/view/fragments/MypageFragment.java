package com.capstone.crypto.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.crypto.R;
import com.capstone.crypto.view.model.Chat;
import com.capstone.crypto.view.utils.DBHelper;
import com.capstone.crypto.view.views.MenuActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class MypageFragment extends Fragment {

    public Boolean occupy;
    Button chooseBtn;
    Button confirmBtn;
    Button imageChooseBtn;
    EditText nicknameEdit;
    String nickname;
    Integer preference;

    Integer imgIdx;
    String preferenceTxt;
    String ogTxt;
    String[] items = new String[]{"Ethereum", "bitcoin", "Terra", "Solana", "XRP", "Tether", "USD Coin"};
    Context context;
    String userId;
    HashSet<String> keyList = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        context = container.getContext();
        imageChooseBtn = (Button) view.findViewById(R.id.imageBtn);
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
            imgIdx = Integer.parseInt(cursor.getString(4));
            preference = Integer.parseInt(cursor.getString(5));
            ogTxt = (preference == 1 ? "ethereum" : "bitcoin");
        }
        preferenceTxt = ogTxt;
        nicknameEdit.setText(nickname);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"Ethereum", "bitcoin"};
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
                                    preferenceTxt = (preference == 1 ? "ethereum": "bitcoin");
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("message");


        ValueEventListener listener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach( child-> {
                    Chat chat = child.getValue(Chat.class);
                    if(chat.getId().equals(userId))
                        keyList.add(child.getKey());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addListenerForSingleValueEvent(listener);
        ref.removeEventListener(listener);

        imageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.imgdialog_layout);
                dialog.setTitle("custom dialog !!");
                ImageView img1 = (ImageView) dialog.findViewById(R.id.image1);
                ImageView img2 = (ImageView) dialog.findViewById(R.id.image2);
                ImageView img3 = (ImageView) dialog.findViewById(R.id.image3);
                ImageView img4 = (ImageView) dialog.findViewById(R.id.image4);
                dialog.show();
                img1.setOnClickListener(thisView -> {
                    imgIdx = 0;
                    imageChooseBtn.setText("lee.jpg");
                    dialog.dismiss();
                });
                img2.setOnClickListener(thisView -> {
                    imgIdx = 1;
                    imageChooseBtn.setText("ronaldo.jpg");
                    dialog.dismiss();
                });
                img3.setOnClickListener(thisView -> {
                    imgIdx = 2;
                    imageChooseBtn.setText("delon.jpg");
                    dialog.dismiss();
                });
                img4.setOnClickListener(thisView -> {
                    imgIdx = 3;
                    imageChooseBtn.setText("cha.jpg");
                    dialog.dismiss();
                });
            }

        });

        confirmBtn.setOnClickListener(thisView -> {
            String newNickname = nicknameEdit.getText().toString();
            String query = "UPDATE USERS SET nickname = '" + newNickname + "', preference = " + preference + ", image = " + imgIdx;
            db.execSQL(query);
            Bundle bundle = new Bundle();
            bundle.putString("preference", preferenceTxt);
            bundle.putString("id", userId);
            bundle.putInt("img", imgIdx);
            bundle.putString("nickname", newNickname);
            for(String key : keyList){
                HashMap<String, Object> change = new HashMap<>();
                change.put("image", imgIdx);
                change.put("nickname", newNickname);
                ref.child(key).updateChildren(change);
            }
            ((MenuActivity)getActivity()).changeFrag(1, bundle);
        });

        return view;
    }

}