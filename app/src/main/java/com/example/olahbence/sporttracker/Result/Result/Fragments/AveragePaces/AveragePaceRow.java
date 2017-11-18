package com.example.olahbence.sporttracker.Result.Result.Fragments.AveragePaces;

class AveragePaceRow {
    private String averagePace;
    private String number;

    AveragePaceRow(String averagePace) {
        this.averagePace = averagePace;
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
}

