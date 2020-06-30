package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    TwitterClient client;
    ActivityTimelineBinding timelineBinding;
    List<Tweet> tweetList;
    TweetsAdapter adapter;
    EndlessRecyclerViewScrollListener scrollListener;

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
        // implement swipe refresh capabilities
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        timelineBinding.rvTweets.setLayoutManager(layoutManager);
        timelineBinding.rvTweets.setAdapter(adapter);

        // Add scrollListener to RecyclerView for infinite pagination
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        timelineBinding.rvTweets.addOnScrollListener(scrollListener);

        populateHomeTimeline();
    }

    // Populates the home timeline with the most up to date tweets
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

    // Inflate the options menu at the top on the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Called when an item in the options menu has been clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            //Compose item has been selected
            // Navigate to composition activity
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // When the compose activity finishes, this function will handle the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get tweet from intent
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update recyclerview with new tweet
            tweetList.add(0, tweet);
            adapter.notifyDataSetChanged();
            timelineBinding.rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Loads more tweets as the user scrolls, allowing an infinite timeline
    public void loadMoreData() {
        // Send an API request to get appropriate data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for loadMoreData" + json.toString());

                try {
                    // Construct new models from the JSON response
                    List<Tweet> newTweets = Tweet.fromJsonArray(json.jsonArray);
                    // Append the new tweets to the existing list of tweets + notify the adapter
                    adapter.addAll(newTweets);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure for loadMoreData", throwable);

            }
        }, tweetList.get(tweetList.size() - 1).getId());

    }

}