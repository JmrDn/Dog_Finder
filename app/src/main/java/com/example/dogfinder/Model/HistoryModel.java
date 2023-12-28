package com.example.dogfinder.Model;

public class HistoryModel {
    String heartRate;
    String address;
    String date;
    double latitude;
    double longitude;
    String dateId;

    public HistoryModel(String heartRate, String address, String date,String dateId, double latitude, double longitude) {
        this.heartRate = heartRate;
        this.address = address;
        this.date = date;
        this.dateId = dateId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }
}
