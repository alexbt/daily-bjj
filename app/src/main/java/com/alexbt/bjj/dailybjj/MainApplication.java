package com.alexbt.bjj.dailybjj;

import android.app.Application;
import android.content.Context;

/**
 * Hack to keep a reference to the context..
 * Which, I'm not even using at the moment...
 */
public class MainApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
        ConfigureLog4j.configure(context);
    }

    public static Context getAppContext() {
        return MainApplication.context;
    }
}
