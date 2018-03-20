package com.george.mydeliciousoven;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.george.mydeliciousoven.network.NetworkUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepsFragment extends Fragment implements StepsRecyclerAdapter.StepsClickItemListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String recipeName, jsonResultsSteps;
    private static final int STEPS_LOADER = 47;
    private static final String RECIPE_NAME_TO_PASS = "recipe_name_to_pass";
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private LinearLayoutManager layoutManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private StepsRecyclerAdapter mStepsAdapter;
    private ArrayList<Steps> mStepsList;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.fragmentStepsRecycler)
    RecyclerView mRecyclerSteps;

    public StepsFragment() {
        // Required empty public constructor
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
        void onFragmentInteraction(String string,String string2,String string3);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepsFragment newInstance(String param1, String param2) {
        StepsFragment fragment = new StepsFragment();
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

        //restore recycler view at same position
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //restore recycler view at same position
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, stepsView);

        //restore recycler view at same position
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }

        recipeName = this.getArguments().getString(RECIPE_NAME_TO_PASS);
        Log.e("stepsFragment", recipeName);

        mRecyclerSteps.setHasFixedSize(true);
        layoutManager  = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerSteps.setLayoutManager(layoutManager);
        mStepsList = new ArrayList<>();
        mStepsAdapter = new StepsRecyclerAdapter(getActivity(), mStepsList, this);
        mRecyclerSteps.setAdapter(mStepsAdapter);

        //Upon creation we check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Init loader
            android.support.v4.app.LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> internetLoader = loaderManager.getLoader(STEPS_LOADER);
            if (internetLoader == null) {
                loaderManager.initLoader(STEPS_LOADER, null, mLoaderSteps);
            } else {
                loaderManager.restartLoader(STEPS_LOADER, null, mLoaderSteps);
            }
        } else {
            ///Toast No internet
        }


        return stepsView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());
    }

    private LoaderManager.LoaderCallbacks mLoaderSteps = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<ArrayList<Steps>>(getActivity()) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public ArrayList<Steps> loadInBackground() {

                    try {
                        jsonResultsSteps = NetworkUtilities.makeHttpRequest(new URL(MainActivity.urlToFetchData)
                                , getActivity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Steps> stepsNameList = new ArrayList<>();
                    try {
                        stepsNameList = NetworkUtilities.getValuesFromJsonForSteps(jsonResultsSteps, getActivity(), recipeName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return stepsNameList;

                }
            };
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            ArrayList<Steps> arrayS = (ArrayList<Steps>) data;
            mStepsList = arrayS;
            mStepsAdapter.setStepsData(mStepsList);

            //restore recycler view position
            if(savedRecyclerLayoutState!=null){
                layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            }

        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };



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

    @Override
    public void onListItemClick(int itemIndex) {
        Steps sted = mStepsList.get(itemIndex);
        mListener.onFragmentInteraction(sted.getDescription(),sted.getVideoURL(),sted.getThumbnailURL());
    }


}
