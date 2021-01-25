package com.alexbt.bjj.dailybjj.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.alexbt.bjj.dailybjj.util.NotificationHelper;

import org.apache.log4j.Logger;

public class BroadcastService extends Service {
    private final Logger LOG = Logger.getLogger(BootCompletedReceiver.class);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.info("Entering 'onStartCommand'");
        try {
            NotificationHelper.scheduleNotification(getApplicationContext(), false);
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
        LOG.info("Exiting 'onStartCommand'");
        return Service.START_STICKY;
    }
}