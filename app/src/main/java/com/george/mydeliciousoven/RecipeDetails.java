package com.george.mydeliciousoven;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecipeDetails extends AppCompatActivity implements IngredientsFragment.OnFragmentInteractionListener,
        StepsFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentVideoInteractionListener {

    private static final String RECIPE_NAME_TO_PASS = "recipe_name_to_pass";
    private static final String DESCRIPTION_OF_STEP = "description_of_step";
    private static final String VIDEO_OF_STEP = "video_of_step";
    private static final String DESCRIPTION_FOR_FRAGMENT = "description_for_fragment";
    private static final String VIDEO_FOR_FRAGMENT = "video_for_fragment";
    private static final String THUMBNAIL_FOR_FRAGMENT = "thumbnail_for_fragment";
    private static final String THUMBNAIL_OF_STEP = "thumbnail_of_step";

    private long playbackPositionOnresume;
    private int currentWindowOnResume;
    private boolean playWhenReadyOnResume = true;
    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW = "current_window";
    private static final String PLAY_WHEN_READY = "play_when_ready";

    private static final String ARTICLE_SCROLL_POSITION = "article_scroll_position";

    private String recipeName, ingredientsFromFragment;
    private String descriptionPassed, videoPassed;
    private String thumbnailPassed = "";
    private static final String LOG_TAG = RecipeDetails.class.getSimpleName();
    private boolean mTwoPaneDetails;
    private Bundle bundleForVideo;
    private ActionBar ab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scrollOfFrameLayouts)
    ScrollView mScrollView;
    @BindView(R.id.fab)
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        //trying to save scroll position
        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray(ARTICLE_SCROLL_POSITION);
            if (position != null)
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });
        }

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_NAME_TO_PASS)) {
            recipeName = intent.getStringExtra(RECIPE_NAME_TO_PASS);
            Log.d(LOG_TAG, recipeName);
        }

        ab = getSupportActionBar();
        ab.setTitle(recipeName);
        ab.setDisplayHomeAsUpEnabled(true);

        //Upon creation we check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            //Not used Butterknife for this comparison
            if (findViewById(R.id.linear_master_detail_tablet) != null) {

                mTwoPaneDetails = true;

                if (savedInstanceState == null) {

                    //the first two fragments in the left
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    Bundle bundle = new Bundle();
                    bundle.putString(RECIPE_NAME_TO_PASS, recipeName);
                    // Creating a new ingredients fragment
                    IngredientsFragment ingredFragment = new IngredientsFragment();
                    ingredFragment.setArguments(bundle);
                    // Add the fragment to its container using a transaction
                    fragmentManager.beginTransaction().add(R.id.ingredients_container_tablet, ingredFragment).commit();

                    //Steps  fragment
                    StepsFragment stepiFragment = new StepsFragment();
                    stepiFragment.setArguments(bundle);

                    fragmentManager.beginTransaction().add(R.id.steps_container_tablet, stepiFragment).commit();

                    //the video fragment in the right
                    Bundle bundle2 = new Bundle();
                    bundle2.putString(DESCRIPTION_FOR_FRAGMENT, "");
                    bundle2.putString(VIDEO_FOR_FRAGMENT, "");
                    bundle2.putString(THUMBNAIL_FOR_FRAGMENT, "");

                    VideoFragment videoFragm = new VideoFragment();
                    videoFragm.setArguments(bundle2);
                    // Add the fragment to its container using a transaction
                    fragmentManager.beginTransaction().add(R.id.video_details_container_tablet, videoFragm).commit();
                } else {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    playbackPositionOnresume = sharedPreferences.getLong(PLAYBACK_POSITION, 0);
                    currentWindowOnResume = sharedPreferences.getInt(CURRENT_WINDOW, 0);
                    playWhenReadyOnResume = sharedPreferences.getBoolean(PLAY_WHEN_READY, true);
                    descriptionPassed = sharedPreferences.getString(DESCRIPTION_FOR_FRAGMENT, "");
                    videoPassed = sharedPreferences.getString(VIDEO_FOR_FRAGMENT, "");
                    thumbnailPassed = sharedPreferences.getString(THUMBNAIL_FOR_FRAGMENT, "");
                    //When coming from videoactivity landscape and we go to recipedetails landscape two pane
                    //the video fragment in the right
                    Bundle bundle3 = new Bundle();
                    bundle3.putLong(PLAYBACK_POSITION, playbackPositionOnresume);
                    bundle3.putInt(CURRENT_WINDOW, currentWindowOnResume);
                    bundle3.putBoolean(PLAY_WHEN_READY, playWhenReadyOnResume);
                    bundle3.putString(DESCRIPTION_FOR_FRAGMENT, descriptionPassed);
                    bundle3.putString(VIDEO_FOR_FRAGMENT, videoPassed);
                    bundle3.putString(THUMBNAIL_FOR_FRAGMENT, thumbnailPassed);

                    VideoFragment videoFragm = new VideoFragment();
                    videoFragm.setArguments(bundle3);
                    // Add the fragment to its container using a transaction
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.video_details_container_tablet, videoFragm).commit();
                }

            } else {

                if (savedInstanceState == null) {
                    mTwoPaneDetails = false;

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    Bundle bundle = new Bundle();
                    bundle.putString(RECIPE_NAME_TO_PASS, recipeName);
                    // Creating a new ingredients fragment
                    IngredientsFragment ingredFragment = new IngredientsFragment();
                    ingredFragment.setArguments(bundle);
                    // Add the fragment to its container using a transaction
                    fragmentManager.beginTransaction().add(R.id.ingredients_container, ingredFragment).commit();

                    //Steps  fragment
                    StepsFragment stepiFragment = new StepsFragment();
                    stepiFragment.setArguments(bundle);

                    fragmentManager.beginTransaction().add(R.id.steps_container, stepiFragment).commit();
                }
            }
        } else {
            Toast.makeText(RecipeDetails.this, R.string.please_connect_to_internet, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onFragmentInteraction(String description, String videoUrl, String thumbnailUrl) {

        bundleForVideo = new Bundle();
        bundleForVideo.putString(DESCRIPTION_OF_STEP, description);
        bundleForVideo.putString(VIDEO_OF_STEP, videoUrl);
        bundleForVideo.putString(DESCRIPTION_FOR_FRAGMENT, description);
        bundleForVideo.putString(VIDEO_FOR_FRAGMENT, videoUrl);
        bundleForVideo.putString(THUMBNAIL_FOR_FRAGMENT, thumbnailUrl);
        bundleForVideo.putString(THUMBNAIL_OF_STEP, thumbnailUrl);

        if (mTwoPaneDetails) {
            VideoFragment videoFragm = new VideoFragment();
            videoFragm.setArguments(bundleForVideo);
            // Add the fragment to its container using a transaction
            getSupportFragmentManager().beginTransaction().replace(R.id.video_details_container_tablet, videoFragm).commit();
        } else {

            Intent intent = new Intent(RecipeDetails.this, VideoActivity.class);
            intent.putExtras(bundleForVideo);

            startActivity(intent);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray(ARTICLE_SCROLL_POSITION,
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    public void onFragmentInteractionIngredients(String ingredients) {
        ingredientsFromFragment = ingredients;
    }

    @Override
    public void onFragmentInteraction(long playbaAKPosition, int currentWINDOW, boolean playwhenready, String description, String thumbnail, String video) {
    }

    @OnClick(R.id.fab)
    public void clickFabToWidget(View view) {
        if (ingredientsFromFragment != null) {
            updateWidgetWithIngredients(ingredientsFromFragment, recipeName);
        }
    }

    private void updateWidgetWithIngredients(String ingredienti, String recipeName) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetId = appWidgetManager.getAppWidgetIds(new ComponentName(this, OvenWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_text);

        OvenWidgetProvider.updateWidgetWithIngredents(this, appWidgetManager, ingredienti, recipeName, appWidgetId);

        Toast.makeText(RecipeDetails.this, recipeName + " " + getString(R.string.isDesired), Toast.LENGTH_LONG).show();

    }
}
