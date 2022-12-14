package com.capstone.crypto.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.capstone.crypto.R;
import com.capstone.crypto.view.adapters.NewsListViewAdapter;
import com.capstone.crypto.view.ResponseModel;
import com.capstone.crypto.view.model.Articles;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NewsFragment extends Fragment {

    private EditText cryptoTxt;
    private Button searchBtn;
    private ListView listView;
    private Button articleBtn;
    private ProgressDialog dialog;
    private String name;
    private Button readFullBtn;
    private Integer crypto;
    private List<Articles> newsList;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        context = container.getContext();

        // Initialize variables
        articleBtn = view.findViewById(R.id.articleBtn);
        listView = view.findViewById(R.id.listview);
        searchBtn = view.findViewById(R.id.articleBtn);
        crypto = getArguments().getString("preference").equals("bitcoin") ? 2 : 1;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Data being processed...");
        initView();

        // Generate List View for articles
        searchBtn.setOnClickListener(tempView -> {
            articleBtn.setVisibility(View.VISIBLE);
            newsList = new ArrayList<Articles>();
            listView.setVisibility(View.INVISIBLE);
            String crypto = cryptoTxt.getText().toString();
            name = crypto;
        });

        articleBtn.setOnClickListener(tempView -> {
            searchNews();
        });
        return view;
    }


    // ask server to make a list of articles
    void searchNews()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        //send HTTP request to server to get articles
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS).writeTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS).build();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse("http://3.39.61.211:8080/news/" + crypto).newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                Type collectionType = new TypeToken<List<Articles>>(){}.getType();
                newsList = (List<Articles>) new Gson()
                        .fromJson( myResponse , collectionType);
                NewsListViewAdapter newsListViewAdapter = new NewsListViewAdapter(context, newsList, 2);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(newsListViewAdapter);
                        articleBtn.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e.toString());
                dialog.dismiss();
            }
        });
    }

    void initView()
    {
        name = this.getArguments().getString("preference");
    }

}