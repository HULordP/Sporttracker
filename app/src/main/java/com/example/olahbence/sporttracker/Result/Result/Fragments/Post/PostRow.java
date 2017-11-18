package com.example.olahbence.sporttracker.Result.Result.Fragments.Post;

public class PostRow {
    private long date;
    private String username;
    private String message;
    private String email;

    public PostRow() {
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    String getUsername() {
        return username;
    }

    String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
