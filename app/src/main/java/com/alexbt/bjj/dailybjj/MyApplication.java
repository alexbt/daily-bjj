package com.alexbt.bjj.dailybjj;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        ConfigureLog4j.configure(context);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
