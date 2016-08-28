package com.fries.edoo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * Created by TooNies1810 on 8/13/16.
 */
public class CommonVLs {
    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static byte[] getFileDataFromBitmap(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void setBackgroundColorForView(View view, int idColor, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(context.getResources().getColor(idColor, context.getTheme()));
        } else {
            view.setBackgroundColor(context.getResources().getColor(idColor));
        }
    }
}
