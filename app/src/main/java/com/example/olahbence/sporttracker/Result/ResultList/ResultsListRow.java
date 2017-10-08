package com.example.olahbence.sporttracker.Result.ResultList;

public class ResultsListRow {
    public String mDate;
    public String mDistance;
    public String mTime;

    public ResultsListRow(String mDate, String mDistance, String mTime) {
        this.mDate = mDate;
        this.mDistance = mDistance;
        this.mTime = mTime;
    }

    public ResultsListRow() {

    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
