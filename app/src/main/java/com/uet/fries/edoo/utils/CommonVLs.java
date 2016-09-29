package com.uet.fries.edoo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by TooNies1810 on 8/13/16.
 */
public class CommonVLs {
    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String convertDate(String date) {
        String format = CommonVLs.TIME_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return DateFormat.format("dd/MM/yy", sdf.parse(date).getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static byte[] getFileDataFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void setBackgroundColorForView(View view, int idColor, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(context.getResources().getColor(idColor, context.getTheme()));
        } else {
            view.setBackgroundColor(context.getResources().getColor(idColor));
        }
    }

    public static int getColor(int idColor, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(idColor, context.getTheme());
        } else {
            return context.getResources().getColor(idColor);
        }
    }

    // get time
    public static String getDateTime(String timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String date = DateFormat.format("hh:mm, dd-MM-yyyy", cal).toString();
        return date;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static boolean isHasStoragePermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    // Camera Permissions
    private static final int REQUEST_CAMERA = 2;
    private static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CAMERA,
                    REQUEST_CAMERA
            );
        }
    }

    public static boolean isHasCameraPermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


    // Internet state Permissions
    private static final int REQUEST_INTERNET_STATE = 3;
    private static final String[] PERMISSIONS_INTERNET_STATE = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyInternetStatePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_INTERNET_STATE,
                    REQUEST_INTERNET_STATE
            );
        }
    }

    public static boolean isHasNetworkPermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
