package com.example.pettrackerfersoncapstone.Model;

public class HistoryModel {
    String heartRate;
    String location;
    String heartRateHighest;
    String heartRateAverage;
    String heartRateLowest;
    String date;

    public HistoryModel(String location, String heartRateHighest, String heartRateAverage, String heartRateLowest, String date) {
        this.location = location;
        this.heartRateHighest = heartRateHighest;
        this.heartRateAverage = heartRateAverage;
        this.heartRateLowest = heartRateLowest;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getHeartRateHighest() {
        return heartRateHighest;
    }

    public String getHeartRateAverage() {
        return heartRateAverage;
    }

    public String getHeartRateLowest() {
        return heartRateLowest;
    }
}
