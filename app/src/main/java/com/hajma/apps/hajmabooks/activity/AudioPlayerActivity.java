package com.hajma.apps.hajmabooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.SoundAdapter;
import com.hajma.apps.hajmabooks.model.SoundApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AudioPlayerActivity extends AppCompatActivity {

    private ImageView imagePlayPause;
    private TextView textCurrentTime;
    private TextView textTotalTime;
    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private ArrayList<SoundApiModel> soundList;
    private RecyclerView rvSounds;
    private SoundAdapter soundAdapter;
    private ImageView imgPlayerCover;
    private TextView txtAudioPlayerBookName;
    private ImageButton imgBtnBackAudioPlayer;
    private String cover;
    private String title;
    private boolean isPaused;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        soundList = getIntent().getParcelableArrayListExtra("sounds");
        cover = getIntent().getStringExtra("cover");
        title = getIntent().getStringExtra("name");


        rvSounds = findViewById(R.id.rvSoundList);
        imgPlayerCover = findViewById(R.id.imgAudioPlayerImage);
        txtAudioPlayerBookName = findViewById(R.id.txtAudioPlayerBookName);
        imgBtnBackAudioPlayer = findViewById(R.id.imgBtnBackAudioPlayer);

        imgBtnBackAudioPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Picasso.get()
                .load(cover
                        .replace("http:", "https:"))
                .into(imgPlayerCover);

        txtAudioPlayerBookName.setText(title);

        imagePlayPause = findViewById(R.id.imagePlayPause);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);
        playerSeekBar = findViewById(R.id.playerSeekBar);

        mediaPlayer = new MediaPlayer();

        imagePlayPause.setEnabled(false);

        playerSeekBar.setMax(100);

        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()) {
                    isPaused = true;
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setBackgroundResource(R.drawable.ic_play);
                }else {
                    mediaPlayer.start();
                    imagePlayPause.setBackgroundResource(R.drawable.ic_pause);
                    updateSeekBar();
                }
            }
        });

        setupRecyclerView();

        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                SeekBar seekBar = (SeekBar) v;

                int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                textCurrentTime.setText(milliSecondsToTime(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerSeekBar.setProgress(0);
                imagePlayPause.setBackgroundResource(R.drawable.ic_play);
                textCurrentTime.setText("0:00");
                textTotalTime.setText("0:00");
                mediaPlayer.reset();
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerSeekBar.setSecondaryProgress(percent);
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                imagePlayPause.setEnabled(true);
                textTotalTime.setText(milliSecondsToTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                imagePlayPause.setBackgroundResource(R.drawable.ic_pause);
                updateSeekBar();
            }
        });


        prepareMediaPlayer(soundList.get(0).getSound());

    }


    private void setupRecyclerView() {
        rvSounds.setLayoutManager(new LinearLayoutManager(this));
        soundAdapter = new SoundAdapter(soundList, this);
        rvSounds.setAdapter(soundAdapter);
    }

    public void prepareMediaPlayer(String url) {
            playerSeekBar.setProgress(0);
            imagePlayPause.setBackgroundResource(R.drawable.ic_play);
            textCurrentTime.setText("0:00");
            textTotalTime.setText("0:00");
            mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

        }catch(Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTime(currentDuration));

        }
    };

    private void updateSeekBar() {
        if(mediaPlayer.isPlaying()) {
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private String milliSecondsToTime(long milliSeconds) {
        String timeString = "";
        String secondsString;

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) /1000);

        if(hours > 0) {
            timeString = hours + ":";
        }

        if(seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timeString = timeString + minutes + ":" + secondsString;

        return timeString;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }
}
