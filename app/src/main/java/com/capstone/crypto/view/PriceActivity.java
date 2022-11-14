package com.capstone.crypto.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PriceActivity  extends AppCompatActivity {

    private EditText cryptoTxt;
    private Button searchBtn;
    private String status;
    private ListView listView;
    private LineChart chart;
    private Thread thread;
    private Button articleBtn;
    private ImageView imageView;
    private ProgressDialog dialog;
    private Integer choosed = 2;
    private String name;
    private Button readFullBtn;

    int i = 0;
    int j = 0;
    public static List<CryptoPrice> cryptoCurrencies;
    public static List<ExpectedPrice> expectedPrices;
    private ArrayList<News> newsList;
    private ResponseModel responseModel;

    public List<CryptoPrice> getCryptoCurrencies() {
        return cryptoCurrencies;
    }

    public List<ExpectedPrice> getExpectedPrices() {
        return expectedPrices;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        articleBtn = findViewById(R.id.articleBtn);
        searchBtn = findViewById(R.id.searchBtn2);
        cryptoTxt = findViewById(R.id.searchBox);
        imageView = findViewById(R.id.imageView2);
        listView = findViewById(R.id.listview);
        chart = findViewById(R.id.bar);

        dialog = new ProgressDialog(PriceActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Data being processed...");

        initView();
        searchBtn.setOnClickListener(view -> {
            articleBtn.setVisibility(View.VISIBLE);
            newsList = new ArrayList<>();
            listView.setVisibility(View.INVISIBLE);
            String crypto = cryptoTxt.getText().toString();
            name = crypto;
            searchRealPrice(crypto, choosed);
        });
        articleBtn.setOnClickListener(view -> {
            searchNews();
        });
    }

    void searchNews()
    {
        System.out.println("ㅎㅎㅎ");
        PriceActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ㅋㅋㅋ");
                dialog.show();
            }
        });
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse("https://api.currentsapi.services/v1/search?keywords=" + name + "&language=en&apiKey=bUOAN1mHVyUahBl1LBy0uTDfcCtiYStsong5IkUzfUFErv5R").newBuilder();
        String url = urlBuilder.build().toString();
        System.out.println(url);
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                newsList = gson.fromJson(myResponse, ResponseModel.class).getNews();
                for(News n : newsList)
                    System.out.println(n.getTitle());
                NewsListViewAdapter newsListViewAdapter = new NewsListViewAdapter(getApplicationContext(), newsList);
                PriceActivity.this.runOnUiThread(new Runnable() {
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
        ChartMaker marker = new ChartMaker(this,R.layout.chart_maker);
        marker.setChartView(chart);
        chart.setMarker(marker);
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
        searchRealPrice(name, choosed);
    }



/*    void addEntry() {
        int size = cryptoCurrencies.size();
//        int size2 = expectedPrices.size();
        LineData data = chart.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            set = createBlueSet();
            data.addDataSet(set);
        }
        i = 0;
        while (true) {
            if (i == size) {
                i = 0;
                break;
            }
            data.addEntry(new Entry(i, (float) cryptoCurrencies.get(i).getHigh()), 0);
            System.out.println(cryptoCurrencies.get(i).getHigh()+cryptoCurrencies.get(i).getTime());
            i++;
        }
        System.out.println("------");
//        ArrayList<Entry> entry = new ArrayList<>();
//        for(int j = 0 ; j < expectedPrices.size(); j++)
//            entry.add(new Entry(Float.parseFloat(expectedPrices.get(j).getTime().substring(2)), (float)expectedPrices.get(j).getHigh()));
//        LineDataSet Rset = createRedSet(new LineDataSet(entry, "Expected Price"));
//        data.addDataSet(Rset);

        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(size / 2);
        chart.moveViewToX(data.getEntryCount());
    }*/
    void addEntry(int numberOfChart) {
        LineData lineData = chart.getData();
        int size1 = cryptoCurrencies.size();
        int size2 = expectedPrices.size();
        ArrayList<Entry> data1 = new ArrayList<Entry>();
        i = 0;
        while (true) {
            if (i == size1) {
                i = 0;
                break;
            }
            data1.add(new Entry(i, (float) cryptoCurrencies.get(i).getHigh()));
            i++;
        }
        LineDataSet dataset1 = new LineDataSet(data1, "actual");
        dataset1 = createBlueSet(dataset1);
        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        lines.add(dataset1);

        System.out.println("------");
        if(numberOfChart == 2) {
            ArrayList<Entry> data2 = new ArrayList<Entry>();
            i = size2 - 300;
            for( ;i < size2; i++){
                data2.add(new Entry(i, (float) expectedPrices.get(i).getPrice()));
                i++;
            }
//            while (true) {
//                if (i == size2) {
//                    i = 0;
//                    break;
//                }
//                data2.add(new Entry(i, (float) expectedPrices.get(i).getPrice()));
//                i++;
//            }
            LineDataSet dataset2 = new LineDataSet(data2, "expected");
            dataset2 = createRedSet(dataset2);
            lines.add(dataset2);
        }
        chart.setData(new LineData(lines));
        chart.setVisibleXRangeMaximum(size1 / 2);
        chart.moveViewToX(data1.size());
    }

    void feedMultiple(int numberOfChart)
    {
        if(thread != null)
            thread.interrupt();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry(numberOfChart);
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

    public ArrayList<String> getAreaCount() {
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < cryptoCurrencies.size(); i++)
            label.add(cryptoCurrencies.get(i).getTime().substring(2,10));
//        for (int i = 0; i < expectedPrices.size(); i++)
//            label.add(expectedPrices.get(i).getTime().substring(2));
        return label;
    }

    void drawLineChart(int numberOfChart)
    {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
        YAxis leftAxis = chart.getAxisRight();
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        LineData data = new LineData();
        chart.setData(data);
        feedMultiple(numberOfChart);
    }

    private LineDataSet createBlueSet(LineDataSet set)
    {
//        LineDataSet set = new LineDataSet(null, "Real Price");
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

    private LineDataSet createRedSet(LineDataSet set)
    {
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setLineWidth(4f);
        set.setDrawCircles(false);
        set.setFillAlpha(95);
        set.setFillColor(Color.RED);
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
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
        HttpUrl.Builder urlBuilder;
//        urlBuilder = HttpUrl.parse("https://jongseol-crypto.herokuapp.com/real/"+ num + "/"+crypto).newBuilder();
        urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/real/"+ num + "/"+crypto).newBuilder();
        String url = urlBuilder.build().toString();
        System.out.println(url);
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
                System.out.println(myResponse);
                Gson gson = new GsonBuilder().create();
                Type collectionType = new TypeToken<List<CryptoPrice>>(){}.getType();
                cryptoCurrencies = (List<CryptoPrice>) new Gson()
                        .fromJson( myResponse , collectionType);
                if(num == 2)
                    searchPrice(name, num);
                else {
                    drawLineChart(1);
                    dialog.dismiss();
                }
            }
        });

    }
    void searchPrice(String name, int num)
    {
        String crypto = name.toLowerCase(Locale.ROOT);
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder;
//        urlBuilder = HttpUrl.parse("https://jongseol-crypto.herokuapp.com/"+crypto).newBuilder();
        urlBuilder = HttpUrl.parse("http://10.0.2.2:8080/"+crypto).newBuilder();
        String url = urlBuilder.build().toString();
        System.out.println(url);

        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                System.out.println(myResponse);
                Type collectionType = new TypeToken<List<ExpectedPrice>>(){}.getType();
                try{
                    expectedPrices = (List<ExpectedPrice>) new Gson()
                            .fromJson( myResponse , collectionType);
                    if(expectedPrices.size() == 0)
                    {
                        PriceActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PriceActivity.this, "해당하는 가상화폐가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    System.out.println("zz" + expectedPrices.size());
                    drawLineChart(2);
                    dialog.dismiss();
//                    searchRealPrice(name, num);
                }catch (JsonSyntaxException e)
                {
                    PriceActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PriceActivity.this, "Invalid Name of the Cryptocurrency", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.toString());
                PriceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PriceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });                }

        });
    }
}
