package com.capstone.crypto.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.capstone.crypto.R;
import com.capstone.crypto.view.model.Chat;

import java.util.ArrayList;

public class ChatListViewAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Chat> chatData;
    private LayoutInflater inflater;
    private String userId;

    public ChatListViewAdapter(Context context, int chat_listview, ArrayList<Chat> array, String userId) {
        this.context = context;
        this.layout = chat_listview;
        this.chatData = array;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println(chatData.get(position).getContent());
        System.out.println(chatData.get(position).getTime());
        if(convertView == null){
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.chat_listview, parent, false);

            convertView = inflater.inflate(layout, parent, false); //아이디를 가지고 view를 만든다
        }
        TextView tv_msg = (TextView)convertView.findViewById(R.id.tv_content);
        TextView tv_time = (TextView)convertView.findViewById(R.id.tv_time);
        TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
        TextView my_msg = (TextView)convertView.findViewById(R.id.my_content);
        TextView my_time = (TextView)convertView.findViewById(R.id.my_time);
        TextView my_name = (TextView)convertView.findViewById(R.id.my_name);

        if(chatData.get(position).getId().equals(userId)){
            tv_time.setVisibility(View.GONE);
            tv_name.setVisibility(View.GONE);
            tv_msg.setVisibility(View.GONE);

            my_msg.setVisibility(View.VISIBLE);
            my_time.setVisibility(View.VISIBLE);
            System.out.println("right");
            my_name.setText("나");
            my_time.setText(chatData.get(position).getTime());
            my_msg.setText(chatData.get(position).getContent());
        }
        else{
            tv_time.setVisibility(View.VISIBLE);
            tv_name.setVisibility(View.VISIBLE);
            tv_msg.setVisibility(View.VISIBLE);

            my_msg.setVisibility(View.GONE);
            my_time.setVisibility(View.GONE);
            System.out.println("wrong");
            tv_time.setText(chatData.get(position).getTime());
            tv_msg.setText(chatData.get(position).getContent());
            tv_name.setText(chatData.get(position).getId());
        }

        return convertView;
    }

}
