package com.example.musicapp;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class PrefsActivity extends AppCompatActivity {
    public static final String OPT_LOOP = "loop";
    public static final boolean OPT_LOOP_DEF = true;
    public static final String OPT_SHUFFLE = "shuffle";
    public static final boolean OPT_SHUFFLE_DEF = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

//        getSupportActionBar().setTitle("Settings");

        if (findViewById(R.id.settings_container) != null){
            if (savedInstanceState != null) {
                return;
            }
            PrefsFragment frag = new PrefsFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.settings_container, frag).commit();

        }
    }
    public static boolean getLoop(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_LOOP, OPT_LOOP_DEF);
    }
    public static boolean getShuffle(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SHUFFLE, OPT_SHUFFLE_DEF);
    }
}
