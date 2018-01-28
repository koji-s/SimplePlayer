package com.example.android.simpleplayer;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class Player extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    /**
     * TODO. 再生中にアクションバーの戻るでListViewに戻った時、再生しているMP3を停止する。
     * TODO. MP3対象ディレクトリを選択・変更できるようにする。
     */
    // A handler for passing drawing data to the UI thread.
    private Handler handler = new Handler();

    // Media player class for MP3 playback.
    private MediaPlayer mp;

    // All file names included in the album, received from FileList.
    private String[] fileNames;

    // Number of the fileNames to be played.
    private int fileNameNo;

    // MP3 file to be played back
    private String filePath;

    // Directory where MP3 files to be played are stored.
    private String folderPath = Environment.getExternalStorageDirectory().getPath() + "/Music/Duo3_0 復習用/";

    // Flag for seek bar control.
    private boolean wasPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_player);

        // Set the tool bar as action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Activate UP Navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Register Listener on each button on the screen.
        View btnPlayOnOff = findViewById(R.id.playOnOff);
        btnPlayOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAction(ButtonAction.PlayOnOff);
            }
        });
        View btnPrev = findViewById(R.id.prev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAction(ButtonAction.Prev);
            }
        });
        View btnNext = findViewById(R.id.next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAction(ButtonAction.Next);
            }
        });
        View btnPrev4Sec = findViewById(R.id.prev4Sec);
        btnPrev4Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAction(ButtonAction.Prev4Sec);
            }
        });
        View btnNext4Sec = findViewById(R.id.next4Sec);
        btnNext4Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAction(ButtonAction.Next4Sec);
            }
        });

        // Accept an intent.
        Intent intent = getIntent();
        fileNames = intent.getStringArrayExtra("List");
        fileNameNo = intent.getIntExtra("ListNo", 0);
        this.filePath = folderPath + fileNames[fileNameNo];

        // Start play.
        startMP3();
    }

    @Override
    protected void onStop() {
        Log.i("onStop", "onStop");
        super.onStop();
        if (mp != null) {
            mp.stop();
        }
    }

    private void buttonAction(ButtonAction action) {
        switch (action) {
            case PlayOnOff:
                playOnOff();
                break;
            case Next:
                next();
                break;
            case Prev:
                prev();
                break;
            case Next4Sec:
                next4Sec();
                break;
            case Prev4Sec:
                prev4Sec();
                break;
            default:
                break;
        }
    }

    private void playOnOff() {
        if (mp == null) {
            return;
        }
        if (mp.isPlaying()) {
            playOffMP3();
        } else {
            playOnMP3();
        }
    }

    private void next() {
        if (mp == null) {
            return;
        }
        if (mp.isPlaying()) {
            // stop, if play.
            mp.stop();
        }
        if (fileNameNo < (fileNames.length - 1)) {
            fileNameNo++;
        } else {
            fileNameNo = 0;
        }
        this.filePath = folderPath + fileNames[fileNameNo];
        startMP3();
    }

    private void prev() {
        if (mp == null) {
            return;
        }
        if (mp.isPlaying()) {
            // pause.
            playOffMP3();
        }
        if (mp.getCurrentPosition() < 500) {
            if (fileNameNo > 0) {
                fileNameNo--;
            } else {
                fileNameNo = 0;
            }
            this.filePath = folderPath + fileNames[fileNameNo];
            startMP3();
        } else {
            mp.seekTo(0);
            playOnMP3();
        }
    }

    private void next4Sec() {
        if (mp == null) {
            return;
        }
        if (mp.isPlaying()) {
            playOffMP3();
        }
        int last = mp.getDuration();
        int cp = mp.getCurrentPosition();
        int setTime = cp + 4000;
        if (setTime > last) {
            setTime = last;
        }
        mp.seekTo(setTime);
        playOnMP3();
    }

    private void prev4Sec() {
        if (mp == null) {
            return;
        }
        if (mp.isPlaying()) {
            playOffMP3();
        }
        int cp = mp.getCurrentPosition();
        int setTime = cp - 4000;
        if (setTime <= 0) {
            setTime = 0;
        }
        mp.seekTo(setTime);
        playOnMP3();
    }

    private void startMP3() {
        // "Album name" and "Title" are acquired from the metadata of the file to be played back.
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);
        String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String titleName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        mediaMetadataRetriever.release();

        // Information acquired from metadata is set in the display area of "Album name" and "Title" on the player
        // screen.
        TextView textView = (TextView) findViewById(R.id.titleName);
        textView.setText(albumName.concat(" / ").concat(titleName));

        // Start playing MP3 files.
        stopMP3();
        mp = new MediaPlayer();

        try {
            mp.setDataSource(filePath);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(false);
        mp.setOnCompletionListener(this);

        // get playing time.
        int playAllSec = mp.getDuration();
        Log.d("times", "" + playAllSec);

        TextView tv = (TextView) findViewById(R.id.maxTime);
        tv.setText(String.valueOf(Utils.formatTime(playAllSec)));

        // Make settings for the seek bar.
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(playAllSec);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        Log.d("SeekBar", String.valueOf(progress));

                        if (mp == null) {
                            return;
                        }

                        //
                        TextView currentTime = (TextView) findViewById(R.id.currentTime);
                        currentTime.setText(String.valueOf(Utils.formatTime(mp.getCurrentPosition())));

                        // Currently, if the MP3 file is being played, do nothing.
                        if (mp.isPlaying()) {
                            return;
                        }

                        mp.seekTo(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Log.d("SeekBar", "onStartTrackingTouch");

                        wasPlaying = mp.isPlaying();
                        playOffMP3();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Log.d("SeekBar", "onStopTrackingTouch");

                        // Separate processing depending on whether the state before touching Seekbar is "playing" or
                        // "stopped".
                        if (wasPlaying) {
                            playOnMP3();
                        }
                    }
                }

        );

        playOnOff();
    }

    private void playOnMP3() {
        if (mp == null) {
            return;
        }
        View btnPlayOnOff = findViewById(R.id.playOnOff);
        btnPlayOnOff.setBackgroundColor(Color.GREEN);

        mp.start();
        startPlayProgressUpdater();
    }

    private void playOffMP3() {
        if (mp == null) {
            return;
        }
        View btnPlayOnOff = findViewById(R.id.playOnOff);
        btnPlayOnOff.setBackgroundColor(Color.GRAY);

        try {
            mp.pause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void startPlayProgressUpdater() {
        TextView currentTime = (TextView) findViewById(R.id.currentTime);
        currentTime.setText(String.valueOf(Utils.formatTime(mp.getCurrentPosition())));
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(mp.getCurrentPosition());
        if (mp.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 100);
        }
    }

    /**
     * stop MP3 file.
     */
    private void stopMP3() {
        if (mp == null) {
            return;
        }
        try {
            mp.stop();
            mp.release();
            mp = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * When file playing is completed, move to the next file.
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        this.next();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("ActionBar", "onOptionsItemSelected");

        return super.onOptionsItemSelected(item);
    }
}


