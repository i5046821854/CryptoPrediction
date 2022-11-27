package com.capstone.crypto.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatFragment extends Fragment {

    public static ArrayList<Chat> chatList;
    private static ChatListViewAdapter adapter;
    ListView listView;
    Context context;
    Button sendBtn;
    EditText chatText;
    String userId;
    TextView titleTxt;
    FirebaseDatabase database;
    DatabaseReference ref;
    Integer image;
    private String preference;
    ChildEventListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatList = new ArrayList<>();
        System.out.println("화면 시작!!");
        System.out.println(chatList.size());
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        titleTxt = view.findViewById(R.id.chatRoomTitle);
        listView = view.findViewById(R.id.chatListView);
        sendBtn = view.findViewById(R.id.sendBtn);
        chatText = view.findViewById(R.id.messageEditTxt);
        context = container.getContext();
        userId = getArguments().getString("id");
        preference = getArguments().getString("preference");
        image = getArguments().getInt("img");
        titleTxt.setText("Chat Room for " + preference.toUpperCase());

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("message");

        adapter = new ChatListViewAdapter(context, R.layout.chat_listview, chatList, userId, preference);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //builder에게 옵션주기
                builder.setTitle("Delete");
                builder.setMessage("Do you want this message to be deleted?");

                //3개 가능/ 메시지, 일어나야하는일
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

        sendBtn.setOnClickListener(thisView -> {
            Date today = new Date();
            SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
            System.out.println("zzzz");
            ref.push().setValue(new Chat(0, userId, chatText.getText().toString(), timeNow.format(today), preference, image));
//            ref.push().setValue(new Chat(0, userId, chatText.getText().toString(), LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))));
            chatText.setText("");
        });
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("애디드!!");
                Chat value = dataSnapshot.getValue(Chat.class); // 괄호 안 : 꺼낼 자료 형태
                System.out.println(value.getContent());
                if(value.getCrypto().equals(preference)){
                    chatList.add(value);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("child changed!");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addChildEventListener(listener);

        return view;
    }

    public static void changeValue(String id, int img){
        chatList.stream().filter(chat -> chat.getId().equals(id))
                .forEach(chat -> chat.setImage(img));
        adapter.notifyDataSetChanged();
    }
    

    @Override
    public void onPause() {
        System.out.println("paused");
        ref.removeEventListener(listener);
        super.onPause();
    }

}