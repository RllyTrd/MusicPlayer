package com.example.musicapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicDatabase implements Parcelable {
    private String selection;
    private String title;
    private ArrayList<String> playList = new ArrayList<>();
    private HashMap<String, Song> database = new HashMap<String, Song>();

    private Context context;


    protected MusicDatabase(Parcel in) {
        selection = in.readString();
        playList = in.readArrayList(getClass().getClassLoader());
        database = in.readHashMap(getClass().getClassLoader());
    }
    public MusicDatabase (Context context) {
         this.context = context;
        Song song = new Song("Ethernight Club", R.drawable.ethernightclub, R.raw.ethernightclub);
        database.put(song.getTitle(), song);
        playList.add(song.getTitle());

       song = new Song("Robobozo", R.drawable.robobozor, R.raw.robobozo);
        database.put(song.getTitle(), song);
        playList.add(song.getTitle());

        song = new Song("Go Cart - Drop Mix", R.drawable.gocartdropmix, R.raw.gocartdropmix);
        database.put(song.getTitle(), song);
        playList.add(song.getTitle());

        song = new Song("Level Up", R.drawable.levelup, R.raw.levelup);
        database.put(song.getTitle(), song);
        playList.add(song.getTitle());

        song = new Song("Monkeys Spinning Monkeys",R.drawable.monkeysspinningmonkeys, R.raw.monkeysspinningmonkeys);
        database.put(song.getTitle(), song);
        playList.add(song.getTitle());

    }
    public Song getSong(String title){
        return database.get(title);
    }


    public String getSelection(){
        return selection;
    }

    public static final Creator<MusicDatabase> CREATOR = new Creator<MusicDatabase>() {
        @Override
        public MusicDatabase createFromParcel(Parcel in) {
            return new MusicDatabase(in);
        }

        @Override
        public MusicDatabase[] newArray(int size) {
            return new MusicDatabase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(selection);
        dest.writeList(playList);
        dest.writeMap(database);
    }
    public void setSong(String title){
        selection = title;
    }

    public String[] getTitles() {

        return playList.toArray(new String[0]);
    }
    public int getCurrIndex(){
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).equals(selection))
                return i;
        }
        return -1;
    }

    public int size(){
       return database.size();
    }

    public String getSongAt(int index){
        return playList.get(index);
    }

    public void setSelection(String title) {
        this.selection = title;
    }
}
