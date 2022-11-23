package com.capstone.crypto.view.model;

public class Chat {

    private int imageId;
    private String id;
    private String content;
    private String time;


    public Chat(){

    }

    public Chat(int imageId, String id, String content, String time) {
        this.imageId = imageId;
        this.id = id;
        this.content = content;
        this.time = time;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
