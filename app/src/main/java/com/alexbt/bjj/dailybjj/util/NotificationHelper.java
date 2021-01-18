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
import java.util.Calendar;

public class NotificationHelper {

    private static final int MINUTES_ONE_HOUR = 60;
    private static final int MINUTES_ONE_DAY = 24 * 60;

    private static void showNotification(Context context) {
        new NotificationReceiver().onReceive(context, null);
    }

    public static void scheduleNotification(Context context, SharedPreferences sharedPreferences) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int currentMinutes = now.getHour() * MINUTES_ONE_HOUR + now.getMinute();

        String lastNotificationDay = sharedPreferences.getString("last_notification_day", null);
        int minutesFromMidnight = sharedPreferences.getInt("notification_time", 0);

        String hours = String.format("%02d", getHours(minutesFromMidnight));
        String minutes = String.format("%02d", getMinutes(minutesFromMidnight));

        if (isNotificationTimePassed(currentMinutes, minutesFromMidnight) && !isAlreadyNotifiedForToday(today, lastNotificationDay)) {
            NotificationHelper.showNotification(context);
            minutesFromMidnight += MINUTES_ONE_DAY;
            Toast.makeText(context, String.format("Notification now and next tomorrow at %sh%s", hours, minutes), Toast.LENGTH_LONG).show();
        } else if (isAlreadyNotifiedForToday(today, lastNotificationDay)) {
            minutesFromMidnight += MINUTES_ONE_DAY;
            Toast.makeText(context, String.format("Next notification tomorrow at %sh%s", hours, minutes), Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context, String.format("Next notification in %d minutes at %sh%s", minutesFromMidnight, hours, minutes), Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar notificationTime = DateHelper.getNextNotificationCalendar(minutesFromMidnight);


        manager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending);
    }

    private static boolean isAlreadyNotifiedForToday(LocalDate today, String lastNotificationDay) {
        return today.toString().equals(lastNotificationDay);
    }

    private static int getHours(int minutesFromMidnight) {
        return minutesFromMidnight / MINUTES_ONE_HOUR;
    }

    private static int getMinutes(int minutesFromMidnight) {
        return minutesFromMidnight % MINUTES_ONE_HOUR;
    }

    private static boolean isNotificationTimePassed(int currentMinutes, int minutesFromMidnight) {
        return currentMinutes >= (minutesFromMidnight - 2);
    }
}
