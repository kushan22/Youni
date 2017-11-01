package com.youni.youni;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener {
    private String YOUTUBE_PLAYLIST = "";
    private YouTube mYoutubeDataApi;
    private Playlist mPlaylist;
    private String topicName, userName;
    private long alternativeTimeDuration = 0;
    private YouTubePlayer player;
    private long timeSpentTillNow = 0;
    private long restTime = 0;
    private long totalDuration;
    String loadedVideoId;
    ArrayList<String> videoIds = new ArrayList<>();
    String videoIdForTimeCalc;
    private boolean isPaused = false;
    private boolean isCanceled = false;
    private long timeRemaining = 0;
    private String count = "";
    int pos = 0;
    private long totalTimeSpent = 0;
    TextView tv3;
    private static final String COUNTPREF = "count";
    ProgressDialog dialog;
    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
    /* private static final String URL = "http://viesr.com/youniService.asmx?WSDL";
     private static final String NAMESPACE = "http://viesr.com/";
     private static final String ACTION = "http://viesr.com/updateCourseState";
     private static final String METHOD = "updateCourseState";
     private static final String URL1 = "http://viesr.com/youniService.asmx?WSDL";
     private static final String NAMESPACE1 = "http://viesr.com/";
     private static final String ACTION1 = "http://viesr.com/getWatchedvideos";
     private static final String METHOD1 = "getWatchedvideos";
     private static final String URL2 = "http://viesr.com/youniService.asmx?WSDL";
     private static final String NAMESPACE2 = "http://viesr.com/";
     private static final String ACTION2 = "http://viesr.com/InsertWatchedVideos";
     private static final String METHOD2 = "InsertWatchedVideos";
     private static final String URL3 = "http://viesr.com/youniService.asmx?WSDL";
     private static final String NAMESPACE3 = "http://viesr.com/";
     private static final String ACTION3 = "http://viesr.com/updateWatchedVideos";
     private static final String METHOD3 = "updateWatchedVideos";*/
    RecyclerView rv1, rv2;
    TextView tv1, tv2;
    RecyclerView.LayoutManager mLayoutManager;
    private PlayListCardAdapter mAdapter;
    private WatchedAdapter wAdapter;
    //YouTubePlayerView youView;
    String videoId;
    // YouTubePlayer mPlayer;
    Toolbar toolbar;
    @SuppressLint("InlinedApi")
    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    @SuppressLint("InlinedApi")
    private static final int LANDSCAPE_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    private boolean mAutoRotation = false;
    private long countInMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAutoRotation = Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

        setContentView(R.layout.content_main);
       /* toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/


        if (Apikey.YOUTUBE_API_KEY.startsWith("YOUR_API_KEY")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Edit ApiKey.java and replace \"YOUR_API_KEY\" with your Applications Browser API Key")
                    .setTitle("Missing API Key")
                    .setNeutralButton("Ok, I got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (savedInstanceState == null) {


            Intent courseReceiver = getIntent();
            YOUTUBE_PLAYLIST = courseReceiver.getStringExtra("playlist");
            topicName = courseReceiver.getStringExtra("topicNameCompleted");

            SharedPreferences prefs = getSharedPreferences(Signin.PREF, Context.MODE_PRIVATE);
            userName = prefs.getString(Signin.USER, "user");


          /*  SharedPreferences topicPref = getSharedPreferences(COUNTPREF,Context.MODE_PRIVATE);
            String currentTopic = topicPref.getString("COUNT","Any Topic");
            if (!currentTopic.equals("Any Topic")){

                Toast.makeText(getApplicationContext(),"You were recently at " + currentTopic,Toast.LENGTH_SHORT).show();

            }*/




           /* youView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            youView.initialize(Apikey.YOUTUBE_API_KEY,this);*/
            YouTubePlayerSupportFragment frag =
                    (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
            frag.initialize(Apikey.YOUTUBE_API_KEY, this);


            mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null)
                    .setApplicationName(getResources().getString(R.string.app_name))
                    .build();
            Log.i("youtube", mYoutubeDataApi.toString());

            Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);

            rv1 = (RecyclerView) findViewById(R.id.youtube_recycler_view);
            rv2 = (RecyclerView) findViewById(R.id.watchedVideos);
            tv1 = (TextView) findViewById(R.id.havenotwatched);
            tv2 = (TextView) findViewById(R.id.totalVideos);
            tv3 = (TextView) findViewById(R.id.timer);
            rv2.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv2.setLayoutManager(linearLayoutManager);
            rv1.setHasFixedSize(true);

            Resources resources = getResources();
            if (resources.getBoolean(R.bool.isTablet)) {
                // use a staggered grid layout if we're on a large screen device
                mLayoutManager = new StaggeredGridLayoutManager(resources.getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL);
            } else {
                // use a linear layout on phone devices
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
            }


            rv1.setLayoutManager(mLayoutManager);


            if (mPlaylist != null) {

                initAdapter(mPlaylist);
            } else {

                mPlaylist = new Playlist(YOUTUBE_PLAYLIST);
                initAdapter(mPlaylist);

                new GetPlaylistAsyncTask(mYoutubeDataApi) {

                    @Override
                    protected void onPostExecute(Pair<String, List<Video>> stringListPair) {
                        super.onPostExecute(stringListPair);
                        handleGetPlaylistResult(mPlaylist, stringListPair);

                    }
                }.execute(mPlaylist.playlistId, mPlaylist.getNextPageToken());
            }


            new getWatchedVideos().execute("");


        }

    }


   @Override
    protected void onPause() {
        super.onPause();



           Long timeSpent=  timeSpentTillNow + Long.parseLong(tv3.getText().toString()) + restTime;

           tv3.setText("0");
           timeSpentTillNow = 0;
           restTime = 0;


           count = String.valueOf(timeSpent);
           //Toast.makeText(getApplicationContext(), "" + count, Toast.LENGTH_SHORT).show();
           countInMinutes = Long.parseLong(count) /60;
         //  Toast.makeText(MainActivity.this, "" + countInMinutes, Toast.LENGTH_SHORT).show();
           new SameSetTime().execute("");



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        player = null;

    }

    @Override
    protected void onResume() {
        super.onResume();

        timeRemaining = 0;
        playbackEventListener.onPaused();
    }

    @Override
    protected void onStop() {
        super.onStop();

        player = null;

    }

    @Override
    public void onBackPressed() {


      /*  count = (tv3.getText().toString());
        countInMinutes = Long.parseLong(count) / 60;

        Toast.makeText(MainActivity.this, "" + countInMinutes, Toast.LENGTH_SHORT).show();
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("");
        dialog.setMessage("Saving your Progress");
        dialog.setCancelable(false);
        dialog.show();
        new SetTotalTime().execute("");*/
        Intent backIntent = new Intent(MainActivity.this, Courses.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(backIntent);
       // finish();
        return;

    }





    public String getTimeDataFromJson(String jsonString) {

        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    private class SameSetTime extends AsyncTask<String,String,String> {


        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {


            final String baseUrl = "http://www.youni.co.in/courses/courses_time_spent.php";
            final String QUERY_TIME = "time_spent";
            final String QUERY_EMAIL = "email";
            final String QUERY_PLAYLISTID = "playlistid";

            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_TIME, String.valueOf(countInMinutes)).
                    appendQueryParameter(QUERY_EMAIL, userName).
                    appendQueryParameter(QUERY_PLAYLISTID, YOUTUBE_PLAYLIST).build();

            try {
                URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                if (is == null)
                    return "Network Problem";


                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");


                }


                if (sb.length() == 0) {

                    return "Network Problem";

                }


                res = getTimeDataFromJson(sb.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (request != null)
                    request.disconnect();
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("200")){

               // Toast.makeText(MainActivity.this,"Time Inserted",Toast.LENGTH_SHORT).show();
                Log.i("Time0Inserted","Successful");


            }else if (s.equals("0")){


                Toast.makeText(MainActivity.this,"User not available",Toast.LENGTH_SHORT).show();
               // dialog.dismiss();
            }else if (s.equals("404")){

                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
               // dialog.dismiss();
            }else {

                Toast.makeText(MainActivity.this,"Network Problem",Toast.LENGTH_SHORT).show();
                Intent backIntent = new Intent(MainActivity.this, Courses.class);
                startActivity(backIntent);
                //dialog.dismiss();
            }
        }

    }

    private void initAdapter(final Playlist playlist) {
        // create the adapter with our playlist and a callback to handle when we reached the last item
        mAdapter = new PlayListCardAdapter(playlist, new LastItemReachedListener() {
            @Override
            public void onLastItem(int position, String nextPageToken) {
                new GetPlaylistAsyncTask(mYoutubeDataApi) {
                    @Override
                    public void onPostExecute(Pair<String, List<Video>> result) {
                        handleGetPlaylistResult(playlist, result);
                    }
                }.execute(playlist.playlistId, playlist.getNextPageToken());
            }
        });
        rv1.setAdapter(mAdapter);
    }


    private void handleGetPlaylistResult(Playlist playlist, Pair<String, List<Video>> result) {
        if (result == null) return;
        final int positionStart = playlist.size();
        playlist.setNextPageToken(result.first);
        playlist.addAll(result.second);
        mAdapter.notifyItemRangeInserted(positionStart, result.second.size());
    }

    @Override
    public void onInitializationSuccess(final YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean b) {


        this.player = youTubePlayer;



        rv1.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), new RecyclerItemListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                pos = position;
                Video mVideo = mPlaylist.get(position);
                videoId = mVideo.getId();
                timeRemaining = 0;
                restTime = restTime + Long.parseLong(tv3.getText().toString());
             //   Toast.makeText(getApplicationContext(),"" + restTime,Toast.LENGTH_SHORT).show();





              /*  SharedPreferences prefs = getSharedPreferences(COUNTPREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                String title = mVideo.getSnippet().getTitle();
                editor.putString("COUNT",title);
                editor.commit();*/


                //  videoId = mVideo.getId();
                onInitializationSuccess(provider, youTubePlayer, b);
                //  Toast.makeText(getApplicationContext(),"HEllo",Toast.LENGTH_SHORT).show();


            }
        }));


        player.setOnFullscreenListener(this);
        if (mAutoRotation) {
            player.addFullscreenControlFlag(player.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    | player.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    | player.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
                    | player.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        } else {
            player.addFullscreenControlFlag(player.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    | player.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    | player.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        }
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!b) {


          /*  if (pos != 0) {

                youTubePlayer.cuePlaylist(YOUTUBE_PLAYLIST, pos, 1000);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            } else {

                youTubePlayer.cuePlaylist(YOUTUBE_PLAYLIST);

                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

            }*/


            if (videoId != null) {

                player.loadVideo(videoId);
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                player.addFullscreenControlFlag(player.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            } else {


                player.cuePlaylist(YOUTUBE_PLAYLIST);

                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

            }

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {

            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {

            String errorMessage = String.format("There was a problem in initialising youtube player", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFullscreen(boolean fullsize) {
        if (fullsize) {
            setRequestedOrientation(LANDSCAPE_ORIENTATION);


        } else {
            setRequestedOrientation(PORTRAIT_ORIENTATION);

        }
    }

 /*   @Override
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
        if (id == android.R.id.home) {

            // Toast.makeText(getApplicationContext(),"Home Button Clicked",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Courses.class);
            startActivity(intent);
            finish();

        }

        return true;
    }*/


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (player != null)
                player.setFullscreen(true);


        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (player != null)
                player.setFullscreen(false);
        }
    }

    public interface LastItemReachedListener {
        void onLastItem(int position, String nextPageToken);
    }


    private class putCoureCompleteFlag extends AsyncTask<String, String, String> {


        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {

           /* SoapObject request = new SoapObject(NAMESPACE, METHOD);
            request.addProperty("username", userName);
            request.addProperty("topicName", topicName);
            request.addProperty("playlist", YOUTUBE_PLAYLIST);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;


            HttpTransportSE transport = new HttpTransportSE(URL);
            try {
                transport.call(ACTION, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            if (envelope.bodyIn instanceof SoapFault) {

                String str = ((SoapFault) envelope.bodyIn).faultstring;
                return str;

            } else {

                SoapObject response = (SoapObject) envelope.bodyIn;
                if (response == null) {

                    return "No Internet Connection";

                } else {

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("updateCourseStateResult");
                    return result.toString();
                }

            }*/

            final String baseUrl = "http://www.youni.co.in/courses/oncompletecourses2.php";
            final String QUERY_playlistid = "playlistid";
            final String QUERY_EMAIL = "email";

            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_playlistid, YOUTUBE_PLAYLIST).
                    appendQueryParameter(QUERY_EMAIL, userName).build();


            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                if (is == null)
                    return "Network Problem";

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");


                }


                if (sb.length() == 0)
                    return "Network Problem";


                res = getCompletedCourseFlagJsonData(sb.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (request != null)
                    request.disconnect();
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return res;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Network Problem")) {

                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

            } else if (s.equals("1")) {

               // Toast.makeText(MainActivity.this, s + " Check your resume after watching this video", Toast.LENGTH_SHORT).show();
                Dialog courseCompleteDialog = new Dialog(MainActivity.this);
                courseCompleteDialog.setTitle("Course Completed");
                courseCompleteDialog.setCancelable(false);
                courseCompleteDialog.setContentView(R.layout.course_complete_dialog);
                Button gotoResume = (Button) courseCompleteDialog.findViewById(R.id.goToResume);
                courseCompleteDialog.show();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                courseCompleteDialog.getWindow().setLayout(width,500);
                gotoResume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ResumeIntent = new Intent(MainActivity.this,Profile.class);
                        startActivity(ResumeIntent);

                    }
                });



            } else if (s.equals("0")) {
               Dialog rejectionDialog = new Dialog(MainActivity.this);
                rejectionDialog.setTitle("Certification Rejected");
                rejectionDialog.setContentView(R.layout.certificate_rejected_dialog);
                rejectionDialog.setCancelable(false);
                Button goHome = (Button) rejectionDialog.findViewById(R.id.goToCourses);
                rejectionDialog.show();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                rejectionDialog.getWindow().setLayout(width,500);
                goHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent homeIntent = new Intent(MainActivity.this,Courses.class);
                        startActivity(homeIntent);


                    }
                });
            }else {

                Toast.makeText(MainActivity.this,"Network Problem",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String getCompletedCourseFlagJsonData(String jsonString) {

        Log.i("Reject Message",jsonString);
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("course_state");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {



        }

        @Override
        public void onLoaded(String s) {


            timeRemaining = 0;
            videoIdForTimeCalc = s;


            for (Video v : mPlaylist) {

                String id = v.getId();
                if (id.equals(s)) {

                    loadedVideoId = v.getSnippet().getTitle();

                }


            }


            //  new InsertWacthedVideos().execute("");


        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {


        }

        @Override
        public void onVideoEnded() {


         /*   videoIds.add(loadedVideoId);
            wAdapter = new WatchedAdapter(MainActivity.this, videoIds);
            rv2.setAdapter(wAdapter);
            wAdapter.notifyDataSetChanged();*/


            // Toast.makeText(getApplicationContext(),"Video Ended",Toast.LENGTH_SHORT).show();


            // Toast.makeText(getApplicationContext(), "" + player.getCurrentTimeMillis(), Toast.LENGTH_SHORT).show();
            timeRemaining = 0;
            timeSpentTillNow = timeSpentTillNow + Long.parseLong(tv3.getText().toString());
           // Toast.makeText(getApplicationContext(),"" + timeSpentTillNow,Toast.LENGTH_SHORT).show();
            tv3.setText("0");
            new InsertWacthedVideos().execute("");


        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };


    private long millisInFuture;
    private long timeLeft;
    private long durationInMillis;
    YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

            isPaused = false;
            isCanceled = false;

            CountDownTimer timer;
            if (timeRemaining == 0) {
                 if (player == null) {
                  //   Toast.makeText(getApplicationContext(),"" + alternativeTimeDuration,Toast.LENGTH_SHORT).show();
                     durationInMillis = alternativeTimeDuration;
                     millisInFuture = durationInMillis;
                 }else {
                     alternativeTimeDuration = player.getDurationMillis();
                    // Toast.makeText(getApplicationContext(),"" + alternativeTimeDuration, Toast.LENGTH_SHORT).show();
                     durationInMillis = player.getDurationMillis();
                     millisInFuture = durationInMillis;
                 }
            } else {

                millisInFuture = timeRemaining;
            }
            long countDownInterval = 1000;

            timer = new CountDownTimer(millisInFuture, countDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {


                    if (isPaused || isCanceled) {

                        cancel();
                    } else {

                            if (player != null) {
                                timeLeft = player.getDurationMillis() - millisUntilFinished;

                            }else {
                                timeLeft = durationInMillis - millisUntilFinished;

                            }
                            long timeLeftInSeconds = timeLeft / 1000;
                            //long timeLeftInMinutes  = timeLeft/ (1000 * 60);
                            if (timeLeftInSeconds >= 0) {
                                totalTimeSpent = timeLeftInSeconds;
                                tv3.setText("" + totalTimeSpent);
                            }
                            timeRemaining = millisUntilFinished;

                    }

                }

                @Override
                public void onFinish() {

                }
            }.start();


        }

        @Override
        public void onPaused() {

            isPaused = true;


        }

        @Override
        public void onStopped() {

            // Toast.makeText(getApplicationContext(),"Stopped",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onBuffering(boolean b) {


            if (b) {
                isPaused = true;
            } else {
                isPaused = false;
                onPlaying();
            }

        }

        @Override
        public void onSeekTo(int i) {


        }
    };


    private class getWatchedVideos extends AsyncTask<String, String, String> {


        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {

          /*  SoapObject request = new SoapObject(NAMESPACE1, METHOD1);
            request.addProperty("playList", YOUTUBE_PLAYLIST);
            request.addProperty("useremail", userName);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL1);
            try {
                transport.call(ACTION1, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            if (envelope.bodyIn instanceof SoapFault) {

                String str = ((SoapFault) envelope.bodyIn).faultstring;
                return str;
            } else {

                SoapObject response = (SoapObject) envelope.bodyIn;
                if (response == null) {

                    return "No Internet Available";


                } else {


                    SoapPrimitive result = (SoapPrimitive) response.getProperty("getWatchedvideosResult");
                    return result.toString();
                }


            }*/


            final String baseUrl = "http://www.youni.co.in/courses/watchedvideos.php";
            final String QUERY_PLAYLISTID = "playlistid";
            final String QUERY_EMAIL = "email";

            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_PLAYLISTID, YOUTUBE_PLAYLIST).
                    appendQueryParameter(QUERY_EMAIL, userName).build();

            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                if (is == null)
                    return "Network Problem";

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";


                while ((line = reader.readLine()) != null) {


                    sb.append(line + "\n");
                }


                if (sb.length() == 0)
                    return "Network Problem";

                res = getWatchedDataFromJson(sb.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (request != null)
                    request.disconnect();
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            return res;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Network Problem")) {

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            } else {


                ArrayList<String> allDetails = new ArrayList<>(Arrays.asList(s.split("<")));
                if (allDetails.get(0).equals("No Data")) {
                    String totalVideos = allDetails.get(1);
                    tv2.setText(totalVideos);

                    //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    tv1.setVisibility(View.VISIBLE);
                    rv2.setVisibility(View.INVISIBLE);
                } else {

                    String watchedVideos = allDetails.get(0);
                    String number_of_videos = allDetails.get(1);

                    ArrayList<String> watchedVideosList = new ArrayList<>(Arrays.asList(watchedVideos.split(">")));

                    if (allDetails.isEmpty()) {

                        tv1.setVisibility(View.VISIBLE);
                        rv2.setVisibility(View.INVISIBLE);

                    } else {

                        tv2.setText(number_of_videos);


                        if (watchedVideosList.size() == Integer.parseInt(number_of_videos)) {

                            tv1.setVisibility(View.INVISIBLE);
                            rv2.setVisibility(View.VISIBLE);
                            wAdapter = new WatchedAdapter(MainActivity.this, watchedVideosList);
                            rv2.setAdapter(wAdapter);
                            wAdapter.notifyDataSetChanged();
                            onPause();
                            new putCoureCompleteFlag().execute("");
                        } else {

                            tv1.setVisibility(View.INVISIBLE);
                            rv2.setVisibility(View.VISIBLE);
                            wAdapter = new WatchedAdapter(MainActivity.this, watchedVideosList);
                            rv2.setAdapter(wAdapter);
                            wAdapter.notifyDataSetChanged();
                        }

                    }

                }
            }

        }
    }

    public String getWatchedDataFromJson(String jsonString) {

        Log.i("result", jsonString);
        String result = "";
        String videoTitle = "";
        String videosNumber = "";
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i == 0) {

                    videoTitle = jsonObject.getString("video_name");
                    if (videoTitle.equals("404")) {
                        videosNumber = jsonObject.getString("video_no");
                        result = "No Data" + "<" + videosNumber;
                        return result;

                    } else {

                        //  videoTitle = videoTitle + jsonObject.getString("video_name");
                        videosNumber = jsonObject.getString("video_no");

                    }


                } else {

                    videoTitle = videoTitle + ">" + jsonObject.getString("video_name");


                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        result = videoTitle + "<" + videosNumber;
        Log.i("videoTitle", result);
        return result;


    }


    private class InsertWacthedVideos extends AsyncTask<String, String, String> {


        HttpURLConnection request = null;
        BufferedReader reader = null;
        InputStream is = null;
        String res = "";

        @Override
        protected String doInBackground(String... params) {


         /*   SoapObject request = new SoapObject(NAMESPACE2, METHOD2);
            request.addProperty("playList", YOUTUBE_PLAYLIST);
            request.addProperty("videoTitle", loadedVideoId);
            request.addProperty("useremail", userName);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL2);
            try {
                transport.call(ACTION2, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            if (envelope.bodyIn instanceof SoapFault) {

                String str = ((SoapFault) envelope.bodyIn).faultstring;
                return str;

            } else {

                SoapObject response = (SoapObject) envelope.bodyIn;
                if (response == null) {

                    return "Internet not Available";

                } else {


                    SoapPrimitive result = (SoapPrimitive) response.getProperty("InsertWatchedVideosResult");
                    return result.toString();
                }


            }*/


            final String baseUrl = "http://www.youni.co.in/courses/insertwatchedvideos.php";
            final String QUERY_PLAYLISTID = "playlistid";
            final String QUERY_VIDEOTITLE = "video_title";
            final String QUERY_EMAIL = "email";


            Uri baseUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(QUERY_PLAYLISTID, YOUTUBE_PLAYLIST).
                    appendQueryParameter(QUERY_VIDEOTITLE, loadedVideoId).
                    appendQueryParameter(QUERY_EMAIL, userName).build();

            try {
                java.net.URL myUrl = new URL(baseUri.toString());
                request = (HttpURLConnection) myUrl.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                is = request.getInputStream();
                if (is == null)
                    return "Network Problem";

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {

                    sb.append(line + "\n");
                }


                if (sb.length() == 0)
                    return "Network Problem";


                res = getInsertWatchedDataFromJson(sb.toString());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (request != null)
                    request.disconnect();
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return res;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Network Problem")) {

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            } else if (s.equals("0")) {

                // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
              //  Toast.makeText(getApplicationContext(), "Already Watched", Toast.LENGTH_SHORT).show();


            } else if (s.equals("200")) {

                // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
             //   Toast.makeText(getApplicationContext(), "Successfully Inserted", Toast.LENGTH_SHORT).show();
                new getWatchedVideos().execute("");

            } else if (s.equals("404")) {

                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String getInsertWatchedDataFromJson(String jsonString) {


        String result = "";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }


    /*private class UpdateWatchedVideos extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAMESPACE3, METHOD3);
            request.addProperty("playList", YOUTUBE_PLAYLIST);
            request.addProperty("videoTitle", loadedVideoId);
            request.addProperty("useremail", userName);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE transport = new HttpTransportSE(URL3);
            try {
                transport.call(ACTION3, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            if (envelope.bodyIn instanceof SoapFault) {

                String str = ((SoapFault) envelope.bodyIn).faultstring;
                return str;


            } else {


                SoapObject response = (SoapObject) envelope.bodyIn;
                if (response == null) {

                    return "Internet not Available";


                } else {

                    SoapPrimitive result = (SoapPrimitive) response.getProperty("updateWatchedVideosResult");
                    return result.toString();
                }


            }


        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Internet not Available")) {

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            } else {

                if (s.equals("Successfully Updated")) {

                    new getWatchedVideos().execute("");


                } else {

                    // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }


            }
        }

    }*/
}



