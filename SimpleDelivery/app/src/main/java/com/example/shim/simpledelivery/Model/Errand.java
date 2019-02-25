package com.example.shim.simpledelivery.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Errand {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("buyer_id")
    @Expose
    private int buyer_id;
    @SerializedName("porter_id")
    @Expose
    private int porter_id;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("create_dateTime")
    @Expose
    private Date create_dateTime;
    @SerializedName("delete_dateTime")
    @Expose
    private Date delete_dateTime;
    @SerializedName("contents")
    @Expose
    private String contents;

    public Errand(int id, int buyer_id, int porter_id, String destination, double latitude, double longitude, int price, Date create_dateTime, Date delete_dateTime, String contents) {
        this.id = id;
        this.buyer_id = buyer_id;

        this.destination = destination;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.create_dateTime = create_dateTime;
        this.delete_dateTime = delete_dateTime;
        this.contents = contents;
    }

    public Errand(String destination, double latitude, double longitude, int price, String contents) {
        this.destination = destination;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.contents = contents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(int buyer_id) {
        this.buyer_id = buyer_id;
    }

    public int getPorter_id() {
        return porter_id;
    }

    public void setPorter_id(int porter_id) {
        this.porter_id = porter_id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getCreate_dateTime() {
        return create_dateTime;
    }

    public void setCreate_dateTime(Date create_dateTime) {
        this.create_dateTime = create_dateTime;
    }

    public Date getDelete_dateTime() {
        return delete_dateTime;
    }

    public void setDelete_dateTime(Date delete_dateTime) {
        this.delete_dateTime = delete_dateTime;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
