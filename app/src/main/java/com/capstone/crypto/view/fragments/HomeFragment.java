package com.capstone.crypto.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.crypto.R;
import com.capstone.crypto.view.adapters.NewsListViewAdapter;
import com.capstone.crypto.view.ResponseModel;
import com.capstone.crypto.view.model.CryptoPrice;
import com.capstone.crypto.view.model.ExpectedPrice;
import com.capstone.crypto.view.model.News;
import com.capstone.crypto.view.utils.ChartMaker;
import com.capstone.crypto.view.views.MenuActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
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


public class HomeFragment extends Fragment {

    private EditText cryptoTxt;
    private Button searchBtn;
    private String status;
    private LineChart chart;
    private Thread thread;
    private TextView openTxt;
    private TextView closeTxt;
    private TextView lowTxt;
    private TextView highTxt;
    private TextView volumeTxt;
    private CryptoPrice curPrice;
    private ProgressDialog dialog;
    private Integer choosed = 2;
    private String name;
    private RadioGroup radioGroup;

    int i = 0;
    int j = 0;
    public static List<CryptoPrice> cryptoCurrencies;
    public static List<ExpectedPrice> expectedPrices;
    private ArrayList<News> newsList;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        searchBtn = view.findViewById(R.id.searchBtn2);
        cryptoTxt = view.findViewById(R.id.searchBox);
        radioGroup = view.findViewById(R.id.radioGroup);
        openTxt = view.findViewById(R.id.openTxt);
        closeTxt= view.findViewById(R.id.closeTxt);
        lowTxt = view.findViewById(R.id.lowTxt);
        highTxt = view.findViewById(R.id.highTxt);
        volumeTxt = view.findViewById(R.id.volTxt);
        chart = view.findViewById(R.id.bar);

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Data being processed...");

        initView();
        searchBtn.setOnClickListener(tempView -> {
            newsList = new ArrayList<>();
            String crypto = cryptoTxt.getText().toString();
            name = crypto;
            searchRealPrice(crypto, choosed);
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkNum = 0;
                switch(i){
                    case R.id.radioButton:
                        checkNum = 4;
                        break;
                    case R.id.radioButton2:
                        checkNum = 3;
                        break;
                    case R.id.radioButton3:
                        checkNum = 2;
                        break;
                    case R.id.radioButton4:
                        checkNum = 1;
                        break;
                }
                searchRealPrice(name, checkNum);
            }
        });
        return view;
    }

    void initView()
    {
        ChartMaker marker = new ChartMaker(context,R.layout.chart_maker);
        marker.setChartView(chart);
        chart.setMarker(marker);
//        name = this.getArguments().getString("preference");
        name = "bitcoin";
        System.out.println(name);
        cryptoTxt.setText(name);
        searchRealPrice(name, choosed);
    }


    void addEntry(int numberOfChart) {
        LineData lineData = chart.getData();
        int size1 = cryptoCurrencies.size();
        System.out.println(size1);
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
                getActivity().runOnUiThread(runnable);
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
        getActivity().runOnUiThread(new Runnable() {
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

//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                System.out.println(myResponse);
                Gson gson = new GsonBuilder().create();
                Type collectionType = new TypeToken<List<CryptoPrice>>(){}.getType();
                cryptoCurrencies = (List<CryptoPrice>) new Gson()
                        .fromJson( myResponse , collectionType);
                curPrice = cryptoCurrencies.get(cryptoCurrencies.size()-1);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BigDecimal open = new BigDecimal(curPrice.getOpen());
                        BigDecimal close = new BigDecimal(curPrice.getClose());
                        BigDecimal high = new BigDecimal(curPrice.getHigh());
                        BigDecimal low = new BigDecimal(curPrice.getLow());

                        openTxt.setText(open.toString());
                        closeTxt.setText(close.toString());
                        lowTxt.setText(low.toString());
                        highTxt.setText(high.toString());
                        volumeTxt.setText(Float.toString(curPrice.getVolume()));

                    }
                });
                drawLineChart(1);
                dialog.dismiss();
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "해당하는 가상화폐가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    System.out.println("zz" + expectedPrices.size());
                    drawLineChart(2);
                    dialog.dismiss();
//                    searchRealPrice(name, num);
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