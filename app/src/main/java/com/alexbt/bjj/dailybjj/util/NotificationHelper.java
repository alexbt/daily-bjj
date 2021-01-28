package com.alexbt.bjj.dailybjj.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.alexbt.bjj.dailybjj.notification.SchedulingService;
import com.alexbt.bjj.dailybjj.notification.NotificationService;

import org.apache.log4j.Logger;

public class NotificationHelper {
    private static final Logger LOG = Logger.getLogger(NotificationHelper.class);

    public static void startServiceToNotify(Context context) {
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
            }
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
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
            }
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }
}
