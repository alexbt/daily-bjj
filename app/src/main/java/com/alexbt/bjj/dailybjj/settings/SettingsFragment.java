package com.alexbt.bjj.dailybjj.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.alexbt.bjj.dailybjj.MainActivity;
import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.notification.BootCompletedReceiver;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;

import java.time.LocalDate;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("com.alexbt.DailyNotificationPreference");

        setPreferencesFromResource(R.xml.prefs, rootKey);
        Preference preferenceTime = preferenceManager.findPreference("notification_time");
        preferenceTime.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePreference) {
            TimePreference.TimePreferenceDialogFragmentCompat timepickerdialog = new TimePreference.TimePreferenceDialogFragmentCompat("notification_time");
            timepickerdialog.setTargetFragment(this, 0);
            timepickerdialog.show(getFragmentManager(), getTag());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        NotificationHelper.scheduleNotification(preference.getContext(), preference.getSharedPreferences(), true);
        return true;
    }
}