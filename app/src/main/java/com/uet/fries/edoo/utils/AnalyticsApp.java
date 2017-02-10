package com.uet.fries.edoo.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by tmq on 10/02/2017.
 */

public class AnalyticsApp {

    private static final String TAG = AnalyticsApp.class.getSimpleName();

    public void sendEventWritePost(Context context){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "note"); // or notification, question, poll
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "write_post");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.i(TAG, "Event write_post is sent");
    }

    public void sendEventCommentPost(Context context){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "comment");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "comment_post");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.i(TAG, "Event comment_post is sent");
    }

    public void sendEventLikePost(Context context){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "like");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "like_post");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.i(TAG, "Event like_post is sent");
    }

    public void sendEventDislikePost(Context context){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "dislike");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "dislike_post");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.i(TAG, "Event dislike_post is sent");
    }

    public void sendEventUploadExercise(Context context){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "upload_exercise");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "upload_exercise");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Log.i(TAG, "Event upload_exercise is sent");
    }
}
