package com.example.olahbence.sporttracker.Result.ResultList;

public class ResultsListRow {
    private String date;
    private String distance;
    private String time;

    ResultsListRow(String date, String distance, String time) {
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
