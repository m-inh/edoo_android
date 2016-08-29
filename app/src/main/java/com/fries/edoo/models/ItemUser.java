package com.fries.edoo.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tmq on 29/08/2016.
 */
public class ItemUser {
    private static final String TAG = ItemUser.class.getSimpleName();
    private int id, pointCount;
    private String name,
            email,
            code,
            username,
            birthday,
            capability,
            avatar,
            regularClass,
            description,
            favorite;

    public ItemUser(JSONObject data){
        try {
            id = data.getInt("id");
            pointCount = data.getInt("point_count");
            name = data.getString("name");
            email = data.getString("email");
            code = data.getString("code");
            username = data.getString("username");
            birthday = data.getString("birthday");
            capability = data.getString("capability");
            avatar = data.getString("avatar");
            regularClass = data.getString("regular_class");
            description = data.getString("description");
            favorite = data.getString("favorite");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "Get data Fail from JSONObject");
        }
    }

    public boolean isTeacher(){
        return capability.equalsIgnoreCase("teacher");
    }

    // ------------------------------------ Getter -------------------------------------------------
    public int getPointCount() {
        return pointCount;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRegularClass() {
        return regularClass;
    }

    public String getDescription() {
        return description;
    }

    public String getFavorite() {
        return favorite;
    }

    // --------------------------------------- Setter ----------------------------------------------

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
