package com.example.olahbence.sporttracker.Friends.Activities;

public class FriendsActivitiesRow {
    public String email;
    public String name;
    private String date;
    private String distance;
    private String time;

    FriendsActivitiesRow(String email, String name, String date, String distance, String time) {
        this.email = email;
        this.name = name;
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

    String getTime() {
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

