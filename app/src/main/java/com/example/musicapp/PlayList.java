package com.example.musicapp;

import android.content.Context;
import android.widget.ArrayAdapter;


public class PlayList extends ArrayAdapter<String> {
    public PlayList(Context context, int resource) {
        super(context, resource);
    }
    public PlayList(Context context, int resource, String[] array) {
        super(context, resource, array);
    }
}
