package com.codepath.apps.restclienttemplate.models;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

// model that holds the essential information of a User
@Parcel
public class User {

    private String name;
    private String screenName;
    private String publicImageUrl;

    //Empty constructor for the Parceler library
    public User() {

    }

    // Create a java User object from a jsonObject describing said user
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
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
}
