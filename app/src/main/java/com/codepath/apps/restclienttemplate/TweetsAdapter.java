package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

// Adapter binds tweets to ViewHolder instances in the RecylerView to display a list of tweets
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Activity context;
    List<Tweet> tweetList;

    // Pass in the context and list of tweets
    public TweetsAdapter(Activity context, List<Tweet> tweetList) {
        this.context = context;
        this.tweetList = tweetList;
    }

    // For each row, inflate a layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTweetBinding binding = ItemTweetBinding.inflate(context.getLayoutInflater(), parent, false);
        View tweetView = binding.getRoot();

        return new ViewHolder(tweetView);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    // Bind values of a tweet to a ViewHolder based on the tweet's position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the tweet at position
        Tweet tweet = tweetList.get(position);

        // Bind the tweet to the ViewHolder
        holder.bind(tweet);
    }

    // Clean all elements of the recycler
    public void clear() {
        tweetList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items to our dataset
    public void addAll(List<Tweet> tweets) {
        tweetList.addAll(tweets);
        notifyDataSetChanged();
    }

    // Defines a viewholder which binds a tweet's info to its layout
    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemTweetBinding tweetView;

        public ViewHolder(@NonNull View itemView) { //itemView is a representation of one row in the RecyclerView
            super(itemView);

            tweetView = ItemTweetBinding.bind(itemView);
        }

        public void bind(Tweet tweet) {
            tweetView.tvBody.setText(tweet.getBody());
            tweetView.tvScreenName.setText(tweet.getUser().getScreenName());
            tweetView.tvRelativeTimeAgo.setText(tweet.getRelativeTimeAgo(context));
            //load profile pic using Glide
            Glide.with(context).load(tweet.getUser().getPublicImageUrl()).transform(new RoundedCorners(90)).into(tweetView.ivProfileImage);

            //load media
            Glide.with(context).clear(tweetView.ivMedia);
            List<String> tweetMedia = tweet.getMedia();
            if (tweetMedia.size() > 0) {
                Glide.with(context).load(tweet.getMedia().get(0)).transform(new RoundedCorners(65)).into(tweetView.ivMedia);
            }
        }
    }

}
