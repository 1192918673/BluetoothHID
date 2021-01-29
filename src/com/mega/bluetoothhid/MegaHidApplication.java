package com.mega.bluetoothhid;

import android.app.Application;
import android.util.Log;

public class MegaHidApplication extends Application {
    private static final String TAG = "MegaHidApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }
}