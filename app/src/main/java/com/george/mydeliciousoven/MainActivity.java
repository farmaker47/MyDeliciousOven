package com.george.mydeliciousoven;
/*License
        Copyright 2013 Jake Wharton

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.george.mydeliciousoven.idlingResource.SimpleIdlingResource;
import com.george.mydeliciousoven.network.NetworkUtilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainGridAdapter.RecipesClickItemListener {

    private int firstTimeOpened;
    private static final String FIRST_TIME_OPENED = "first_time_opened";
    private static final String RECIPE_NAME_TO_PASS = "recipe_name_to_pass";
    private static final String QUERY_INTERNET_BUNDLE = "internet_query";
    public static final String urlToFetchData = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final int INTERNET_LOADER = 23;
    private String jsonResults;
    private URL urlToFetchDataInsideLoader;
    private GridLayoutManager mGridLayoutManager;
    private MainGridAdapter mGridAdapter;
    private ArrayList<Recipes> mRecipesList;
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    @BindView(R.id.mainRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindString(R.string.tsekare_me_internet)
    String textForShare;
    @BindString(R.string.send_app_header_of_intent)
    String sendDisplay;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Get the IdlingResource instance
        getIdlingResource();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        firstTimeOpened = sharedPreferences.getInt(FIRST_TIME_OPENED, 0);

        mRecyclerView.setHasFixedSize(true);

        //setting Context and column number for grid
        //Ckeck also for tablet
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager = new GridLayoutManager(this, 1);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, 2);
        }
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecipesList = new ArrayList<>();
        //Setting the adapter
        mGridAdapter = new MainGridAdapter(this, mRecipesList, this);
        mRecyclerView.setAdapter(mGridAdapter);

        //we check if is first time opened if in the future we want to save a recipe in database
        if (firstTimeOpened == 0) {
            //Upon creation we check if there is internet connection
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnected()) {
                Bundle queryBundle = new Bundle();
                //we pass a bundle parameter that we will use to fetch data from specific URL
                queryBundle.putString(QUERY_INTERNET_BUNDLE, urlToFetchData);
                android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                Loader<String> internetLoader = loaderManager.getLoader(INTERNET_LOADER);
                if (internetLoader == null) {
                    loaderManager.initLoader(INTERNET_LOADER, queryBundle, mLoaderInternet);
                } else {
                    loaderManager.restartLoader(INTERNET_LOADER, queryBundle, mLoaderInternet);
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_LONG).show();
            }
        } else {
            //Query the already ready database with recipes
            //Not implemented as not desired
            //We will give firstTimeOpened value = 1
        }

        setSupportActionBar(toolbar);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mGridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //restore recycler view at same position
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
    }


    @OnClick(R.id.fab)
    public void clickFab(View view) {
        Intent shire = new Intent();
        shire.setAction(Intent.ACTION_SEND);
        shire.putExtra(Intent.EXTRA_TEXT, textForShare);
        shire.setType("text/plain");
        startActivity(Intent.createChooser(shire, sendDisplay));
    }

    //Loader to fetch data from internet URL
    private LoaderManager.LoaderCallbacks mLoaderInternet = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<Recipes>>(MainActivity.this) {

                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public ArrayList<Recipes> loadInBackground() {

                    //Is idle = false
                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(false);
                    }

                    String queryUrl = args.getString(QUERY_INTERNET_BUNDLE);
                    if (queryUrl == null) {
                        //if there received string is empty just return
                        return null;
                    }

                    try {
                        urlToFetchDataInsideLoader = new URL(queryUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonResults = NetworkUtilities.makeHttpRequest(urlToFetchDataInsideLoader, MainActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Recipes> recipeNameList = new ArrayList<>();
                    try {
                        recipeNameList = NetworkUtilities.getValuesFromJson(jsonResults, MainActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    return recipeNameList;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {

            ArrayList<Recipes> arrayL = (ArrayList<Recipes>) data;
            mRecipesList = arrayL;
            mGridAdapter.setRecipesData(mRecipesList);

            //set idle to true
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }

            //restore recycler view position
            if (savedRecyclerLayoutState != null) {
                mGridLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    @Override
    public void onListItemClick(int itemIndex) {
        Intent intent = new Intent(MainActivity.this, RecipeDetails.class);
        Recipes rec = mRecipesList.get(itemIndex);
        intent.putExtra(RECIPE_NAME_TO_PASS, rec.getName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
