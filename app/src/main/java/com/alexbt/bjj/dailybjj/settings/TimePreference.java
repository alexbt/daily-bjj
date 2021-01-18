package com.alexbt.bjj.dailybjj.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.alexbt.bjj.dailybjj.R;

class TimePreference extends DialogPreference {
    private static final int MINUTES_ONE_HOUR = 60;
    private final int DEFAULT_HOUR;
    private final int DEFAULT_MINUTES;
    private final int DEFAULT_MINUTES_FROM_MIDNIGHT;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_HOUR = context.getResources().getInteger(R.integer.default_notification_time_hours);
        DEFAULT_MINUTES = context.getResources().getInteger(R.integer.default_notification_time_minutes);
        DEFAULT_MINUTES_FROM_MIDNIGHT = toMinutesFromMidnight(DEFAULT_HOUR, DEFAULT_MINUTES);

        int minutesFromMidnight = getPersistedInt(DEFAULT_MINUTES_FROM_MIDNIGHT);
        updateSummary(minutesFromMidnight);
    }

    private static int toMinutesFromMidnight(int hours, int minutes) {
        return hours * MINUTES_ONE_HOUR + minutes;
    }

    public void persistMinutesFromMidnight(int minutesFromMidnight) {
        super.persistInt(minutesFromMidnight);
        updateSummary(minutesFromMidnight);
        callChangeListener(minutesFromMidnight);
    }

    private void updateSummary(int minutesFromMidnight) {
        int hour = minutesFromMidnight / MINUTES_ONE_HOUR;
        int minutes = minutesFromMidnight % MINUTES_ONE_HOUR;
        setSummary(String.format("%02dh%02d %s", hour, minutes, hour <= 11 ? "AM" : "PM"));
        notifyChanged();
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        int minutesFromMidnight = getPersistedInt(DEFAULT_MINUTES_FROM_MIDNIGHT);
        updateSummary(minutesFromMidnight);
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
            return timePicker;
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (!positiveResult) {
                return;
            }
            int minutesAfterMidnight = toMinutesFromMidnight(timePicker.getHour(), timePicker.getMinute());
            ((TimePreference) getPreference()).persistMinutesFromMidnight(minutesAfterMidnight);
        }

        @Override
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);
        }
    }
}
