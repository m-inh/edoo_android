package com.uet.fries.edoo.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TooNies1810 on 11/20/15.
 */

/**
 * Cac bai post binh thuong
 */
public class ItemTimeLinePost extends ITimelineBase implements Serializable {
    private static final String TAG = ItemTimeLinePost.class.getSimpleName();

    private int like;
    private boolean isSolve;
    private boolean isIncognito;

    public ItemTimeLinePost(){
        super();
    }

    public ItemTimeLinePost(JSONObject jsonTimeline) throws JSONException {
        super(jsonTimeline);

        this.like = jsonTimeline.getInt("vote_count");
        this.isIncognito = jsonTimeline.getInt("is_incognito") == 1;
        this.isSolve = jsonTimeline.getInt("is_solve") == 1;

        if (isIncognito) {
            setNameAuthor("Ẩn danh");
        }
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public boolean isSolve() {
        return isSolve;
    }

    public void setSolve(boolean solve) {
        isSolve = solve;
    }

    public boolean isIncognito() {
        return isIncognito;
    }
}
