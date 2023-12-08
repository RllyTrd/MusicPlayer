package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;

public class MusicPlayer extends AppCompatActivity {
    private MusicDatabase database;
    private SeekBar seekBar;
    private static String TAG = "COMP320";
    private final String PLAYER_POSITION_KEY = "CURR_POSITION";
    private final String PLAYER_STATE_KEY = "CURR_STATE";
    private Timer timer;
    private TimerTask task;
    private String[] titles;
    private boolean isLooping, isShuffle;

    private int currPosition = 0; //location of player on track
    private boolean currState = false; //whether player is musicking
    private boolean ready = false;
    private View songBanner;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String[] playList, shuffledList, regList;
    ImageButton playButton, pauseButton, forwardButton,
            rewindButton, nextButton, previousButton;
    String key;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);
        TextView textView = findViewById(R.id.textView);
        Intent intent = getIntent();
        isLooping = intent.getBooleanExtra(MusicApp.EXTRA_LOOP, false);
        isShuffle = intent.getBooleanExtra(MusicApp.EXTRA_SHUFFLE, false);
        database = intent.getParcelableExtra(MusicApp.EXTRA_DBASE);
        String title = database.getSelection();
        Song song = database.getSong(title);
        textView.setText(song.getTitle());
        String [] regList = database.getTitles();
        String [] playList = regList;
        String [] shuffledList = knuthShuffle(regList);

        findViews();
        setUpButtons();
        setupSeekBar();
        setUpMediaPlayer();
//        int currOrientation = getResources().getConfiguration().orientation;
//        if(currOrientation == Configuration.ORIENTATION_PORTRAIT){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }

    }

    private int indexOf(String[] array, String key) {
        for (int i = 0; i < array.length; i++) {
            if (key.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
    private void setUpButtons() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Media Player play requested...");
                if(ready)
                    mediaPlayer.start();
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please wait!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        pauseButton.setOnClickListener(v -> {
            Log.d(TAG, "Pause requested...");
            if(ready && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            currState = false;
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Previous Song requested...");
                String [] titles = database.getTitles();
                int index = database.getCurrIndex();
                index --;
                index %= titles.length;
                database.getSong(titles[index]);
                if (index < 0 || index > titles.length){
                    return;
                } else {
                    setUpMediaPlayer();
                }
                currState = true;

            }
        });
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Next Song requested...");
                String [] titles = database.getTitles();
                String title = database.getSelection();
                int index = 0;
                for (int i = 0; i < playList.length; i++) {
                    if(title.equals(playList[i])){
                        index = i;
                    }
                }
               index++;
                if (isLooping){
                    if (index == playList.length){
                        index = 0;
                    }
                } else if (isShuffle && index == 0) {
                    playList = knuthShuffle(playList);
                }
                if (currPosition> titles.length){
                    currPosition++;
                } else {
                    currPosition = 0;
                }

                currPosition++;
                index %= database.getTitles().length;
                database.setSong(database.getSongAt(index));
                setUpMediaPlayer();
            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        currPosition = getPreferences(MODE_PRIVATE).getInt(PLAYER_POSITION_KEY, 0);
        currState = getPreferences(MODE_PRIVATE).getBoolean(PLAYER_STATE_KEY, false);

        Log.d(TAG, "curr pos = " + currPosition + " ,and curr state is " + currState );
        setUpMediaPlayer();
        setUpTimer();

    }
    private void setUpMediaPlayer() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared called...");
                ready = true;
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setMin(0);
                mediaPlayer.seekTo(currPosition);
                seekBar.setProgress(currPosition);
                if (currState)
                    mediaPlayer.start();
            }
        });

        //TODO
        mediaPlayer.reset();
        Song song = database.getSong(database.getSelection());
        int songID =  song.getSongId();
        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(songID);
        TextView textView = findViewById(R.id.textView);
        textView.setText(song.getTitle());
        songBanner.setBackground(null);
        songBanner.setBackground(getDrawable(song.getImageId()));
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepareAsync();
            assetFileDescriptor.close();
        }catch (IOException e){
            Log.d(TAG, "Exception when setting data source!");
            Log.d(TAG, e.getMessage());
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int index = database.getCurrIndex();
                index++;
               String title = song.getTitle();

                for (int i = 0; i < playList.length; i++) {
                    if(playList[i].equals(title)){
                        index = i;
                    }
                    index++;
                    database.setSelection(playList[index]);
                    setUpMediaPlayer();
                }
                if(isLooping){
                    index %= playList.length;
                    database.getSong(playList[index]);
                    setUpMediaPlayer();
                } else{
                    if (index == playList.length){
                        mediaPlayer.stop();
                    }
                }
                if(isShuffle){
                    playList = shuffledList;
                } else {
                    playList = regList;
                }
                currState = true;
            }

        });
    }

    private void setUpTimer() {
        task = new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 50, 200);
    }


    private void setupSeekBar(){
         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int curr = seekBar.getProgress();
            mediaPlayer.seekTo(curr);
        }
    });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private static String[] knuthShuffle(String [] keys){
        String [] temp = Arrays.copyOf(keys, keys.length);
        Random random = new Random();
        for(int i = 1; i > keys.length; i++){
            swap(i, random.nextInt(i+1), keys);
        }
        return temp;
    }


    private static void swap(int i, int j, String[] keys){
        String tmp = keys[i];
        keys[i] = keys[j];
        keys[j] = tmp;
    }
    public void setLooping() {
        titles = database.getTitles();
        if (isLooping) {
            if (titles.length == indexOf(titles, key)) {
                key = titles[0];
            }
        }

    }
    public void setShuffle(){
        if (isShuffle){
            knuthShuffle(titles);
        } else if (isShuffle && isLooping){
            if (titles.length == indexOf(titles, key)){
                key = titles[0];
                knuthShuffle(titles);
            }

        }
    }





    private void findViews(){
        songBanner = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.button_play);
        pauseButton = findViewById((R.id.button_pause));
        forwardButton = findViewById(R.id.button_forward);
        rewindButton = findViewById(R.id.button_rewind);
        nextButton = findViewById(R.id.button_next);
        previousButton = findViewById(R.id.button_previous);
    }


}