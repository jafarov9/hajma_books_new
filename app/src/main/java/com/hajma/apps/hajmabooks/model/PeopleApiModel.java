package com.hajma.apps.hajmabooks.model;

public class PeopleApiModel {

    private int id;
    private String profile;
    private String name;

    public PeopleApiModel() {
    }

    public PeopleApiModel(int id, String profile, String name) {
        this.id = id;
        this.profile = profile;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
