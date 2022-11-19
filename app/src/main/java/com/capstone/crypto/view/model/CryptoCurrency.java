package com.capstone.crypto.view.model;

public class CryptoCurrency {

    private Long cryptoId;
    private Long priceId;
    private String name;
    private String name_kor;
    private Long price;
    private String dateTime;

    public Long getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(Long cryptoId) {
        this.cryptoId = cryptoId;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_kor() {
        return name_kor;
    }

    public void setName_kor(String name_kor) {
        this.name_kor = name_kor;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
