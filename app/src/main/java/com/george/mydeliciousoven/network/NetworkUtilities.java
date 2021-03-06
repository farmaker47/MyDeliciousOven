package com.george.mydeliciousoven.network;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.george.mydeliciousoven.Ingredients;
import com.george.mydeliciousoven.R;
import com.george.mydeliciousoven.Recipes;
import com.george.mydeliciousoven.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by farmaker1 on 12/03/2018.
 */

public class NetworkUtilities {

    private static final String LOG_TAG = NetworkUtilities.class.getSimpleName();

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url, Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            if (urlConnection != null && context != null) {
                urlConnection.setRequestMethod(context.getString(R.string.get));
            }
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                if (context != null) {
                    Log.d(context.getString(R.string.success), context.getString(R.string.twoHundred));
                }
            } else {
                if (context != null) {
                    Log.d(LOG_TAG, context.getString(R.string.errorResponseCode) + urlConnection.getResponseCode());
                }
            }
        } catch (IOException e) {
            if (context != null) {
                Log.e(LOG_TAG, context.getString(R.string.problemRetrivingJson), e);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //here fron the Json response we retrieve the information that we need name, ingredients, steps e.t.c
    public static ArrayList<Recipes> getValuesFromJson(String JSONdata, Context context) {

        if (JSONdata == null) {
            return null;
        }

        ArrayList<Recipes> recipes = new ArrayList<>();

        try {
            JSONArray root = new JSONArray(JSONdata);

            for (int i = 0; i < root.length(); i++) {

                String name, servings, image, id2;
                JSONObject recipesObject = root.getJSONObject(i);
                id2 = recipesObject.optString("id");
                name = recipesObject.optString("name");
                servings = recipesObject.optString("servings");
                image = recipesObject.getString("image");

                recipes.add(new Recipes(id2, name, servings, image));
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, context.getString(R.string.problemParsingJson), e);
        }
        return recipes;
    }

    public static ArrayList<Ingredients> getValuesFromJsonForIngredients(String jsonResultsIngredients, FragmentActivity activity, String recipeName) {

        if (jsonResultsIngredients == null) {
            return null;
        }

        ArrayList<Ingredients> ingrediento = new ArrayList<>();

        try {
            JSONArray root = new JSONArray(jsonResultsIngredients);

            for (int i = 0; i < root.length(); i++) {

                String quantity = null, measure = null, ingredient = null;

                JSONObject recipesObject = root.getJSONObject(i);

                if (recipesObject.optString("name").equals(recipeName)) {
                    //Get info from ingredients
                    JSONArray ingredientsArray = recipesObject.getJSONArray("ingredients");
                    for (int j = 0; j < ingredientsArray.length(); j++) {

                        JSONObject singleIngredientObject = ingredientsArray.getJSONObject(j);
                        quantity = singleIngredientObject.optString("quantity");
                        measure = singleIngredientObject.optString("measure");
                        ingredient = singleIngredientObject.optString("ingredient");

                        ingrediento.add(new Ingredients(quantity, measure, ingredient));
                    }
                }
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, activity.getString(R.string.problemParsingJson), e);
        }
        return ingrediento;
    }

    public static ArrayList<Steps> getValuesFromJsonForSteps(String jsonResultsSteps, FragmentActivity activity, String recipeName) {

        if (jsonResultsSteps == null) {
            return null;
        }

        ArrayList<Steps> stepsOfOven = new ArrayList<>();

        try {
            JSONArray root = new JSONArray(jsonResultsSteps);

            for (int i = 0; i < root.length(); i++) {

                JSONObject recipesObject = root.getJSONObject(i);
                String id = null, shortDescription = null, description = null, videoURL = null, thumbnailURL = null;

                if (recipesObject.optString("name").equals(recipeName)) {
                    //Get the steps
                    JSONArray stepsArray = recipesObject.getJSONArray("steps");
                    for (int k = 0; k < stepsArray.length(); k++) {

                        JSONObject stepsObject = stepsArray.getJSONObject(k);
                        id = stepsObject.optString("id");
                        shortDescription = stepsObject.optString("shortDescription");
                        description = stepsObject.optString("description");
                        videoURL = stepsObject.optString("videoURL");
                        thumbnailURL = stepsObject.getString("thumbnailURL");

                        stepsOfOven.add(new Steps(id, shortDescription, description, videoURL, thumbnailURL));
                    }
                }
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, activity.getString(R.string.problemParsingJson), e);
        }
        return stepsOfOven;
    }
}
