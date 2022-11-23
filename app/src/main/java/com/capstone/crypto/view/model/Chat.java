package com.capstone.crypto.view.model;

public class Chat {

    private int imageId;
    private String crypto;
    private String id;
    private String content;
    private String time;
    private Integer image;

    public Chat(){

    }

    public Chat(int imageId, String id, String content, String time, String crypto, Integer img) {
        this.imageId = imageId;
        this.id = id;
        this.content = content;
        this.time = time;
        this.crypto = crypto;
        this.image = img;
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

    public String getCrypto() {
        return crypto;
    }

    public void setCrypto(String crypto) {
        this.crypto = crypto;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
