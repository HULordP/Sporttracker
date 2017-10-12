package com.example.olahbence.sporttracker.Result.Result;

public class ResultRow {
    public String averagePace;
    public String number;

    public ResultRow(String averagePace) {
        this.averagePace = averagePace;
    }

    public ResultRow(String averagePace, String number) {
        this.averagePace = averagePace;
        this.number = number;
    }

    public ResultRow() {

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(String averagePace) {
        this.averagePace = averagePace;
    }
}

