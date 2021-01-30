package com.alexbt.bjj.dailybjj.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

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

    public static void saveNotificationTime(Context context, int hour, int minutes) {
        getSharedPreference(context).edit()
                .putInt("scheduled_notification_hours", hour)
                .putInt("scheduled_notification_minutes", minutes)
                .apply();
    }

    public static String getNextNotificationText(SharedPreferences sharedPreferences) {
        LocalDateTime nextNotificationTime = getNextNotification(sharedPreferences);
        LocalDate nextNotificationDay = nextNotificationTime.toLocalDate();

        String day;
        LocalDate today = DateHelper.getNow().toLocalDate();
        if (today.equals(nextNotificationDay)) {
            day = "Today";
        } else if (today.plusDays(1).equals(nextNotificationDay)) {
            day = "Tomorrow";
        } else {
            LOG.warn(String.format("Unexpected with nextNotification=%s", nextNotificationTime));
            return String.format("At %s", formatTime(nextNotificationTime.getHour(), nextNotificationTime.getMinute()));
        }

        return String.format("%s at %s", day, formatTime(nextNotificationTime.getHour(), nextNotificationTime.getMinute()));
    }

    public static LocalDateTime getNextNotification(SharedPreferences sharedPreferences) {
        int hours = PreferenceHelper.getScheduledNotificationHours(sharedPreferences);
        int minutes = PreferenceHelper.getScheduledNotificationMinutes(sharedPreferences);
        LocalDateTime now = DateHelper.getNow().withHour(hours).withMinute(minutes);
        if (now.isBefore(DateHelper.getNow())) {
            now = now.plusDays(1);
        }
        return now;
    }

    public static String getLastNotificationText(SharedPreferences sharedPreferences) {
        LocalDateTime lastNotification = PreferenceHelper.getLastNotification(sharedPreferences);
        if (lastNotification == null) {
            return "Never";
        }

        LocalDate lastNotificationDay = lastNotification.toLocalDate();
        LocalDate today = DateHelper.getNow().toLocalDate();
        String message;
        if (lastNotificationDay.equals(today)) {
            message = "Today";
        } else if (lastNotification.plusDays(1).equals(today)) {
            message = "Yesterday";
        } else {
            return "Few days ago";
        }

        return String.format("%s at %s", message, formatTime(lastNotification.getHour(), lastNotification.getMinute()));
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
        sharedPreferences.edit()
                .putInt("scheduled_notification_hours", hours)
                .putInt("scheduled_notification_minutes", minutes)
                .apply();
    }

    public static LocalDateTime getLastTimeAlarmUpdated(SharedPreferences sharedPreferences) {
        String lastDaySetScheduleStr = sharedPreferences.getString("last_time_alarm_updated", null);
        if (lastDaySetScheduleStr != null) {
            return LocalDateTime.parse(lastDaySetScheduleStr);
        }
        return LocalDateTime.now().minusDays(1);
    }

    public static void touchLastTimeAlarmUpdated(SharedPreferences sharedPreferences) {
        sharedPreferences.edit()
                .putString("last_time_alarm_updated", LocalDateTime.now().toString())
                .apply();
    }

    public static void initSharedPreference(PreferenceManager preferenceManager) {
        preferenceManager.setSharedPreferencesName("com.alexbt.DailyNotificationPreference");
    }

    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE);
    }

    public static void saveLastNotificationTime(Context context, LocalDateTime now) {
        getSharedPreference(context).edit()
                .putString("last_notification_time", now.toString())
                .apply();

    }
}
