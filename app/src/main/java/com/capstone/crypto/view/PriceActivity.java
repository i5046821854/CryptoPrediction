package com.capstone.crypto.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    LineChart chart;
    Thread thread;
    int i = 0;
    List<CryptoPrice> cryptoCurrencies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        searchBtn = findViewById(R.id.searchBtn2);
        cryptoTxt = findViewById(R.id.searchBox);
        chart = findViewById(R.id.bar);
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

    void drawLineChart()
    {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisRight();
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        LineData data = new LineData();
        chart.setData(data);
        feedMultiple();
    }
    void feedMultiple()
    {
        if(thread != null)
            thread.interrupt();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                while(true)
//                {
//                    if(i == 2400)
//                    {
//                        i = 0;
//                        break;
//                    }
//                    runOnUiThread(runnable);
//                    i++;
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                runOnUiThread(runnable);
            }
        });
        thread.start();
    }

    void addEntry() {
        System.out.println(i);
        LineData data = chart.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }
        while (true) {
            if (i == 2400) {
                i = 0;
                break;
            }
            data.addEntry(new Entry(i, (float) cryptoCurrencies.get(i).getHigh()), 0);
            i++;
        }
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(1000);
        chart.moveViewToX(data.getEntryCount());

    }

    private LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(4f);
        set.setCircleRadius(0.1f);
        set.setFillAlpha(95);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,177,177));
        set.setDrawValues(false);
        return set;
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
                cryptoCurrencies = (List<CryptoPrice>) new Gson()
                        .fromJson( myResponse , collectionType);
                drawLineChart();
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
