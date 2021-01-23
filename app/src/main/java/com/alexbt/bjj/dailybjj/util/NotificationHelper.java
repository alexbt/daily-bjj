package com.alexbt.bjj.dailybjj.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.alexbt.bjj.dailybjj.notification.NotificationReceiver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class NotificationHelper {

    private static Object MUTEX = new Object();
    private static final int MINUTES_ONE_HOUR = 60;
    private static final int MINUTES_ONE_DAY = 24 * 60;

    private static void showNotification(Context context) {
        new NotificationReceiver().onReceive(context, null);
    }

    public static void scheduleNotification(Context context, boolean showToast) {
        synchronized (MUTEX) {
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now().plusMinutes(2);

            SharedPreferences sharedPreferences = context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE);
            LocalDateTime lastNotificationTime = PreferenceUtil.getLastNotification(sharedPreferences);
            int hours = PreferenceUtil.getScheduledNotificationHours(sharedPreferences);
            int minutes = PreferenceUtil.getScheduledNotificationMinutes(sharedPreferences);

            String toastMessage;
            LocalDateTime notificationTime = LocalDate.now().atStartOfDay().withHour(hours).withMinute(minutes);
            if (isNotificationTimePassed(now, notificationTime) && !isAlreadyNotifiedForToday(today, lastNotificationTime)) {
                NotificationHelper.showNotification(context);
                notificationTime = notificationTime.plusDays(1);
                toastMessage = String.format("Upcoming Daily BJJ in few seconds and next scheduled for tomorrow at %s", PreferenceUtil.formatTime(hours, minutes));

            } else if (isAlreadyNotifiedForToday(today, lastNotificationTime)) {
                notificationTime = notificationTime.plusDays(1);
                toastMessage = String.format("Next Daily BJJ scheduled for tomorrow at %s", PreferenceUtil.formatTime(hours, minutes));
            } else {
                toastMessage = String.format("Next Daily BJJ scheduled today at %s", PreferenceUtil.formatTime(hours, minutes));
            }
            if (showToast) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), AlarmManager.INTERVAL_DAY, pending);
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
