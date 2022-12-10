package com.capstone.crypto.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.capstone.crypto.R;
import com.capstone.crypto.view.adapters.ChatListViewAdapter;
import com.capstone.crypto.view.model.Chat;
import com.capstone.crypto.view.views.MenuActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatFragment extends Fragment {

    public static ArrayList<Chat> chatList;
    private static ChatListViewAdapter adapter;
    private ListView listView;
    private Context context;
    private Button sendBtn;
    private EditText chatText;
    private String userId;
    private TextView titleTxt;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Integer image;
    private String nickname;
    private String preference;
    private ChildEventListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //initialize variables
        titleTxt = view.findViewById(R.id.chatRoomTitle);
        listView = view.findViewById(R.id.chatListView);
        sendBtn = view.findViewById(R.id.sendBtn);
        chatText = view.findViewById(R.id.messageEditTxt);
        context = container.getContext();
        Bundle bundle = getArguments();
        userId = getArguments().getString("id");
        preference = getArguments().getString("preference");
        image = getArguments().getInt("img");
        nickname = getArguments().getString("nickname");
        titleTxt.setText("Chat Room for " + preference.toUpperCase());

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("message");

        adapter = new ChatListViewAdapter(context, R.layout.chat_listview, chatList, userId, preference);
        listView.setAdapter(adapter);

        //delete message
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Do you want this message to be deleted?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        chatList.remove(position);
                        adapter.notifyDataSetChanged();//새로고침
                    }
                });
                //취소
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();

                return false; //true하면 일반클릭과 롱클릭 둘다 먹고 false하면 롱클릭만 먹는다
            }
        });

        //send message
        sendBtn.setOnClickListener(thisView -> {
            Date today = new Date();
            SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
            ref.push().setValue(new Chat(0, userId, nickname, chatText.getText().toString(), timeNow.format(today), preference, image));
            chatText.setText("");
        });


        //listener when other users send message to DB
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat value = dataSnapshot.getValue(Chat.class);
                if(value.getCrypto().equals(preference)){
                    chatList.add(value);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ((MenuActivity)getActivity()).changeFrag(3, bundle);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        ref.addChildEventListener(listener);
        return view;
    }


    @Override
    public void onPause() {
        ref.removeEventListener(listener);
        super.onPause();
    }

}