package com.fries.edoo.app;

import android.content.Context;

import com.squareup.picasso.Picasso;

/**
 * Created by TooNies1810 on 1/15/16.
 */
public class AppManager {
    private static AppManager _sharePointer = null;

    private Context mainContext;

    private Picasso avaResize;

    private AppManager(){}

    public static AppManager getInstance(){
        if(_sharePointer == null){
            _sharePointer = new AppManager();
        }
        return _sharePointer;
    }

    public Context getMainContext() {
        return mainContext;
    }

    public void setMainContext(Context mainContext) {
        this.mainContext = mainContext;
    }
}
