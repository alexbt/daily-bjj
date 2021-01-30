package com.alexbt.bjj.dailybjj.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceHelper;
import com.google.common.io.ByteStreams;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener, TimePickerDialog.OnTimeSetListener {

    private final Logger LOG = Logger.getLogger(SettingsFragment.class);

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        PreferenceHelper.initSharedPreference(preferenceManager);

        setPreferencesFromResource(R.xml.settings, rootKey);

        SharedPreferences sharedPreferences = preferenceManager.getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Preference button = findPreference("sendLogsByEmail");
        button.setOnPreferenceClickListener(preference -> {
            try {
                sendEmail(getContext(), sharedPreferences);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Issue Sending email: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return true;
        });

        updateLastNotification();
        updateNextNotificationField();
        updateScheduledNotificationField();
    }

    private void sendEmail(Context context, SharedPreferences sharedPreferences) throws IOException {
        String scheduledText = PreferenceHelper.getScheduledNotificationText(sharedPreferences);
        int scheduledHours = PreferenceHelper.getScheduledNotificationHours(sharedPreferences);
        int scheduleMinutes = PreferenceHelper.getScheduledNotificationMinutes(sharedPreferences);
        String lastNotificationText = PreferenceHelper.getLastNotificationText(sharedPreferences);
        LocalDateTime lastNotification = PreferenceHelper.getLastNotification(sharedPreferences);
        String nextNotificationText = PreferenceHelper.getNextNotificationText(sharedPreferences);
        LocalDateTime nextNotification = PreferenceHelper.getNextNotification(sharedPreferences);
        LocalDateTime lastTimeAlarmUpdated = PreferenceHelper.getLastTimeAlarmUpdated(sharedPreferences);
        String buildVersion = getContext().getResources().getString(R.string.build_version);
        LOG.info(String.format("Sending email with logs for buildVersion=%s, scheduledNotificationText=%s, lastNotificationText=%s, nextNotification=%s",
                buildVersion, scheduledText, lastNotificationText, nextNotification));
        File logFile = new File(FileSystemHelper.getCacheDir(getContext()) + "/dailybjj.log");
        String logContent = new String(ByteStreams.toByteArray(new FileInputStream(logFile)));
        File tempFile = File.createTempFile(String.format("DailyBJJ-%s_", buildVersion), ".log", getContext().getExternalCacheDir());
        FileWriter fw = new FileWriter(tempFile);
        fw.write(logContent);
        fw.flush();
        fw.close();

        Uri logFileUri = Uri.fromFile(tempFile);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alex.belisleturcot+dailybjj@gmail.com"});
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "DailyBJJ Logs");
        intent.putExtra(Intent.EXTRA_STREAM, logFileUri);
        intent.putExtra(Intent.EXTRA_TEXT, String.format("Hi Alex,"
                        + "\n\nPlease find attached my DailyBJJ logs!\n"

                        + "\nscheduledText: %s\nscheduledHours: %s\nscheduledMinutes: %s"
                        + "\nlastNotification: %s\nlastNotificationText: %s"
                        + "\nnextNotification: %s\nnextNotificationText: %s"
                        + "\nlastDayAlarmUpdated: %s"
                        + "\nDateHelper.getNow(): %s\nDateHelper.getNowWithBuffer(): %s"
                        + "\nBuildVersion: %s"

                        + "\n\n\nLogContent:\n%s"
                        + "\n\nRegards,",
                scheduledText, scheduledHours, scheduleMinutes,
                lastNotification, lastNotificationText,
                nextNotification, nextNotificationText,
                lastTimeAlarmUpdated,
                DateHelper.getNow(), DateHelper.getNowWithBuffer(),
                buildVersion,
                logContent
        ));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }

    private void sendSimpleEmail(Context context, SharedPreferences sharedPreferences) {
        try {
            File logFile = new File(FileSystemHelper.getCacheDir(getContext()) + "/dailybjj.log");
            String logContent = new String(ByteStreams.toByteArray(new FileInputStream(logFile)));
            File tempFile = File.createTempFile(String.format("DailyBJJ-tmp"), ".log", getContext().getExternalCacheDir());
            FileWriter fw = new FileWriter(tempFile);
            fw.write(logContent);

            fw.flush();
            fw.close();

            Uri logFileUri = Uri.fromFile(tempFile);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alex.belisleturcot@gmail.com"});
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "DailyBJJ Logs");
            intent.putExtra(Intent.EXTRA_STREAM, logFileUri);
            intent.putExtra(Intent.EXTRA_TEXT, String.format("%s", logContent));
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(context, "issue Sending email " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void updateScheduledNotificationField() {
        String message = PreferenceHelper.getScheduledNotificationText(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().findPreference("scheduled_notification_time").setSummary(message);
    }

    private void updateNextNotificationField() {
        String message = PreferenceHelper.getNextNotificationText(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().findPreference("next_notification_time").setSummary(message);
    }

    private void updateLastNotification() {
        String message = PreferenceHelper.getLastNotificationText(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().findPreference("last_notification_time").setSummary(message);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference.getKey().equals("scheduled_notification_time")) {
            ((CustomTimePreference) preference).onDisplayPreferenceDialog(this, this, getFragmentManager(), getTag());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals("scheduled_notification_time")) {
            try {
                NotificationHelper.startServiceToSchedule(preference.getContext());
            } catch (Exception e) {
                LOG.error("Unexpected error", e);
            }
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("scheduled_notification_hours") || key.equals("scheduled_notification_minutes")) {
            updateScheduledNotificationField();
            updateNextNotificationField();
        } else if (key.equals("last_notification_time")) {
            updateLastNotification();
            updateNextNotificationField();
        } else if (key.equals("next_notification_time")) {
            updateNextNotificationField();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        try {
            PreferenceHelper.scheduleNotification(getPreferenceManager().getSharedPreferences(), hours, minutes);
            NotificationHelper.startServiceToSchedule(getContext());
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }
}