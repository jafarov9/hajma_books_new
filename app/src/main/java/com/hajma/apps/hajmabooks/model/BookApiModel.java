package com.hajma.apps.hajmabooks.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BookApiModel implements Parcelable {

    private int id;
    private int pageCount;
    private int year;
    private int sound_count;
    private int page_count;

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    private String author_name;


    public int getSound_count() {
        return sound_count;
    }

    public void setSound_count(int sound_count) {
        this.sound_count = sound_count;
    }

    private String price;
    private String cover;
    private String name;

    public BookApiModel() {

    }

    public BookApiModel(int id, int pageCount, int year, String price, String epub, String cover, String name) {
        this.id = id;
        this.pageCount = pageCount;
        this.year = year;
        this.price = price;
        this.cover = cover;
        this.name = name;
    }


    protected BookApiModel(Parcel in) {
        id = in.readInt();
        pageCount = in.readInt();
        year = in.readInt();
        price = in.readString();
        cover = in.readString();
        name = in.readString();
    }

    public static final Creator<BookApiModel> CREATOR = new Creator<BookApiModel>() {
        @Override
        public BookApiModel createFromParcel(Parcel in) {
            return new BookApiModel(in);
        }

        @Override
        public BookApiModel[] newArray(int size) {
            return new BookApiModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(pageCount);
        dest.writeInt(year);
        dest.writeString(price);
        dest.writeString(cover);
        dest.writeString(name);
        dest.writeString(author_name);
        dest.writeInt(page_count);
    }
}
