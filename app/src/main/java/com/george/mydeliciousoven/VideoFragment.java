package com.george.mydeliciousoven;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindDrawable;
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
public class VideoFragment extends Fragment implements ExoPlayer.EventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String descriptionPassed, videoPassed;
    private String thumbnailPassed = "";
    private static final String DESCRIPTION_FOR_FRAGMENT = "description_for_fragment";
    private static final String DESCRIPTION_STATE_FOR_FRAGMENT = "description_state_for_fragment";
    private static final String VIDEO_FOR_FRAGMENT = "video_for_fragment";
    private static final String VIDEO_STATE_FOR_FRAGMENT = "video_state_for_fragment";
    private static final String THUMBNAIL_FOR_FRAGMENT = "thumbnail_for_fragment";
    private static final String THUMB_STATE_FOR_FRAGMENT = "thumb_state_for_fragment";

    private SimpleExoPlayer mExoPlayer;

    private long playbackPosition;
    private static final String PLAYBACK_POSITION = "playback_position";
    private int currentWindow;
    private static final String CURRENT_WINDOW = "current_window";
    private boolean playWhenReady = true;
    private static final String PLAY_WHEN_READY = "play_when_ready";
    private static MediaSessionCompat mMediaSession;
    private static final String TAG = VideoFragment.class.getSimpleName();
    private PlaybackStateCompat.Builder mStateBuilder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.videoTextView)
    TextView videoTextView;
    @BindView(R.id.instructionsOfVideoTextView)
    TextView instructionsOfVideoTextView;
    @BindView(R.id.exoPlayerView)
    SimpleExoPlayerView mExoplayerView;
    @BindView(R.id.imageAboveExoplayer)
    ImageView imageAboveExo;

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
        thumbnailPassed = this.getArguments().getString(THUMBNAIL_FOR_FRAGMENT);

        if (savedInstanceState != null) {
            descriptionPassed = savedInstanceState.getString(DESCRIPTION_STATE_FOR_FRAGMENT);
            videoPassed = savedInstanceState.getString(VIDEO_STATE_FOR_FRAGMENT);
            thumbnailPassed = savedInstanceState.getString(THUMB_STATE_FOR_FRAGMENT);

            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        }

        instructionsOfVideoTextView.setText(descriptionPassed);
        Log.e("VideoFragment", descriptionPassed);

        videoTextView.setText(videoPassed);
        Log.e("VideoFragment", videoPassed);

        //if thumbnail is not present then we load an image in imageView
        if (!thumbnailPassed.equals("") && !thumbnailPassed.endsWith(".mp4")) {
            Picasso.with(getActivity()).load(thumbnailPassed).into(imageAboveExo);
            mExoplayerView.setVisibility(View.INVISIBLE);
        }else if(videoPassed.equals("")){
            mExoplayerView.setVisibility(View.INVISIBLE);
        }

        // Initialize the Media Session.
        initializeMediaSession();

        return rootViewVideo;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DESCRIPTION_STATE_FOR_FRAGMENT, descriptionPassed);
        outState.putString(VIDEO_STATE_FOR_FRAGMENT, videoPassed);
        outState.putString(THUMB_STATE_FOR_FRAGMENT, thumbnailPassed);

        outState.putLong(PLAYBACK_POSITION, playbackPosition);
        outState.putInt(CURRENT_WINDOW, currentWindow);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
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

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (thumbnailPassed.endsWith(".mp4")) {
                initializePlayer(thumbnailPassed);
                mExoplayerView.setVisibility(View.VISIBLE);
            } else{
                initializePlayer(videoPassed);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            if (thumbnailPassed.endsWith(".mp4")) {
                initializePlayer(thumbnailPassed);
                mExoplayerView.setVisibility(View.VISIBLE);
            } else{
                initializePlayer(videoPassed);
            }
        }
    }

    // is just an implementation detail to have a pure full screen experience
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mExoplayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    //Exoplayer event listeners
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

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

    //initialize Exoplayer
    private void initializePlayer(String mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mExoplayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "mydeliciousoven");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(mediaUri), new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(playWhenReady);
            mExoPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.release();
            mExoPlayer.removeListener(this);
            mExoPlayer = null;
        }
        /*mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;*/
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
