package com.alexbt.bjj.dailybjj.util;

import java.time.LocalDate;
import java.util.Calendar;

public class DateHelper {
    private static final int MINUTES_ONE_HOUR = 60;
    private static final int MINUTES_ONE_DAY = 24 * 60;

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate getLastWeek() {
        return getToday().minusDays(7);
    }

    public static LocalDate getNextWeek() {
        return getToday().plusDays(7);
    }

    public static LocalDate getLastWeekPlusDays(int page) {
        return getLastWeek().plusDays(page);
    }

    public static Calendar getNextNotificationCalendar(int minutesFromMidnight) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        date.add(Calendar.MINUTE, minutesFromMidnight);
        //if (Calendar.getInstance().getTimeInMillis() > date.getTimeInMillis()) {
        //    date.add(Calendar.DAY_OF_MONTH, 1);
        //}
        return date;
    }
}
