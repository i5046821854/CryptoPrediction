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


public class NewsListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Articles> items;
    private Button readFullBtn;
    private int type;

    public NewsListViewAdapter(Context mContext, List<Articles> items, int type) {
        this.mContext = mContext;
        this.items = items;
        this.type = type;
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

    public final static String EXT_TITLE = "TITLE";
    public final static String EXT_DESC = "DESC";
    public final static String EXT_URL = "URL";
    public final static String EXT_AUTHOR = "AUTHOR";
    public final static String EXT_IMAGE = "IMAGE";
    public final static String EXT_LANG = "LANG";
    public final static String EXT_CATEGORY = "CATEGORY";
    public final static String EXT_PUBLISHED = "PUBLISHED";




    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.news_listview, viewGroup, false);
        }
        System.out.println("tqweqwe");
        TextView titleTxt  = (TextView)view.findViewById(R.id.titleTxt2);
        readFullBtn = (Button)view.findViewById(R.id.detailBtn);
        TextView dateTxt = view.findViewById(R.id.dateTxt);
        Integer id = items.get(i).getArticleId();
//        String author = (items.get(i).getAuthor() == null)? " " : items.get(i).getAuthor();
        String title = (items.get(i).getTitle() == null)? " ": items.get(i).getTitle();
        if(this.type == 2) {
            dateTxt.setText(items.get(i).getDate());
            if(title.length() > 30){
                title = title.substring(0, 30);
            }
        }
        else
        {
            if(title.length() > 25){
                title = title.substring(0, 25);
            }
            dateTxt.setVisibility(View.GONE);
        }
        titleTxt.setText(title);
        //authorTxt.setText(author);
        readFullBtn.setOnClickListener(tempView ->{
            Intent redirect = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(i).getSrc()));
            mContext.startActivity(redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        return view;

    }
}
