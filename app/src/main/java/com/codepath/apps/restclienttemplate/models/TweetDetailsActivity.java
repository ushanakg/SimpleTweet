package com.codepath.apps.restclienttemplate.models;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TweetDetailsActivity extends AppCompatActivity {

    private Tweet tweet;
    private android.widget.ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTweetDetailsBinding binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        Log.d("TweetDetailsActivity", tweet.getBody());

        binding.tvBody.setText(tweet.getBody());
        binding.tvName.setText(tweet.getUser().getName());
        binding.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        //binding.tvRelativeTimeAgo.setText(tweet.getRelativeTimeAgo(context));
        //load profile pic using Glide
        Glide.with(this).load(tweet.getUser().getPublicImageUrl()).transform(new RoundedCorners(90)).into(binding.ivProfileImage);

        //load media
        Glide.with(this).clear(binding.ivMedia0);
        if (tweet.getMedia() != "") {
            Glide.with(this).load(tweet.getMedia()).transform(new RoundedCorners(65)).into(binding.ivMedia0);
        }
    }
}