package com.alexbt.bjj.dailybjj.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.alexbt.bjj.dailybjj.MainActivity;
import com.alexbt.bjj.dailybjj.R;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs, rootKey);
        Preference preferenceTime = getPreferenceManager().findPreference("notificationTime");
        preferenceTime.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePreference) {
            TimePreference.TimePreferenceDialogFragmentCompat timepickerdialog = new TimePreference.TimePreferenceDialogFragmentCompat("notificationTime");
            timepickerdialog.setTargetFragment(this, 0);
            timepickerdialog.show(getFragmentManager(), getTag());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        TimePreference timePreference = (TimePreference) preference;
        ((MainActivity) getActivity()).showNotification(timePreference.getHours(), timePreference.getMinutes());
        return true;
    }
}