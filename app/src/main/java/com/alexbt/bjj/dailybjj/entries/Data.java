package com.alexbt.bjj.dailybjj.entries;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Data {
    @SerializedName(value = "version")
    private String version;

    @SerializedName(value = "dailyEntries")
    Map<String, DailyEntry> dailyEntries;

    public String getVersion() {
        return version;
    }

    public Map<String, DailyEntry> getDailyEntries() {
        return dailyEntries;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDailyEntries(Map<String, DailyEntry> dailyEntries) {
        this.dailyEntries = dailyEntries;
    }
}
