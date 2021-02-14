package com.hajma.apps.hajmabooks.model;

public class ChatsApiModel {

    private int user_id;
    private int last_message_id;
    private int message_count;
    private String name;
    private String email;
    private String username;
    private String profile;
    private String last_message_date;
    private String last_message;

    public ChatsApiModel() {



    }

    public ChatsApiModel(int user_id, int last_message_id, int message_count, String name, String email, String username, String profile, String last_message_date, String last_message) {
        this.user_id = user_id;
        this.last_message_id = last_message_id;
        this.message_count = message_count;
        this.name = name;
        this.email = email;
        this.username = username;
        this.profile = profile;
        this.last_message_date = last_message_date;
        this.last_message = last_message;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(int last_message_id) {
        this.last_message_id = last_message_id;
    }

    public int getMessage_count() {
        return message_count;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLast_message_date() {
        return last_message_date;
    }

    public void setLast_message_date(String last_message_date) {
        this.last_message_date = last_message_date;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
