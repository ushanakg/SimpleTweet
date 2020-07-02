package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
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
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet {

    @ColumnInfo
    @PrimaryKey
    private long id;

    @ColumnInfo
    private String body;

    @ColumnInfo
    private String createdAt;

    @ColumnInfo
    private String media = "";

    @ColumnInfo
    private long userId;

    @Ignore
    private User user;

    // Empty constructor for the Parceler library
    public Tweet() {

    }

    // Creates a java Tweet instance from a JSONObject that describes that tweet
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text").split(" https")[0];
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        tweet.userId = tweet.user.getId();

        if (jsonObject.has("extended_entities")) {
            JSONObject entities = jsonObject.getJSONObject("extended_entities");
            JSONArray media = entities.getJSONArray("media");
            if (media.length() > 0) {
                tweet.media = ((JSONObject) media.get(0)).getString("media_url_https");
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

    public String getTimePosted(Context context) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String time = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            time = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_TIME);

        } catch (ParseException e) {
            Log.e(Tweet.class.getSimpleName(), "Calculating time posted failed", e);
        }
        return time;
    }

    public String getDatePosted(Context context) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String date = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            date = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_NUMERIC_DATE);
            String full_date = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_YEAR);
            date = date + "/" + full_date.substring(full_date.length() - 2);

        } catch (ParseException e) {
            Log.e(Tweet.class.getSimpleName(), "Calculating time posted failed", e);
        }
        return date;
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

    public String getMedia() {
        return media;
    }

    public long getUserId() {
        return userId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
