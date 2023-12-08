package com.example.musicapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Song implements Parcelable {
    private int songId;
    private int imageId;
    private String title;
    private String artist;
    private double duration;
    private int picture;
    protected Song(Parcel in) {
        title = in.readString();
        imageId = in.readInt();
        songId = in.readInt();
    }
    protected Song(String title, int imageId, int songId){
        this.title = title;
        this.imageId = imageId;
        this.songId = songId;
    }
    public String getTitle(){
        return title;
    }
    public int getImageId(){
        return imageId;
    }
    public int getSongId(){
        return songId;
    }
    public void setTitle(){
        this.title = title;
    }
    public void setImageId(){
        this.imageId = imageId;
    }
    public void setSongId(){
        this.songId = songId;
    }


    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(imageId);
        dest.writeInt(songId);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
