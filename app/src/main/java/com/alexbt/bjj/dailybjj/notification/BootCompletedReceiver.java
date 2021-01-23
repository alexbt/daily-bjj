package com.alexbt.bjj.dailybjj.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alexbt.bjj.dailybjj.util.NotificationHelper;

import org.apache.log4j.Logger;

public class BootCompletedReceiver extends BroadcastReceiver {
    private final Logger LOG = Logger.getLogger(BootCompletedReceiver.class);

    @Override
    public void onReceive(final Context context, Intent intent) {
        LOG.info("Entering 'onReceive'");
        try {
            NotificationHelper.scheduleNotification(context, false);
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
        LOG.info("Exiting 'onReceive'");
    }
}