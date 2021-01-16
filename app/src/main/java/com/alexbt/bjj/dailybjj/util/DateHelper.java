package com.alexbt.bjj.dailybjj.util;

import java.time.LocalDate;
import java.util.Calendar;

public class DateHelper {
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate getLastWeek() {
        return getToday().minusDays(6);
    }

    public static LocalDate getNextWeek() {
        return getToday().plusDays(7);
    }

    public static LocalDate getLastWeekPlusDays(int page) {
        return getLastWeek().plusDays(page);
    }

    public static Calendar getNotificationTime(int hour, int minutes) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minutes);
        if (Calendar.getInstance().getTimeInMillis() > date.getTimeInMillis()) {
            date.add(Calendar.DAY_OF_MONTH, 1);
        }
        return date;
    }
}
