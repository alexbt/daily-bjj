package com.alexbt.bjj.dailybjj.settings;

import android.app.TimePickerDialog;
import android.content.Context;

public class CustomTimePicker extends TimePickerDialog {

    public CustomTimePicker(Context context, TimePickerDialog.OnTimeSetListener listener, int hours, int minutes) {
        super(context, listener, hours, minutes, true);
    }
}
