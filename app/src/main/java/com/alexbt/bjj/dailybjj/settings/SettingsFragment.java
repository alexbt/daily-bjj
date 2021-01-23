package com.alexbt.bjj.dailybjj.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceUtil;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("com.alexbt.DailyNotificationPreference");

        setPreferencesFromResource(R.xml.prefs, rootKey);
        preferenceManager.findPreference("scheduled_notification_time").setOnPreferenceChangeListener(this);
        //preferenceManager.findPreference("last_notification_time").setOnPreferenceChangeListener(this);
        //preferenceManager.findPreference("next_notification_time").setOnPreferenceChangeListener(this);

        updateLastNotification();
        updateNextNotification();
    }

    private void updateNextNotification() {
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
            NotificationHelper.scheduleNotification(preference.getContext(), true);
            updateNextNotification();
        }
        return true;
    }
}