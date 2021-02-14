package com.hajma.apps.hajmabooks.model;

public class CategoryApiModel {

    private int id;
    private String vertical;
    private String horizontal_large;
    private String horizontal_small;
    private String name;

    public CategoryApiModel(int id, String vertical, String horizontal_large, String horizontal_small, String name) {
        this.id = id;
        this.vertical = vertical;
        this.horizontal_large = horizontal_large;
        this.horizontal_small = horizontal_small;
        this.name = name;
    }

    public CategoryApiModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public String getHorizontal_large() {
        return horizontal_large;
    }

    public void setHorizontal_large(String horizontal_large) {
        this.horizontal_large = horizontal_large;
    }

    public String getHorizontal_small() {
        return horizontal_small;
    }

    public void setHorizontal_small(String horizontal_small) {
        this.horizontal_small = horizontal_small;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
