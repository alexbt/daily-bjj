package com.alexbt.bjj.dailybjj.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceHelper;

import org.apache.log4j.Logger;

import java.time.LocalDateTime;

public class ScreenOnReceiver extends BroadcastReceiver {
    private final Logger LOG = Logger.getLogger(ScreenOnReceiver.class);

    @Override
    public void onReceive(final Context context, Intent intent) {
        LOG.info("Entering 'onReceive'");
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE);
        LocalDateTime lastNotification = PreferenceHelper.getLastNotification(sharedPreferences);

        LocalDateTime now = DateHelper.getNow();
        if (lastNotification.plusDays(1).isBefore(DateHelper.getNow())) {
            LOG.warn(String.format("Re-scheduling with lastNotification: %s, now: %s", lastNotification, now));
            NotificationHelper.startServiceToScheduleOnBoot(context);
        }
        LOG.info("Exiting 'onReceive'");
    }
}