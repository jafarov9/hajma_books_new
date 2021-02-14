package com.hajma.apps.hajmabooks.model;

public class MessageApiModel {

    private int from_user;
    private int to_user;
    private String message;
    private String date;

    public MessageApiModel() { }

    public MessageApiModel(int from_user, int to_user, String message, String date) {
        this.from_user = from_user;
        this.to_user = to_user;
        this.message = message;
        this.date = date;
    }

    public int getFrom_user() {
        return from_user;
    }

    public void setFrom_user(int from_user) {
        this.from_user = from_user;
    }

    public int getTo_user() {
        return to_user;
    }

    public void setTo_user(int to_user) {
        this.to_user = to_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
