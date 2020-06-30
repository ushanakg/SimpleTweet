package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    ActivityTimelineBinding timelineBinding;
    List<Tweet> tweetList;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create ViewBinding and set the content to view
        timelineBinding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(timelineBinding.getRoot());

        client = TwitterApp.getRestClient(this);

        // Configure the refreshing colors
        timelineBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        timelineBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Timeline refreshed");

                populateHomeTimeline();
            }
        });

        // RecyclerView can be found at: timelineBinding.rvTweets
        // Initiate the list of tweets and adapter
        tweetList = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweetList);
        // Setup recycler view by setting layout manager and adapter
        timelineBinding.rvTweets.setLayoutManager(new LinearLayoutManager(this));
        timelineBinding.rvTweets.setAdapter(adapter);

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());

                // after receiving tweets from get request, update tweetList to populate timeline
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    // Refresh is finished
                    timelineBinding.swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Converting JSONArray to tweetList failed", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure! " + response, throwable);
            }
        });
    }


}