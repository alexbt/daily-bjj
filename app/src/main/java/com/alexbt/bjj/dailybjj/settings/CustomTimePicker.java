package com.alexbt.bjj.dailybjj.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

public class CustomTimePicker extends TimePickerDialog {
    private TimePicker timePicker;
    private TimePicker.OnTimeChangedListener listener;

    public CustomTimePicker(Context context, TimePickerDialog.OnTimeSetListener listener, int hours, int minutes) {
        super(context, listener, hours, minutes, true);
    }

    public void setOnTimeChangedListener(TimePicker.OnTimeChangedListener listener) {
        this.listener = listener;
    }
}
