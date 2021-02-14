package com.hajma.apps.hajmabooks.model;

import java.util.ArrayList;

public class DetailedBookApiModel {

    private int id;
    private String name;
    private String image;
    private String content;
    private int year;
    private String price;
    private int page_count;
    private String epub;
    private String book_language;
    private String seller;
    private boolean has_sound;
    private ArrayList<CategoryApiModel> bookCategories;
    private ArrayList<AuthorApiModel> bookAuthors;

    public ArrayList<CategoryApiModel> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(ArrayList<CategoryApiModel> bookCategories) {
        this.bookCategories = bookCategories;
    }

    public ArrayList<AuthorApiModel> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(ArrayList<AuthorApiModel> bookAuthors) {
        this.bookAuthors = bookAuthors;
    }

    public DetailedBookApiModel() {

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public String getEpub() {
        return epub;
    }

    public void setEpub(String epub) {
        this.epub = epub;
    }

    public String getBook_language() {
        return book_language;
    }

    public void setBook_language(String book_language) {
        this.book_language = book_language;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public boolean isHas_sound() {
        return has_sound;
    }

    public void setHas_sound(boolean has_sound) {
        this.has_sound = has_sound;
    }
}
