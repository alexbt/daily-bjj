package com.alexbt.bjj.dailybjj.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.util.PreferenceUtil;

class TimePreference extends DialogPreference {
    private static final int MINUTES_ONE_HOUR = 60;
    private final int DEFAULT_HOUR;
    private final int DEFAULT_MINUTES;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_HOUR = context.getResources().getInteger(R.integer.default_notification_time_hours);
        DEFAULT_MINUTES = context.getResources().getInteger(R.integer.default_notification_time_minutes);

        update(context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE));
    }

    private static int toMinutesFromMidnight(int hours, int minutes) {
        return hours * MINUTES_ONE_HOUR + minutes;
    }

    public void update(SharedPreferences sharedPreferences) {
        int hours = PreferenceUtil.getScheduledNotificationHours(sharedPreferences);
        int minutes = PreferenceUtil.getScheduledNotificationMinutes(sharedPreferences);

        super.persistInt(hours * 60 + minutes);
        updateSummary(sharedPreferences);
        callChangeListener(hours * 60 + minutes);
    }

    private void updateSummary(SharedPreferences sharedPreferences) {
        String message = PreferenceUtil.getScheduledNotificationText(sharedPreferences);
        setSummary(message);
        notifyChanged();
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        update(getSharedPreferences());
    }

    public static class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
        private TimePicker timePicker;

        public TimePreferenceDialogFragmentCompat(String key) {
            Bundle bundle = new Bundle(1);
            bundle.putString(this.ARG_KEY, key);
            setArguments(bundle);
        }

        @Override
        protected View onCreateDialogView(Context context) {
            timePicker = new TimePicker(context);
            //SharedPreferences sharedPreferences = context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE);
            //int hours = PreferenceUtil.getScheduledNotificationHours(sharedPreferences);
            //int minutes = PreferenceUtil.getScheduledNotificationMinutes(sharedPreferences);
            //timePicker.setHour(hours);
            //timePicker.setMinute(minutes);
            return timePicker;
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (!positiveResult) {
                return;
            }
            PreferenceUtil.saveNotificationTime(getPreference().getSharedPreferences(), timePicker.getHour(), timePicker.getMinute());
            ((TimePreference) getPreference()).update(getPreference().getSharedPreferences());

        }

        @Override
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);
        }
    }
}
