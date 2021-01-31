package com.alexbt.bjj.dailybjj.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.notification.NotificationService;
import com.alexbt.bjj.dailybjj.notification.SchedulingService;

import org.apache.log4j.Logger;

public class NotificationHelper {
    private static final Logger LOG = Logger.getLogger(NotificationHelper.class);

    public static void startServiceToNotify(Context context) {
        try {
            Intent intentService = new Intent(context, NotificationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            } else {
                context.startService(intentService);
                //context.startForegroundService(intentService);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }

    public static void startServiceToSchedule(Context context) {
            try {
                Intent intentService = new Intent(context, SchedulingService.class);
                LOG.info(String.format("Build.VERSION.SDK_INT=%d, Build.VERSION_CODES.O=%d",
                        Build.VERSION.SDK_INT, Build.VERSION_CODES.O));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intentService);
                } else {
                    context.startService(intentService);
                    //context.startForegroundService(intentService);
                }
            } catch (Exception e) {
                LOG.error("Unexpected error", e);
            }
    }

    public static String createNotificationChannel(Context context) {
        LOG.info("Entering 'createNotificationChannel'");

        String channelId = null;
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = context.getResources().getString(R.string.app_name);
            // The id of the channel.
            channelId = appName + "_ChannelId";

            // The user-visible name of the channel.
            // The user-visible description of the channel.
            String channelDescription = appName + " Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
            //            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, appName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            //            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        }

        LOG.info(String.format("Exiting 'createNotificationChannel' with channelId=%s", channelId));
        return channelId;
    }

    public static void startServiceToScheduleOnBoot(Context context) {
            try {
                Intent intentService = new Intent(context, SchedulingService.class);
                intentService.putExtra("isOnBoot", true);
                LOG.info(String.format("Build.VERSION.SDK_INT=%d, Build.VERSION_CODES.O=%d",
                        Build.VERSION.SDK_INT, Build.VERSION_CODES.O));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intentService);
                } else {
                    context.startService(intentService);
                    //context.startForegroundService(intentService);
                }
            } catch (Exception e) {
                LOG.error("Unexpected error", e);
            }
    }
}
