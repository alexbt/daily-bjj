package com.alexbt.bjj.dailybjj.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();
    private static final int MINUTES_ONE_HOUR = 60;
    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static int DEFAULT_HOUR = R.integer.default_notification_time_hours;
    private static int DEFAULT_MINUTES = R.integer.default_notification_time_minutes;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "Entering 'onReceive'");
        if (BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences preferences = context.getSharedPreferences("com.alexbt.DailyNotificationPreference", 0);
            NotificationHelper.scheduleNotification(context, preferences);
        }
        Log.i(TAG, "Exiting 'onReceive'");
    }
}