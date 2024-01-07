package com.example.dogfinder.Model;

public class HistoryModel {
    String heartRate;
    String address;
    String date;
    double latitude;
    double longitude;
    String dateId;

    String highestHeartRate;
    String lowestHeartRate;
    String averageHeartRate;

    public HistoryModel(String heartRate, String address, String date,String dateId, double latitude, double longitude, String highestHeartRate, String lowestHeartRate,String averageHeartRate) {
        this.heartRate = heartRate;
        this.address = address;
        this.date = date;
        this.dateId = dateId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.highestHeartRate = highestHeartRate;
        this.lowestHeartRate = lowestHeartRate;
        this.averageHeartRate = averageHeartRate;
    }

    public String getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(String averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
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

    public String getHighestHeartRate() {
        return highestHeartRate;
    }

    public void setHighestHeartRate(String highestHeartRate) {
        this.highestHeartRate = highestHeartRate;
    }

    public String getLowestHeartRate() {
        return lowestHeartRate;
    }

    public void setLowestHeartRate(String lowestHeartRate) {
        this.lowestHeartRate = lowestHeartRate;
    }
}
