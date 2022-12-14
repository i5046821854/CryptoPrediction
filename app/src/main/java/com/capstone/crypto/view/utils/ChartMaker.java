package com.capstone.crypto.view.utils;

import android.content.Context;
import android.widget.TextView;

import com.capstone.crypto.R;
import com.capstone.crypto.view.fragments.HomeFragment;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

public class ChartMaker extends MarkerView {

    private TextView tvContent;

    public ChartMaker(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView)findViewById(R.id.tvContent2);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float x = e.getX();
        float realPrice = HomeFragment.cryptoCurrencies.get((int)x).getClose();
        String date = HomeFragment.cryptoCurrencies.get((int)x).getTime();
        int len = date.length();
        tvContent.setText("Date : " + date.substring(2,10)+ "\nPrice : " + Utils.formatNumber( realPrice, 0, true));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() + 120);
    }

}


