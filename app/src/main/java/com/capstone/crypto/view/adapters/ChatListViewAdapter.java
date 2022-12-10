package com.capstone.crypto.view.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.crypto.R;
import com.capstone.crypto.view.model.Chat;
import com.capstone.crypto.view.utils.DBHelper;
import com.capstone.crypto.view.utils.DBconnection;

import java.util.ArrayList;


//listView Adapter for the chat room
public class ChatListViewAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Chat> chatData;
    private LayoutInflater inflater;
    private String userId;
    private String preference;
    private Integer imgArr[] = {R.drawable.lee, R.drawable.cr, R.drawable.alang, R.drawable.cha};

    public ChatListViewAdapter(Context context, int chat_listview, ArrayList<Chat> array, String userId, String preference) {
        this.context = context;
        this.layout = chat_listview;
        this.chatData = array;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userId = userId;
        this.preference = preference;
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


    //how to handle each chat message
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false); //아이디를 가지고 view를 만든다
        }

        String nickname = chatData.get(position).getNickname();
        TextView tv_msg = (TextView)convertView.findViewById(R.id.tv_content);
        TextView tv_time = (TextView)convertView.findViewById(R.id.tv_time);
        TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
        TextView my_msg = (TextView)convertView.findViewById(R.id.my_content);
        TextView my_time = (TextView)convertView.findViewById(R.id.my_time);
        TextView my_name = (TextView)convertView.findViewById(R.id.my_name);
        ImageView my_image = (ImageView)convertView.findViewById(R.id.my_image);
        ImageView tv_image = (ImageView)convertView.findViewById(R.id.tv_image);

        if(chatData.get(position).getId().equals(userId)){  //show user's message
            tv_time.setVisibility(View.GONE);
            tv_name.setVisibility(View.GONE);
            tv_msg.setVisibility(View.GONE);
            tv_image.setVisibility(View.GONE);

            my_msg.setVisibility(View.VISIBLE);
            my_time.setVisibility(View.VISIBLE);
            my_name.setVisibility(View.VISIBLE);
            my_image.setVisibility(View.VISIBLE);

            my_time.setText(chatData.get(position).getTime());
            my_msg.setText(chatData.get(position).getContent());
            my_image.setImageResource(imgArr[chatData.get(position).getImage()]);
        }
        else{   //show other users' message
            tv_time.setVisibility(View.VISIBLE);
            tv_name.setVisibility(View.VISIBLE);
            tv_msg.setVisibility(View.VISIBLE);
            tv_image.setVisibility(View.VISIBLE);

            my_msg.setVisibility(View.GONE);
            my_time.setVisibility(View.GONE);
            my_name.setVisibility(View.GONE);
            my_image.setVisibility(View.GONE);

            tv_time.setText(chatData.get(position).getTime());
            tv_msg.setText(chatData.get(position).getContent());
            tv_name.setText(nickname);
            tv_image.setImageResource(imgArr[chatData.get(position).getImage()]);
        }

        return convertView;
    }

}
