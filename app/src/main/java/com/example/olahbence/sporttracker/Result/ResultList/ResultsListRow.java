package com.example.olahbence.sporttracker.Result.ResultList;

public class ResultsListRow {
    public String date;
    public String distance;
    public String time;

    public ResultsListRow(String date, String distance, String time) {
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
