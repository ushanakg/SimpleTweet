package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

// model that holds the essential information of a User
@Parcel
@Entity
public class User {

    @ColumnInfo
    @PrimaryKey
    private long id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String screenName;

    @ColumnInfo
    private String publicImageUrl;

    //Empty constructor for the Parceler library
    public User() {

    }

    // Create a java User object from a jsonObject describing said user
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).getUser());
        }
        return users;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getPublicImageUrl() {
        return publicImageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setPublicImageUrl(String publicImageUrl) {
        this.publicImageUrl = publicImageUrl;
    }
}
