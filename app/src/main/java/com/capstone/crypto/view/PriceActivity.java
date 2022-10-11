package com.capstone.crypto.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.crypto.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    ImageView imageView;
    ProgressDialog dialog;
    Integer choosed = 1;
    String name;

    int i = 0;
    List<CryptoPrice> cryptoCurrencies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        searchBtn = findViewById(R.id.searchBtn2);
        cryptoTxt = findViewById(R.id.searchBox);
        imageView = findViewById(R.id.imageView2);
        chart = findViewById(R.id.bar);
        dialog = new ProgressDialog(PriceActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Chart processing...");
        initView();
        searchBtn.setOnClickListener(view -> {
            String crypto = cryptoTxt.getText().toString();
            name = crypto;
            searchPrice(crypto, choosed);
        });
    }

    void initView()
    {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        cryptoTxt.setText(name);
        imageView.setOnClickListener(new View.OnClickListener() {
            int tempChoosed = choosed;
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"HOUR", "DAY", "WEEK", "MONTH"};
                final Integer[] mappedItems = new Integer[]{1,2,3,4};
                AlertDialog.Builder dialog = new AlertDialog.Builder(PriceActivity.this);
                dialog.setTitle("Choose Time Period")
                        .setSingleChoiceItems(items
                                , -1
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        tempChoosed = mappedItems[i];
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(choosed != tempChoosed){
                                    choosed = tempChoosed;
                                    searchRealPrice(name, choosed);
                                    Toast.makeText(PriceActivity.this, "COMPLETE!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(PriceActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
        searchPrice(name, choosed);
    }

    public ArrayList<String> getAreaCount() {
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < cryptoCurrencies.size(); i++)
            label.add(cryptoCurrencies.get(i).getTime().substring(2,10));
        return label;
    }

    void drawLineChart()
    {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
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
                runOnUiThread(runnable);
            }
        });
        thread.start();
    }

    void addEntry() {
        int size = cryptoCurrencies.size();
        System.out.println(i);
        LineData data = chart.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }
        i = 0;
        while (true) {
            if (i == size) {
                i = 0;
                break;
            }
            data.addEntry(new Entry(i, (float) cryptoCurrencies.get(i).getHigh()), 0);
            i++;
        }
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(size / 2);
        chart.moveViewToX(data.getEntryCount());
    }

    private LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(4f);
        set.setDrawCircles(false);
        set.setFillAlpha(95);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,177,177));
        set.setDrawValues(false);
        return set;
    }
    void searchRealPrice(String name, int num)
    {
        PriceActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        String crypto = name.toLowerCase(Locale.ROOT);
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse("https://jongseol-crypto.herokuapp.com/real/"+ num + "/"+crypto).newBuilder();
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
                dialog.dismiss();
            }
        });

    }
    void searchPrice(String name, int num)
    {
        String crypto = name.toLowerCase(Locale.ROOT);
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse("https://jongseol-crypto.herokuapp.com/"+crypto).newBuilder();
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
                try{
                    List<CryptoCurrency> cryptoCurrencies = (List<CryptoCurrency>) new Gson()
                            .fromJson( myResponse , collectionType);
                    searchRealPrice(name, num);
                }catch (JsonSyntaxException e)
                {
                    System.out.println("asd");
                    PriceActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PriceActivity.this, "해당하는 가상화폐가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
