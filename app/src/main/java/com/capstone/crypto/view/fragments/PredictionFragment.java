package com.capstone.crypto.view.fragments;

import static java.lang.Math.*;
import static java.lang.Math.abs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.crypto.R;
import com.capstone.crypto.view.ResponseModel;
import com.capstone.crypto.view.adapters.NewsListViewAdapter;
import com.capstone.crypto.view.model.Articles;
import com.capstone.crypto.view.model.CryptoPrice;
import com.capstone.crypto.view.model.ExpectedPrice;
import com.capstone.crypto.view.model.News;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

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

public class PredictionFragment extends Fragment {

    private EditText cryptoTxt;
    private Button searchBtn;
    private String status;
    private TextView resultTxt;
    private LineChart chart;
    private Thread thread;
    private ProgressDialog dialog;
    private Integer choosed = 2;
    private String name;
    private float curPrice;
    private TextView noticeTxt;
    private String date;
    private Button showArticleBtn;
    private TextView momentumPro;
    private TextView anormalyPro;
    private TextView textView8;
    private TextView sentimentalPro;
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private Integer crypto;
    private float tomorrowPrice;
    private String preference;

    int i = 0;
    int j = 0;
    public static List<CryptoPrice> cryptoCurrencies;
    public static List<ExpectedPrice> expectedPrices;
    private Integer diff = 1899 - 378;
    private List<Articles> newsList;
    private ResponseModel responseModel;
    Context context;
    public List<CryptoPrice> getCryptoCurrencies() {
        return cryptoCurrencies;
    }

    public List<ExpectedPrice> getExpectedPrices() {
        return expectedPrices;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prediction, container, false);
        context = container.getContext();
        searchBtn = view.findViewById(R.id.searchBtn2);
        cryptoTxt = view.findViewById(R.id.searchBox);
        preference =  getArguments().getString("preference");
        crypto = preference.equals("bitcoin") ? 2 : 1;
        chart = view.findViewById(R.id.bar);
        resultTxt = view.findViewById(R.id.resultTxt);
        noticeTxt = view.findViewById(R.id.textView9);
        showArticleBtn = view.findViewById(R.id.showArticleBtn);
        momentumPro = view.findViewById(R.id.momPro);
        anormalyPro = view.findViewById(R.id.anoPro);
        textView8 = view.findViewById(R.id.textView8);
        sentimentalPro = view.findViewById(R.id.senPro);
        progressBar1 = view.findViewById(R.id.progressBar);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar3 = view.findViewById(R.id.progressBar3);
        sentimentalPro = view.findViewById(R.id.senPro);

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Data being processed...");

        initView();

        searchBtn.setOnClickListener(tempView -> {
            String cryptoStr = cryptoTxt.getText().toString();
            name = cryptoStr;
            preference = name;
            crypto = name.equals("bitcoin") ? 2 : 1;
            initView();
//            searchRealPrice(cryptoStr);
        });
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float x = e.getX();
                if((int)x - diff < 0)
                    return;
                ExpectedPrice expected = expectedPrices.get((int)x - diff);
                CryptoPrice prev = cryptoCurrencies.get((int)x - diff - 1);
                float expectedPrice = expected.getPrice();
                float prevPrice = prev.getClose();
                double rate;
                date = expected.getDateTime().substring(0,10);
                rate = abs(round((expectedPrice - curPrice) / curPrice * 100 * 100) / 100.0);
                if(curPrice < expectedPrice){
                    resultTxt.setText(date + "\nExpected Price : " + Utils.formatNumber(expectedPrice, 0, true) + " (" + rate + "% increase)");
                    resultTxt.setTextColor(Color.RED);
                }else if(curPrice > expectedPrice){
                    resultTxt.setText(date + "\nExpected Price : " + Utils.formatNumber(expectedPrice, 0, true) +" (" + rate + "% decrease)");
                    resultTxt.setTextColor(Color.BLUE);
                }else{
                    resultTxt.setText(date + "\nExpected Price : " + Utils.formatNumber(expectedPrice, 0, true) + " (0% increase)" );
                    resultTxt.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        showArticleBtn.setOnClickListener(thisView-> {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS).writeTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS).build();
            HttpUrl.Builder urlBuilder;
//        urlBuilder = HttpUrl.parse("https://api.currentsapi.services/v1/search?keywords=" + name + "&language=en&apiKey=bUOAN1mHVyUahBl1LBy0uTDfcCtiYStsong5IkUzfUFErv5R").newBuilder();
            urlBuilder = HttpUrl.parse("http://3.39.61.211:8080/news/" + date + "/" + crypto).newBuilder();
            String url = urlBuilder.build().toString();
            Request req = new Request.Builder().url(url).build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String myResponse = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    Type collectionType = new TypeToken<List<Articles>>() {
                    }.getType();
                    newsList = (List<Articles>) new Gson()
                            .fromJson(myResponse, collectionType);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog();
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.toString());
                    dialog.dismiss();
                }
            });
        });
        return view;
    }

    private void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.new_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        ListView listView = (ListView) dialog.findViewById(R.id.listview_alterdialog_list);
        NewsListViewAdapter adapter = new NewsListViewAdapter(context, newsList, 1);
        listView.setAdapter(adapter);
        TextView title = dialog.findViewById(R.id.textview_alterdialog_title);
        TextView noArticle = dialog.findViewById(R.id.noArticleTxt);
        if(newsList.size() == 0)
            noArticle.setVisibility(View.VISIBLE);
        title.setText("Articles about " + preference.toUpperCase() + "\n on " + date);
        dialog.setTitle("custom dialog !!");
        dialog.show();
    }

    void initView()
    {
        if(crypto == 1){
            sentimentalPro.setTextColor(Color.RED);
            anormalyPro.setTextColor(Color.BLUE);
            sentimentalPro.setText("Positive");
            anormalyPro.setText("Low Possibility");
            progressBar1.setProgressTintList(ColorStateList.valueOf(Color.RED));
            progressBar2.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        }else{
            sentimentalPro.setTextColor(Color.BLUE);
            anormalyPro.setTextColor(Color.BLUE);
            sentimentalPro.setText("Negative");
            anormalyPro.setText("Low Possibility");
            progressBar1.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
            progressBar2.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        }
        name = preference;
        cryptoTxt.setText(name);
        searchRealPrice(name);
    }


    void addEntry() {
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
            data1.add(new Entry(i, (float) cryptoCurrencies.get(i).getClose()));
            i++;
        }
        LineDataSet dataset1 = new LineDataSet(data1, "actual");
        dataset1 = createBlueSet(dataset1);
        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        lines.add(dataset1);
        ArrayList<Entry> data2 = new ArrayList<Entry>();
        i = 0;
        for( ;i < size2; i++){
            data2.add(new Entry(i + diff, (float) expectedPrices.get(i).getPrice() - 300));
            i++;
        }
        LineDataSet dataset2 = new LineDataSet(data2, "expected");
        dataset2 = createRedSet(dataset2);
        lines.add(dataset2);

        ArrayList<Entry> data3 = new ArrayList<Entry>();
        data3.add(new Entry(i + diff, (float) expectedPrices.get(size2 -1).getPrice() - 300));
        LineDataSet dataset3 = new LineDataSet(data3, "Tomorrow");
        dataset3 = createGreenSet(dataset3);
        lines.add(dataset3);
        Highlight h = new Highlight(size1 - 1, 0,0);
        chart.setData(new LineData(lines));
        chart.highlightValue(h);

        chart.setVisibleXRangeMaximum(size1 / 2);
        chart.moveViewToX(data1.size());
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
                getActivity().runOnUiThread(runnable);
            }
        });
        thread.start();
    }

    public ArrayList<String> getAreaCount() {
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < cryptoCurrencies.size(); i++)
            label.add(cryptoCurrencies.get(i).getTime().substring(0,10));
//        for (int i = 0; i < expectedPrices.size(); i++)
//            label.add(expectedPrices.get(i).getTime().substring(2));
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
        set.setHighlightLineWidth(1.5f);
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
        set.setHighlightLineWidth(1.5f);
        set.setDrawValues(false);
        return set;
    }

    private LineDataSet createGreenSet(LineDataSet set)
    {
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.parseColor("#00804b"));
        set.setDrawCircles(true);
        set.setCircleRadius(5);
        set.setCircleHoleColor(Color.parseColor("#00804b"));
        set.setCircleColor(Color.parseColor("#00804b"));
//        set.setFillAlpha(95);
        set.setColor(Color.parseColor("#00804b"));
//        set.setHighLightColor(Color.rgb(244,177,177));
        set.setHighlightLineWidth(1.5f);
        set.setDrawValues(true);
        return set;
    }

    void searchRealPrice(String name)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        String crypto = name.toLowerCase(Locale.ROOT);
        if(crypto.equals("ethereum"))
            crypto = "etherium";
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
        HttpUrl.Builder urlBuilder;
//        urlBuilder = HttpUrl.parse("https://jongseol-crypto.herokuapp.com/real/"+ num + "/"+crypto).newBuilder();
        urlBuilder = HttpUrl.parse("http://3.39.61.211:8080/real/"+ 2 + "/"+crypto).newBuilder();
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.toString());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                Type collectionType = new TypeToken<List<CryptoPrice>>(){}.getType();
                cryptoCurrencies = (List<CryptoPrice>) new Gson()
                        .fromJson( myResponse , collectionType);
                int size = cryptoCurrencies.size();
                diff = size - 378;
                curPrice = cryptoCurrencies.get(size-1).getClose();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTxt.setText("Current Price : " + Utils.formatNumber(curPrice, 0, true, ','));
                        cryptoTxt.setText(name);
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(curPrice < cryptoCurrencies.get(size - 2).getHigh()){
                            progressBar3.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            momentumPro.setTextColor(Color.BLUE);
                            momentumPro.setText(String.valueOf(curPrice - cryptoCurrencies.get(size - 2).getHigh()));
                        }else{
                            progressBar3.setProgressTintList(ColorStateList.valueOf(Color.RED));
                            momentumPro.setTextColor(Color.RED);
                            momentumPro.setText("+" + String.valueOf(curPrice - cryptoCurrencies.get(size - 2).getHigh()));
                        }
                    }
                });
                searchPrice(name);
            }
        });

    }
    void searchPrice(String name)
    {
        String crypto = name.toLowerCase(Locale.ROOT);
        if(crypto.equals("ethereum"))
            crypto = "etherium";
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder;
//        urlBuilder = HttpUrl.parse("https://jongseol-crypto.herokuapp.com/"+crypto).newBuilder();
        urlBuilder = HttpUrl.parse("http://3.39.61.211:8080/"+crypto).newBuilder();
        String url = urlBuilder.build().toString();

        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                Type collectionType = new TypeToken<List<ExpectedPrice>>(){}.getType();
                try{
                    expectedPrices = (List<ExpectedPrice>) new Gson()
                            .fromJson( myResponse , collectionType);
                    if(expectedPrices.size() == 0)
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "해당하는 가상화폐가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    drawLineChart();
                    tomorrowPrice = expectedPrices.get(expectedPrices.size()-1).getPrice();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView8.setText("Expected Crypto Price for Tomorrow : " + Utils.formatNumber(tomorrowPrice, 0, true, ','));
                            textView8.setTextColor(Color.BLACK);
                        }
                    });

                    dialog.dismiss();
                }catch (JsonSyntaxException e)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Invalid Name of the Cryptocurrency", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });                }

        });
    }

}