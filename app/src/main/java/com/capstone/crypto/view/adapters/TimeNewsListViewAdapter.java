package com.capstone.crypto.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.crypto.R;
import com.capstone.crypto.view.model.Articles;
import com.capstone.crypto.view.model.News;

import java.util.ArrayList;
import java.util.List;


public class TimeNewsListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Articles> items;
    private Button readFullBtn;

    public TimeNewsListViewAdapter(Context mContext, List<Articles> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.news_listview, viewGroup, false);
        }

        TextView titleTxt  = (TextView)view.findViewById(R.id.titleTxt2);
        readFullBtn = (Button)view.findViewById(R.id.detailBtn);
        Integer id = items.get(i).getArticleId();
        String title = (items.get(i).getTitle() == null)? " ": items.get(i).getTitle();
        titleTxt.setText(title);
        readFullBtn.setOnClickListener(tempView ->{
            Intent redirect = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(i).getSrc()));
            mContext.startActivity(redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        return view;

    }
}
