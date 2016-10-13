package com.uet.fries.edoo.utils;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.uet.fries.edoo.helper.SQLiteHandler;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by tmq on 25/09/2016.
 */

public class Reporter {

    public static void register(Context context) {
//        Fabric.with(context, new Crashlytics());
//
//        SQLiteHandler sqlite = new SQLiteHandler(context);
//
//        HashMap<String, String> user = sqlite.getUserDetails();
//
//        Crashlytics.setUserIdentifier(user.get("mssv"));
//        Crashlytics.setUserEmail(user.get("email"));
//        Crashlytics.setUserName(user.get("name"));
//        Crashlytics.setString("Regular Class", user.get("lop"));
    }

}
