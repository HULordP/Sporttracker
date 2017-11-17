package com.example.olahbence.sporttracker.Result.Result.Fragments.AveragePaces;

public class AveragePaceRow {
    private String averagePace;
    private String number;

    AveragePaceRow(String averagePace) {
        this.averagePace = averagePace;
    }

    public AveragePaceRow(String averagePace, String number) {
        this.averagePace = averagePace;
        this.number = number;
    }

    public AveragePaceRow() {

    }

    String getNumber() {
        return number;
    }

    void setNumber(String number) {
        this.number = number;
    }

    String getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(String averagePace) {
        this.averagePace = averagePace;
    }
}

