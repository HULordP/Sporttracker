package com.example.olahbence.sporttracker.Friends.Activities;

public class FriendsActivitiesRow {
    public String email;
    public String name;
    public String date;
    public String distance;
    public String time;

    public FriendsActivitiesRow(String email, String name, String date, String distance, String time) {
        this.email = email;
        this.name = name;
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

