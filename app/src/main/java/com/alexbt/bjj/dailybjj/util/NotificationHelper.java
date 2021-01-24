package com.alexbt.bjj.dailybjj.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.alexbt.bjj.dailybjj.notification.NotificationReceiver;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NotificationHelper {
    private static final Logger LOG = Logger.getLogger(NotificationHelper.class);

    private static final Object MUTEX = new Object();
    private static final int MINUTES_ONE_HOUR = 60;

    public static void scheduleNotification(Context context, boolean showToast) {
        synchronized (MUTEX) {
            LocalDate today = DateHelper.getToday();
            LocalDateTime now = DateHelper.getNowWithBuffer();

            SharedPreferences sharedPreferences = context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE);
            LocalDateTime lastNotificationTime = PreferenceHelper.getLastNotification(sharedPreferences);
            int hours = PreferenceHelper.getScheduledNotificationHours(sharedPreferences);
            int minutes = PreferenceHelper.getScheduledNotificationMinutes(sharedPreferences);

            String toastMessage;
            LocalDateTime notificationTime = LocalDate.now().atStartOfDay().withHour(hours).withMinute(minutes);
            boolean notificationTimePassed = isNotificationTimePassed(now, notificationTime);
            boolean alreadyNotifiedForToday = isAlreadyNotifiedForToday(today, lastNotificationTime);
            LOG.info(String.format("Scheduling notification for notificationTime=%s, notificationTimePassed=%s, alreadyNotifiedForToday=%s",
                    notificationTime, notificationTimePassed, alreadyNotifiedForToday));

            if (notificationTimePassed && !alreadyNotifiedForToday) {
                LOG.info("Schedule time is in the past, showing notification now and scheduling for tomorrow");
                notificationTime = notificationTime.plusDays(1);
                toastMessage = String.format("Upcoming Daily BJJ in few seconds and next scheduled for tomorrow at %s", PreferenceHelper.formatTime(hours, minutes));

            } else if (alreadyNotifiedForToday) {
                LOG.info("Notification was already sent to user, scheduling for tomorrow");
                notificationTime = notificationTime.plusDays(1);
                toastMessage = String.format("Next Daily BJJ scheduled for tomorrow at %s", PreferenceHelper.formatTime(hours, minutes));
            } else {
                LOG.info("Schedule time is in the future, scheduling for later today");
                toastMessage = String.format("Next Daily BJJ scheduled today at %s", PreferenceHelper.formatTime(hours, minutes));
            }
            if (showToast) {
                LOG.info(String.format("Showing toast=%s", toastMessage));
                Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            ZonedDateTime zonedDateTime = notificationTime.atZone(ZoneId.systemDefault());
            LOG.info(String.format("Scheduling notification for notificationTime=%s, zonedDateTime=%s", notificationTime, zonedDateTime));
            manager.setRepeating(AlarmManager.RTC_WAKEUP, zonedDateTime.toInstant().toEpochMilli(), AlarmManager.INTERVAL_DAY, pending);
        }
    }

    private static boolean isAlreadyNotifiedForToday(LocalDate today, LocalDateTime lastNotificationDay) {
        return today.equals(lastNotificationDay != null ? lastNotificationDay.toLocalDate() : null);
    }

    private static int getHours(int minutesFromMidnight) {
        return minutesFromMidnight / MINUTES_ONE_HOUR;
    }

    private static int getMinutes(int minutesFromMidnight) {
        return minutesFromMidnight % MINUTES_ONE_HOUR;
    }

    private static boolean isNotificationTimePassed(LocalDateTime now, LocalDateTime notificationTime) {
        return now.isAfter(notificationTime);
    }
}
