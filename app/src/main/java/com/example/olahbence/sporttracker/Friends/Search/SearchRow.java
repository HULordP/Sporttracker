package com.example.olahbence.sporttracker.Friends.Search;

public class SearchRow {
    public String email;
    public String name;

    public SearchRow(String email) {
        this.email = email;
    }

    public SearchRow() {

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

