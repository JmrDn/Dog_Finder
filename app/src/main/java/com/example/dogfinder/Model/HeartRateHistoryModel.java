package com.example.dogfinder.Model;

public class HeartRateHistoryModel {
    String heartRate;
    String time;

    public HeartRateHistoryModel(String heartRate, String time){
        this.heartRate = heartRate;
        this.time = time;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
