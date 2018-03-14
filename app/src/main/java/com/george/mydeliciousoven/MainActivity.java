package com.george.mydeliciousoven;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.george.mydeliciousoven.network.NetworkUtilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Because json file from internet neverchanges I decide not to call everytime the loader to fetch data from internet.. So
        //I will fetch the data once and then app will query the SQLIte database
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
                ///Toast No internet
            }
        } else {
            //Query the already ready database
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
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

            //restore recycler view position
            if(savedRecyclerLayoutState!=null){
                mGridLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int itemIndex) {
        Intent intent = new Intent(MainActivity.this, RecipeDetails.class);
        Recipes rec = mRecipesList.get(itemIndex);
        intent.putExtra(RECIPE_NAME_TO_PASS, rec.getName());
        startActivity(intent);
    }
}
