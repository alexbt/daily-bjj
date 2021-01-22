package com.alexbt.bjj.dailybjj.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alexbt.bjj.dailybjj.util.NotificationHelper;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "Entering 'onReceive'");
        NotificationHelper.scheduleNotification(context, false);
        Log.i(TAG, "Exiting 'onReceive'");
    }
}