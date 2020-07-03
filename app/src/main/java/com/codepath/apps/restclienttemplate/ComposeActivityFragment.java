package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivityFragment extends DialogFragment {

    private static final String TAG = ComposeActivityFragment.class.getSimpleName();
    public static final int MAX_TWEET_LENGTH = 280;
    private TextWatcher characterCounter;
    private User user;
    private TwitterClient client;

    private ActivityComposeBinding composeBinding;

    // Empty for DialogFragment
    public ComposeActivityFragment() {

    }

    public static ComposeActivityFragment newInstance(User user) {
        ComposeActivityFragment frag = new ComposeActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        composeBinding = ActivityComposeBinding.inflate(getLayoutInflater(), container, false);
        View view = composeBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.user = Parcels.unwrap(getArguments().getParcelable("user"));

        client = TwitterApp.getRestClient(view.getContext());

        //Set up character counter
        characterCounter = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets textview to the current length
                composeBinding.tvCharacterCount.setText(String.valueOf(s.length()) + "/" + MAX_TWEET_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };

        composeBinding.etCompose.addTextChangedListener(characterCounter);


        //Add a click listener to the button
        composeBinding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tweetContent = composeBinding.etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(view.getContext(), "Sorry, your tweet cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(view.getContext(), "Sorry, your tweet is too long.", Toast.LENGTH_LONG).show();
                    return;
                }
                // Valid tweet, Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publishTweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "published: " + tweet.getBody());

                            // send the contents of the published tweet back to be displayed in the RecyclerView
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publishTweet", throwable);
                    }
                });
            }
        });


        composeBinding.etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
