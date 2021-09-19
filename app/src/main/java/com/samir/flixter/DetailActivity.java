package com.samir.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.samir.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity{

    private static final String YOUTUBE_API_KEY = "AIzaSyBAs7t8wSPCXacbYi_G9Awas8S-UJjQnyY";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "DetailActivity";
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView playerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        playerView = findViewById(R.id.player);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            playerView.getLayoutParams().height = 1000;
        }

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if(results.length() == 0){
                        return;
                    }
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    initializeYoutube(youtubeKey);
                    Log.d(TAG,"Youtube key " + youtubeKey);
                } catch (JSONException e) {
                    Log.e(TAG,"failed to parse jason",e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"ERROR making request "+ response);
            }
        });

    }

    private void initializeYoutube(final String youtubeKey) {
        playerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(youtubeKey);
                if(ratingBar.getRating() >= 5.0){
                    youTubePlayer.loadVideo(youtubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(TAG,"Error initializing YouTube Player");
            }
        });
    }
}