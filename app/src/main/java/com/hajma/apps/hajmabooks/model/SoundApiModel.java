package com.hajma.apps.hajmabooks.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SoundApiModel implements Parcelable {

    private int id;
    private String title;
    private String sound;

    public SoundApiModel(int id, String title, String sound) {
        this.id = id;
        this.title = title;
        this.sound = sound;
    }

    public SoundApiModel() {
    }

    protected SoundApiModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        sound = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(sound);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SoundApiModel> CREATOR = new Creator<SoundApiModel>() {
        @Override
        public SoundApiModel createFromParcel(Parcel in) {
            return new SoundApiModel(in);
        }

        @Override
        public SoundApiModel[] newArray(int size) {
            return new SoundApiModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
