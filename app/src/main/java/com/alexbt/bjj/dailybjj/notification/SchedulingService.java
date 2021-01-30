package com.alexbt.bjj.dailybjj.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceHelper;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SchedulingService extends Service {
    private final Logger LOG = Logger.getLogger(SchedulingService.class);
    private static final Object MUTEX = new Object();

    @Override
    public int onStartCommand(Intent commandIntent, int flags, int startId) {
        boolean isOnBoot = commandIntent.getBooleanExtra("isOnBoot", false);
        LOG.info("Entering 'onStartCommand'");
        try {
            synchronized (MUTEX) {
                LocalDate today = DateHelper.getToday();
                LocalDateTime now = DateHelper.getNowWithBuffer();

                SharedPreferences sharedPreferences = PreferenceHelper.getSharedPreference(getApplicationContext());
                LocalDateTime lastNotificationTime = PreferenceHelper.getLastNotification(sharedPreferences);
                int hours = PreferenceHelper.getScheduledNotificationHours(sharedPreferences);
                int minutes = PreferenceHelper.getScheduledNotificationMinutes(sharedPreferences);

                LocalDateTime notificationTime = LocalDate.now().atStartOfDay().withHour(hours).withMinute(minutes);
                boolean notificationTimePassed = DateHelper.isNotificationTimePassed(now, notificationTime);
                boolean alreadyNotifiedForToday = DateHelper.isAlreadyNotifiedForToday(today, lastNotificationTime);
                LOG.info(String.format("Scheduling notification for notificationTime=%s, notificationTimePassed=%s, alreadyNotifiedForToday=%s",
                        notificationTime, notificationTimePassed, alreadyNotifiedForToday));

                String toastMessage;
                /*if (alreadyNotifiedForToday) {
                    notificationTime = notificationTime.plusDays(1);
                    LOG.info("Notification was already sent to user, scheduling for tomorrow");
                    toastMessage = String.format("Next Daily BJJ scheduled for today at %s", PreferenceHelper.formatTime(hours, minutes));
                } else */
                if (notificationTimePassed) {
                    toastMessage = String.format("Upcoming Daily BJJ in few seconds and next scheduled for tomorrow at %s", PreferenceHelper.formatTime(hours, minutes));
                    //NotificationHelper.startServiceToNotify(getApplicationContext());
                } else {
                    toastMessage = String.format("Next Daily BJJ scheduled today at %s", PreferenceHelper.formatTime(hours, minutes));
                }
                LOG.info(String.format("toast message=%s", toastMessage));

                if (!isOnBoot) {
                    LOG.info(String.format("Showing toast=%s", toastMessage));
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                ZonedDateTime zonedDateTime = notificationTime.atZone(ZoneId.systemDefault());
                LOG.info(String.format("Scheduling notification for notificationTime=%s, zonedDateTime=%s", notificationTime, zonedDateTime));
                manager.setRepeating(AlarmManager.RTC_WAKEUP, zonedDateTime.toInstant().toEpochMilli(), AlarmManager.INTERVAL_DAY, pending);

                PreferenceHelper.touchLastTimeAlarmUpdated(sharedPreferences);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
        LOG.info("Exiting 'onStartCommand'");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LOG.info("Performing 'onBind'");
        return null;
    }
}