package com.example.olahbence.sporttracker.Friends.Activities;

public class FriendsActivitiesRow {
    public String email;
    public String name;
    public String mDate;
    public String mDistance;
    public String mTime;

    public FriendsActivitiesRow(String email, String name, String mDate, String mDistance, String mTime) {
        this.email = email;
        this.name = name;
        this.mDate = mDate;
        this.mDistance = mDistance;
        this.mTime = mTime;
    }

    public FriendsActivitiesRow() {

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

