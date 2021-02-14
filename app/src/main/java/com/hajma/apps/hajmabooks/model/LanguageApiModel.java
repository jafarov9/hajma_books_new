package com.hajma.apps.hajmabooks.model;

public class LanguageApiModel {

    private int id;
    private String name;

    public LanguageApiModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LanguageApiModel(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
