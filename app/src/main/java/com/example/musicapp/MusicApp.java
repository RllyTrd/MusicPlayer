package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicApp extends AppCompatActivity {
    public static final String EXTRA_DBASE = "dbase";
    public static final String EXTRA_LOOP = "loop";
    public static final String EXTRA_SHUFFLE = "shuffle";
    public final String TAG ="CPTR320";
    private MusicDatabase musicDatabase;
    private View currSelection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.playlist);
        String[] SongList = getResources().getStringArray(R.array.SongList);

        PlayList playList = new PlayList(this, android.R.layout.simple_list_item_1, SongList);
        listView.setAdapter(playList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        String selection = (String) parent.getItemAtPosition(position);
                        currSelection = view;
                        MusicDatabase database = new MusicDatabase(MusicApp.this);
                        database.setSong(selection);

                        Log.d(TAG, "Index clicked is " + position + " set to " + selection);
                        Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
                        intent.putExtra(EXTRA_DBASE, database);
                        intent.putExtra(EXTRA_LOOP, PrefsActivity.getLoop(MusicApp.this));
                        intent.putExtra(EXTRA_SHUFFLE, PrefsActivity.getShuffle(MusicApp.this));
                        startActivity(intent);
                    }
                });

            }
        });

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currSelection != null){
            currSelection.setAlpha(1.0f);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        getSupportActionBar().hide();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


}