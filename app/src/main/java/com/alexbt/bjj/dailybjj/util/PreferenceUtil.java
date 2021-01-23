package com.alexbt.bjj.dailybjj.util;

import android.content.SharedPreferences;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PreferenceUtil {
    private static final Logger LOG = Logger.getLogger(PreferenceUtil.class);


    public static int getScheduledNotificationHours(SharedPreferences sharedPreferences) {
        LOG.info("Entering 'getScheduledNotificationHours'");
        int hours = sharedPreferences.getInt("scheduled_notification_hours", 8);
        LOG.info("Exiting 'getScheduledNotificationHours'");
        return hours;
    }

    public static LocalDateTime getLastNotification(SharedPreferences sharedPreferences) {
        LOG.info("Entering 'getLastNotification'");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String lastNotificationTimeStr = sharedPreferences.getString("last_notification_time", null);
        if (lastNotificationTimeStr != null) {
            return LocalDateTime.parse(lastNotificationTimeStr);
        }
        return null;
    }

    public static int getScheduledNotificationMinutes(SharedPreferences sharedPreferences) {
        LOG.info("Entering 'getScheduledNotificationMinutes'");
        int minutes = sharedPreferences.getInt("scheduled_notification_minutes", 0);
        return minutes;
    }

    public static void saveNotificationTime(SharedPreferences sharedPreferences, int hour, int minutes) {
        LOG.info("Entering 'saveNotificationTime'");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("scheduled_notification_hours", hour);
        editor.putInt("scheduled_notification_minutes", minutes);
        editor.commit();
    }

    public static String getNextNotificationText(SharedPreferences sharedPreferences) {
        int hours = PreferenceUtil.getScheduledNotificationHours(sharedPreferences);
        int minutes = PreferenceUtil.getScheduledNotificationMinutes(sharedPreferences);
        LocalDateTime lastNotification = PreferenceUtil.getLastNotification(sharedPreferences);

        String day = "Today";
        if ((lastNotification!=null
                && lastNotification.getDayOfYear() == LocalDate.now().getDayOfYear())
                || DateHelper.getNow().isAfter(DateHelper.getNow().withHour(hours).withMinute(minutes))) {
            day = "Tomorrow";
        }

        return String.format("%s at %s", day, formatTime(hours, minutes));
    }

    public static String getLastNotificationText(SharedPreferences sharedPreferences) {
        LocalDateTime lastNotification = PreferenceUtil.getLastNotification(sharedPreferences);
        if(lastNotification==null){
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
        int hours = PreferenceUtil.getScheduledNotificationHours(sharedPreferences);
        int minutes = PreferenceUtil.getScheduledNotificationMinutes(sharedPreferences);
        return formatTime(hours, minutes);
    }

    public static String formatTime(int hours, int minutes) {
        return String.format("%02dh%02d %s", hours, minutes, getAmPm(hours));
    }

}
