package com.alexbt.bjj.dailybjj.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.apache.log4j.Logger;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static final int PENDING_INTENT_REQUEST_CODE = 0;
    private final Logger LOG = Logger.getLogger(NotificationReceiver.class);

    @Override
    public void onReceive(final Context context, Intent intent) {
        LOG.info("Entering 'onReceive'");

        try {
            Intent intentService = new Intent(context, NotificationService.class);
            LOG.info(String.format("Build.VERSION.SDK_INT=%d, Build.VERSION_CODES.O=%d",
                    Build.VERSION.SDK_INT, Build.VERSION_CODES.O));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            } else {
                context.startService(intentService);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }

        LOG.info("Exiting 'onReceive'");
    }
}