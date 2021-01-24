package com.alexbt.bjj.dailybjj.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.DialogPreference;

import com.alexbt.bjj.dailybjj.util.DateHelper;

import java.time.LocalDateTime;

class CustomTimePreference extends DialogPreference {
    private CustomTimePicker customTimePicker;

    public CustomTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        //
        getPreferenceManager().setSharedPreferencesName("com.alexbt.DailyNotificationPreference");
    }

    public void onDisplayPreferenceDialog(Fragment fragment, TimePickerDialog.OnTimeSetListener listener, FragmentManager fragmentManager, String tag) {
        getPreferenceManager().setSharedPreferencesName("com.alexbt.DailyNotificationPreference");
        LocalDateTime now = DateHelper.getNow();
        customTimePicker = new CustomTimePicker(getContext(), listener, now.getHour(), now.getMinute());
        customTimePicker.show();
    }
}
