package com.alexbt.bjj.dailybjj.settings;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceHelper;
import com.google.common.io.ByteStreams;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener, TimePickerDialog.OnTimeSetListener {

    private final Logger LOG = Logger.getLogger(SettingsFragment.class);

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("com.alexbt.DailyNotificationPreference");

        setPreferencesFromResource(R.xml.prefs, rootKey);

        CustomTimePreference customScheduledNtificationTime = (CustomTimePreference) findPreference("scheduled_notification_time_2");

        // customScheduledNtificationTime.setOnPreferenceClickListener(preference -> {
        //     customScheduledNtificationTime.buildTimePicker(this, getFragmentManager(), getTag());
        //     customScheduledNtificationTime.setOnTimeChangedListener(this);
        //     return true;
        // });

        SharedPreferences sharedPreferences = preferenceManager.getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Preference button = findPreference("sendLogsByEmail");
        button.setOnPreferenceClickListener(preference -> {
            String scheduledNotification = PreferenceHelper.getScheduledNotificationText(sharedPreferences);
            String lastNotification = PreferenceHelper.getLastNotificationText(sharedPreferences);
            String nextNotification = PreferenceHelper.getNextNotificationText(sharedPreferences);
            String buildVersion = getContext().getResources().getString(R.string.build_version);
            LOG.info(String.format("Sending email with logs for buildVersion=%s, scheduledNotification=%s, lastNotification=%s, nextNotification=%s",
                    buildVersion, scheduledNotification, lastNotification, nextNotification));
            try {
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
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alex.belisleturcot@gmail.com"});
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "DailyBJJ Logs");
                intent.putExtra(Intent.EXTRA_STREAM, logFileUri);
                intent.putExtra(Intent.EXTRA_TEXT, String.format("Hi Alex,"
                                + "\n\nPlease find attached my DailyBJJ logs!\n"
                                + "\n%s: %s"
                                + "\n%s: %s"
                                + "\n%s: %s"
                                + "\n\nRegards,",
                        "Scheduled Notification", scheduledNotification,
                        "Last Notification", lastNotification,
                        "Next Notification", nextNotification,
                        getContext().getResources().getString(R.string.build_version_title), buildVersion
                ));
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 1);

            } catch (Exception e) {
                LOG.error("Unexpected error", e);
            }

            return true;
        });


        //preferenceManager.findPreference("scheduled_notification_time").setOnPreferenceChangeListener(this);
        //preferenceManager.findPreference("last_notification_time").setOnPreferenceChangeListener(this);
        //preferenceManager.findPreference("next_notification_time").setOnPreferenceChangeListener(this);

        updateLastNotification();
        updateNextNotificationField();
        updateScheduledNotificationField();
    }

    private void updateScheduledNotificationField() {
        String message = PreferenceHelper.getScheduledNotificationText(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().findPreference("scheduled_notification_time_2").setSummary(message);
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
        if (preference.getKey().equals("scheduled_notification_time_2")) {
            ((CustomTimePreference) preference).onDisplayPreferenceDialog(this, this, getFragmentManager(), getTag());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals("scheduled_notification_time")) {
            try {
                NotificationHelper.scheduleNotification(preference.getContext(), true);
            } catch (Exception e) {
                LOG.error("Unexpected error", e);
            }
            //updateNextNotificationField();
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("scheduled_notification_hours")) {
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
            NotificationHelper.scheduleNotification(getPreferenceManager().getContext(), true);
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
    }
}