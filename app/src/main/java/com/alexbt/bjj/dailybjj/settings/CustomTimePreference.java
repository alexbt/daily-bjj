package com.alexbt.bjj.dailybjj.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.DialogPreference;

import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceHelper;

import java.time.LocalDateTime;

class CustomTimePreference extends DialogPreference {

    public CustomTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        PreferenceHelper.initSharedPreference(getPreferenceManager());
    }

    public void onDisplayPreferenceDialog(Fragment fragment, TimePickerDialog.OnTimeSetListener listener, FragmentManager fragmentManager, String tag) {
        PreferenceHelper.initSharedPreference(getPreferenceManager());
        LocalDateTime now = DateHelper.getNow();
        CustomTimePicker customTimePicker = new CustomTimePicker(getContext(), listener, now.getHour(), now.getMinute());
        customTimePicker.show();
    }
}
