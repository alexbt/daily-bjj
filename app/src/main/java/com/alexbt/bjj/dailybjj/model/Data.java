package com.alexbt.bjj.dailybjj.model;

import com.google.gson.annotations.Expose;

import java.util.Map;

public class Data {
    @Expose
    private String version;

    @Expose
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

    @Override
    public String toString() {
        return "Data{" +
                "version='" + version + '\'' +
                ", dailyEntries=" + dailyEntries +
                '}';
    }
}
