package com.hajma.apps.hajmabooks.model;

public class BookFileModel {

    private String path;
    private String location;

    public BookFileModel(String path, String location) {
        this.path = path;
        this.location = location;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
