package com.george.mydeliciousoven;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

public class RecipeDetails extends AppCompatActivity implements IngredientsFragment.OnFragmentInteractionListener,
        StepsFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentVideoInteractionListener {

    private static final String RECIPE_NAME_TO_PASS = "recipe_name_to_pass";
    private static final String DESCRIPTION_OF_STEP = "description_of_step";
    private static final String VIDEO_OF_STEP = "video_of_step";
    private static final String DESCRIPTION_FOR_FRAGMENT = "description_for_fragment";
    private static final String VIDEO_FOR_FRAGMENT = "video_for_fragment";
    private static final String THUMBNAIL_FOR_FRAGMENT = "thumbnail_for_fragment";
    private static final String THUMBNAIL_OF_STEP = "thumbnail_of_step";

    private String recipeName, ingredientsFromFragment;
    private static final String LOG_TAG = RecipeDetails.class.getSimpleName();
    private boolean mTwoPaneDetails;
    private Bundle bundleForVideo;
    private ActionBar ab;
    /*@BindView(R.id.linear_master_detail_tablet)LinearLayout linearForTablet;*/
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scrollOfFrameLayouts)
    ScrollView mScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        //trying to save scroll position
        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
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
            Log.e(LOG_TAG, recipeName);
        }

        ab = getSupportActionBar();
        ab.setTitle(recipeName);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWidgetWithIngredients(ingredientsFromFragment, recipeName);
            }
        });

        if (findViewById(R.id.linear_master_detail_tablet) != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

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
            }

        } else if (findViewById(R.id.linear_master_detail_tablet) != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTwoPaneDetails = false;

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
        } else {

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

        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    public void onFragmentInteractionIngredients(String ingredients) {
        ingredientsFromFragment = ingredients;
        Log.e("ingredientsINCOMING", ingredientsFromFragment);

    }

    @Override
    public void onFragmentInteraction(String string) {

    }

    private void updateWidgetWithIngredients(String ingredienti, String recipeName) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetId = appWidgetManager.getAppWidgetIds(new ComponentName(this, OvenWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_text);

        OvenWidgetProvider.updateWidgetWithIngredents(this, appWidgetManager, ingredienti, recipeName, appWidgetId);

        Toast.makeText(RecipeDetails.this, recipeName + " " + getString(R.string.isDesired),Toast.LENGTH_LONG).show();

    }
}
