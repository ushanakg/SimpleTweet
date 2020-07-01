package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.versionedparcelable.ParcelField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// model that holds all the essential information of a tweet
@Parcel
public class Tweet {

    private String body;
    private String createdAt;
    private User user;
    private long id;
    private List<String> media;

    // Empty constructor for the Parceler library
    public Tweet() {

    }

    // Creates a java Tweet instance from a JSONObject that describes that tweet
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text").split(" https")[0];
        Log.i(Tweet.class.getSimpleName(), "Body: " + tweet.body);
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        tweet.media = new ArrayList<>();

        if (jsonObject.has("extended_entities")) {
            JSONObject entities = jsonObject.getJSONObject("extended_entities");
            JSONArray media = entities.getJSONArray("media");
            for (int i = 0; i < media.length(); i++) {
                tweet.media.add(((JSONObject) media.get(i)).getString("media_url_https"));
            }
        }

        return tweet;
    }

    // Takes in a JSONArray of tweets and returns a list of java Tweet instances of the same tweets
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    // Get how long ago the tweet was posted
    public String getRelativeTimeAgo(Context context) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            if (DateUtils.DAY_IN_MILLIS > System.currentTimeMillis() - dateMillis) {
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
                String[] split = relativeDate.split(" ");

                relativeDate = split[0] + split[1].substring(0,1);
            } else {
                relativeDate = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_ABBREV_MONTH);
                String[] split = relativeDate.split(",");
                relativeDate = split[0];

            }


        } catch (ParseException e) {
            Log.e(Tweet.class.getSimpleName(), "Calculating relative time ago failed", e);
        }

        return relativeDate;
    }

    public long getId() {
        return id;
    }

    public List<String> getMedia() {
        return media;
    }
}
