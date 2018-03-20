package com.george.mydeliciousoven;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity implements VideoFragment.OnFragmentVideoInteractionListener {


    private static final String DESCRIPTION_OF_STEP = "description_of_step";
    private static final String VIDEO_OF_STEP = "video_of_step";
    private static final String DESCRIPTION_FOR_FRAGMENT = "description_for_fragment";
    private static final String VIDEO_FOR_FRAGMENT = "video_for_fragment";
    private static final String THUMBNAIL_FOR_FRAGMENT = "thumbnail_for_fragment";
    private static final String THUMBNAIL_OF_STEP = "thumbnail_of_step";
    private String descriptionPassed, videoPassed, thumbNailPassed;
    private static final String LOG_TAG = VideoActivity.class.getSimpleName();

    /*private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW = "current_window";
    private static final String PLAY_WHEN_READY = "play_when_ready";*/

    private ActionBar ab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);


        if (savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent.hasExtra(DESCRIPTION_OF_STEP)) {
                descriptionPassed = intent.getStringExtra(DESCRIPTION_OF_STEP);
                Log.e(LOG_TAG, descriptionPassed);
            }
            if (intent.hasExtra(VIDEO_OF_STEP)) {
                videoPassed = intent.getStringExtra(VIDEO_OF_STEP);
                Log.e(LOG_TAG, videoPassed);
            }
            if (intent.hasExtra(THUMBNAIL_OF_STEP)) {
                thumbNailPassed = intent.getStringExtra(THUMBNAIL_OF_STEP);
                Log.e(LOG_TAG + LOG_TAG, thumbNailPassed);
            }


            FragmentManager fragmentManager = getSupportFragmentManager();

            Bundle bundle = new Bundle();
            bundle.putString(DESCRIPTION_FOR_FRAGMENT, descriptionPassed);
            bundle.putString(VIDEO_FOR_FRAGMENT, videoPassed);
            bundle.putString(THUMBNAIL_FOR_FRAGMENT, thumbNailPassed);/*
            bundle.putLong(PLAYBACK_POSITION,playbackPosition);
            bundle.putInt(CURRENT_WINDOW,currentWindow);
            bundle.putBoolean(PLAY_WHEN_READY,playWhenReady);*/

            // Creating a new ingredients fragment
            VideoFragment videoFragm = new VideoFragment();
            videoFragm.setArguments(bundle);
            // Add the fragment to its container using a transaction
            fragmentManager.beginTransaction().add(R.id.video_details_container, videoFragm).commit();
        }

        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
        ab.setTitle(R.string.recipesVideo);
    }

    @Override
    public void onFragmentInteraction(long playbaAKPosition, int currentWINDOW, boolean playwhenready,String description,String thumbnail,String video) {
        /*playbackPosition = playbaAKPosition;
        currentWindow = currentWINDOW;
        playWhenReady = playwhenready;*/
    }
}
