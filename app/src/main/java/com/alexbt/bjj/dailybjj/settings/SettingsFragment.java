package com.alexbt.bjj.dailybjj.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceUtil;
import com.google.common.io.ByteStreams;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private final Logger LOG = Logger.getLogger(SettingsFragment.class);

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("com.alexbt.DailyNotificationPreference");

        setPreferencesFromResource(R.xml.prefs, rootKey);

        Preference button = findPreference("myCoolButton");
        button.setOnPreferenceClickListener(preference -> {
            try {
                String buildVersion = getContext().getResources().getString(R.string.build_version);
                File logFile = new File(FileSystemHelper.getCacheDir(getContext()) + "/myapp.log");
                String jsonContent = new String(ByteStreams.toByteArray(new FileInputStream(logFile)));
                File tempFile = File.createTempFile(String.format("DailyBjj-%s_", buildVersion), ".log", getContext().getExternalCacheDir());
                FileWriter fw = new FileWriter(tempFile);
                fw.write(jsonContent);
                fw.flush();
                fw.close();

                Uri logFileUri = Uri.fromFile(tempFile);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alex.belisleturcot@gmail.com"});
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "DailyBJJ Logs");
                intent.putExtra(Intent.EXTRA_STREAM, logFileUri);
                intent.putExtra(Intent.EXTRA_TEXT, String.format("Hi Alex,\n\nPlease find attached my DailyBJJ logs!\n\n%s: %s\n\nRegards,",
                        getContext().getResources().getString(R.string.build_version_title),
                        buildVersion));
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 1);

            } catch (Exception e) {
            }

            return true;
        });


        preferenceManager.findPreference("scheduled_notification_time").setOnPreferenceChangeListener(this);
        //preferenceManager.findPreference("last_notification_time").setOnPreferenceChangeListener(this);
        //preferenceManager.findPreference("next_notification_time").setOnPreferenceChangeListener(this);

        updateLastNotification();
        updateNextNotificationField();
    }

    private void updateNextNotificationField() {
        String message = PreferenceUtil.getNextNotificationText(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().findPreference("next_notification_time").setSummary(message);
    }

    private void updateLastNotification() {
        String message = PreferenceUtil.getLastNotificationText(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().findPreference("last_notification_time").setSummary(message);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference.getKey().equals("scheduled_notification_time")) {
            TimePreference.TimePreferenceDialogFragmentCompat timepickerdialog = new TimePreference.TimePreferenceDialogFragmentCompat("scheduled_notification_time");
            timepickerdialog.setTargetFragment(this, 0);
            timepickerdialog.show(getFragmentManager(), getTag());
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
            updateNextNotificationField();
        }
        return true;
    }
}