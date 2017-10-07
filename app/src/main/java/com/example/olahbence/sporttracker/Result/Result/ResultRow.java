package com.example.olahbence.sporttracker.Result.Result;

public class ResultRow {
    public String averagePace;

    public ResultRow(String averagePace) {
        this.averagePace = averagePace;
    }

    public ResultRow()
    {

    }

    public String getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(String averagePace) {
        this.averagePace = averagePace;
    }
}

