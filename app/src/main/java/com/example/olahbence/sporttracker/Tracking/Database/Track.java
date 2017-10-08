package com.example.olahbence.sporttracker.Tracking.Database;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Track {
    public String key;
    public long date;
    public String distance;
    public String filename;
    public String time;

    public Track() {
    }

    public Track(String filename) {
        this.filename = filename;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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
