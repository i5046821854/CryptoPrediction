package com.capstone.crypto.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PriceActivity  extends AppCompatActivity {

    EditText cryptoTxt;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        searchBtn = findViewById(R.id.searchBtn2);
        cryptoTxt = findViewById(R.id.searchBox);
        initView();
        searchBtn.setOnClickListener(view -> {
            String crypto = cryptoTxt.getText().toString();
            searchPrice(crypto);
        });
    }

    void initView()
    {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        cryptoTxt.setText(name);
        searchPrice(name);
        searchRealPrice(name);
    }

    void searchRealPrice(String name)
    {
        String crypto = name.toLowerCase(Locale.ROOT);
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/real/"+crypto).newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.toString());

                PriceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PriceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });                }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                System.out.println(myResponse);
                Type collectionType = new TypeToken<List<CryptoPrice>>(){}.getType();
                List<CryptoPrice> cryptoCurrencies = (List<CryptoPrice>) new Gson()
                        .fromJson( myResponse , collectionType);
                for(CryptoPrice crypto : cryptoCurrencies)
                {
                    System.out.println(crypto.getTime() + crypto.getClose());
                }
            }
        });

    }
    void searchPrice(String name)
    {
        String crypto = name.toLowerCase(Locale.ROOT);
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/"+crypto).newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.toString());

                PriceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PriceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });                }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                System.out.println(myResponse);
                Type collectionType = new TypeToken<List<CryptoCurrency>>(){}.getType();
                List<CryptoCurrency> cryptoCurrencies = (List<CryptoCurrency>) new Gson()
                        .fromJson( myResponse , collectionType);
                for(CryptoCurrency crypto : cryptoCurrencies)
                {
                    System.out.println(crypto.getName() + crypto.getPrice() + crypto.getPrice());
                }
            }
        });
    }
}
