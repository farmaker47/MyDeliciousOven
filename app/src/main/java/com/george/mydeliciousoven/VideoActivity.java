package com.george.mydeliciousoven;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class VideoActivity extends AppCompatActivity implements VideoFragment.OnFragmentVideoInteractionListener{


    private static final String DESCRIPTION_OF_STEP = "description_of_step";
    private static final String VIDEO_OF_STEP = "video_of_step";
    private static final String DESCRIPTION_FOR_FRAGMENT = "description_for_fragment";
    private static final String VIDEO_FOR_FRAGMENT = "video_for_fragment";
    private String descriptionPassed,videoPassed;
    private static final String LOG_TAG = VideoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        if (savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent.hasExtra(DESCRIPTION_OF_STEP)) {
                descriptionPassed = intent.getStringExtra(DESCRIPTION_OF_STEP);
                Log.e(LOG_TAG, descriptionPassed);
            }
            if(intent.hasExtra(VIDEO_OF_STEP)){
                videoPassed = intent.getStringExtra(VIDEO_OF_STEP);
                Log.e(LOG_TAG, videoPassed);
            }


            FragmentManager fragmentManager = getSupportFragmentManager();

            Bundle bundle = new Bundle();
            bundle.putString(DESCRIPTION_FOR_FRAGMENT, descriptionPassed);
            bundle.putString(VIDEO_FOR_FRAGMENT, videoPassed);
            // Creating a new ingredients fragment

            VideoFragment videoFragm = new VideoFragment();
            videoFragm.setArguments(bundle);
            // Add the fragment to its container using a transaction
            fragmentManager.beginTransaction().add(R.id.video_details_container, videoFragm).commit();
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onFragmentInteraction(String string) {

    }
}
