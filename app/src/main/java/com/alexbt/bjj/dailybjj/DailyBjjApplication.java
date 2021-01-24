package com.alexbt.bjj.dailybjj;

import android.app.Application;
import android.content.Context;

public class DailyBjjApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        DailyBjjApplication.context = getApplicationContext();
        ConfigureLog4j.configure(context);
    }

    public static Context getAppContext() {
        return DailyBjjApplication.context;
    }
}
