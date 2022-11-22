package com.capstone.crypto.view;

import com.capstone.crypto.view.model.News;

import java.util.ArrayList;

public class ResponseModel {

    private String status;
    private ArrayList<News> news;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }


}

