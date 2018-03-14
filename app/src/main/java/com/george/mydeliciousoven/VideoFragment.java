package com.george.mydeliciousoven;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoFragment.OnFragmentVideoInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String descriptionPassed, videoPassed;
    private static final String DESCRIPTION_FOR_FRAGMENT = "description_for_fragment";
    private static final String DESCRIPTION_STATE_FOR_FRAGMENT = "description_state_for_fragment";
    private static final String VIDEO_FOR_FRAGMENT = "video_for_fragment";
    private static final String VIDEO_STATE_FOR_FRAGMENT = "video_state_for_fragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.videoTextView)
    TextView videoTextView;
    @BindView(R.id.instructionsOfVideoTextView)
    TextView instructionsOfVideoTextView;

    private OnFragmentVideoInteractionListener mListener;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
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
        View rootViewVideo = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, rootViewVideo);
        descriptionPassed = this.getArguments().getString(DESCRIPTION_FOR_FRAGMENT);
        videoPassed = this.getArguments().getString(VIDEO_FOR_FRAGMENT);

        if (savedInstanceState != null) {
            descriptionPassed = savedInstanceState.getString(DESCRIPTION_STATE_FOR_FRAGMENT);
            videoPassed = savedInstanceState.getString(VIDEO_STATE_FOR_FRAGMENT);
        }


        instructionsOfVideoTextView.setText(descriptionPassed);
        Log.e("VideoFragment", descriptionPassed);

        videoTextView.setText(videoPassed);
        Log.e("VideoFragment", videoPassed);

        return rootViewVideo;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DESCRIPTION_STATE_FOR_FRAGMENT, descriptionPassed);
        outState.putString(VIDEO_STATE_FOR_FRAGMENT, videoPassed);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentVideoInteractionListener) {
            mListener = (OnFragmentVideoInteractionListener) context;
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
    public interface OnFragmentVideoInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String string);
    }
}
