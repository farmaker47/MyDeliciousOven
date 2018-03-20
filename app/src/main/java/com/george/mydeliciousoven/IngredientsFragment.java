package com.george.mydeliciousoven;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.mydeliciousoven.network.NetworkUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IngredientsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IngredientsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int INGREDIENTS_LOADER = 44;
    private String jsonResultsIngredients,recipeName,stringForExpandable;
    private static final String RECIPE_NAME_TO_PASS = "recipe_name_to_pass";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.textViewIngredient) TextView mTextIngredients;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IngredientsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IngredientsFragment newInstance(String param1, String param2) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recipeName = this.getArguments().getString(RECIPE_NAME_TO_PASS);

        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this,rootView);

        //Upon creation we check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Begin loader
            android.support.v4.app.LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> internetLoader = loaderManager.getLoader(INGREDIENTS_LOADER);
            if (internetLoader == null) {
                loaderManager.initLoader(INGREDIENTS_LOADER, null, mLoaderIngredients);
            } else {
                loaderManager.restartLoader(INGREDIENTS_LOADER, null, mLoaderIngredients);
            }
        } else {
            ///Toast No internet
        }

        stringForExpandable = "";

        return rootView;
    }

    //setting string to "" so not to have double inserts
    @Override
    public void onPause() {
        super.onPause();
        stringForExpandable = "";
    }

    private LoaderManager.LoaderCallbacks mLoaderIngredients = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<ArrayList<Ingredients>>(getActivity()) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public ArrayList<Ingredients> loadInBackground() {

                    try {
                        jsonResultsIngredients = NetworkUtilities.makeHttpRequest(new URL(MainActivity.urlToFetchData)
                                , getActivity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Ingredients> ingredientsNameList = new ArrayList<>();
                    try {
                        ingredientsNameList = NetworkUtilities.getValuesFromJsonForIngredients(jsonResultsIngredients, getActivity(),recipeName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return ingredientsNameList;

                }
            };
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {

            ArrayList<Ingredients> arrayIngr = (ArrayList<Ingredients>) data;
           /* Ingredients ingo = arrayIngr.get(0);
            Log.e("FragmentQuantity",ingo.getQuantity());*/

            if (arrayIngr.size() > 0) {
                for (int j = 0; j < arrayIngr.size(); j++) {

                    Ingredients mIngredients = arrayIngr.get(j);

                    String quantity = mIngredients.getQuantity();
                    String measure = mIngredients.getMeasure();
                    String ingredient = mIngredients.getIngredient();


                    String total = "▶" + " " + quantity + " " + measure + " of " + ingredient + "\n";

                    stringForExpandable += total;
                }
                mTextIngredients.setText(stringForExpandable);


            } else {
                mTextIngredients.setText(getResources().getString(R.string.noIngredients));
            }


            /*for(Ingredients ingredients:arrayIngr){

                mTextIngredients.append(ingredients.getQuantity()+ingredients.getMeasure()+ingredients.getIngredient());

            }*/

            mListener.onFragmentInteractionIngredients(stringForExpandable);

        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteractionIngredients(String string);
    }
}
