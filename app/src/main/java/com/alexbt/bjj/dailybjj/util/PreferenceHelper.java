package com.alexbt.bjj.dailybjj.util;

import android.content.SharedPreferences;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PreferenceHelper {
    private static final Logger LOG = Logger.getLogger(PreferenceHelper.class);


    public static int getScheduledNotificationHours(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("scheduled_notification_hours", 8);
    }

    public static LocalDateTime getLastNotification(SharedPreferences sharedPreferences) {
        String lastNotificationTimeStr = sharedPreferences.getString("last_notification_time", null);
        if (lastNotificationTimeStr != null) {
            return LocalDateTime.parse(lastNotificationTimeStr);
        }
        return null;
    }

    public static int getScheduledNotificationMinutes(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("scheduled_notification_minutes", 0);
    }

    public static void saveNotificationTime(SharedPreferences sharedPreferences, int hour, int minutes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("scheduled_notification_hours", hour);
        editor.putInt("scheduled_notification_minutes", minutes);
        editor.apply();
    }

    public static String getNextNotificationText(SharedPreferences sharedPreferences) {
        int hours = PreferenceHelper.getScheduledNotificationHours(sharedPreferences);
        int minutes = PreferenceHelper.getScheduledNotificationMinutes(sharedPreferences);
        LocalDateTime lastNotification = PreferenceHelper.getLastNotification(sharedPreferences);

        String day = "Today";
        if ((lastNotification != null
                && lastNotification.getDayOfYear() == LocalDate.now().getDayOfYear())
                || DateHelper.getNowWithBuffer().isAfter(DateHelper.getNow().withHour(hours).withMinute(minutes))) {
            day = "Tomorrow";
        }

        return String.format("%s at %s", day, formatTime(hours, minutes));
    }

    public static String getLastNotificationText(SharedPreferences sharedPreferences) {
        LocalDateTime lastNotification = PreferenceHelper.getLastNotification(sharedPreferences);
        if (lastNotification == null) {
            return "Never";
        }

        String day;
        int nbDays = LocalDate.now().getDayOfYear() - lastNotification.getDayOfYear();
        if (lastNotification.getDayOfYear() == LocalDate.now().getDayOfYear()) {
            day = "Today";
        } else if (nbDays == 1) {
            day = "Yesterday";
        } else {
            day = String.format("%d days ago", nbDays);
        }

        return String.format("%s at %s", day, formatTime(lastNotification.getHour(), lastNotification.getMinute()));
    }

    private static String getAmPm(int hours) {
        return hours <= 11 ? "AM" : "PM";
    }

    public static String getScheduledNotificationText(SharedPreferences sharedPreferences) {
        int hours = PreferenceHelper.getScheduledNotificationHours(sharedPreferences);
        int minutes = PreferenceHelper.getScheduledNotificationMinutes(sharedPreferences);
        return formatTime(hours, minutes);
    }

    public static String formatTime(int hours, int minutes) {
        return String.format("%02dh%02d %s", hours, minutes, getAmPm(hours));
    }

    public static void scheduleNotification(SharedPreferences sharedPreferences, int hours, int minutes) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("scheduled_notification_hours", hours);
        edit.putInt("scheduled_notification_minutes", minutes);
        edit.apply();
    }
}
