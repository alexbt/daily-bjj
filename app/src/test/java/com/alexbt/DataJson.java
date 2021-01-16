package com.alexbt;

import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class DataJson {
    @Expose
    private String version;

    @Expose
    private String videoPrefix;

    @Expose
    private String imagePrefix;

    @Expose
    private String imageSuffix;

    @Expose
    Map<String, DailyEntryJson> dailyEntries;

    public String getVersion() {
        return version;
    }

    public Map<String, DailyEntryJson> getDailyEntries() {
        return dailyEntries;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDailyEntries(Map<String, DailyEntryJson> dailyEntries) {
        this.dailyEntries = dailyEntries;
    }

    public void setImagePrefix(String imagePrefix) {
        this.imagePrefix = imagePrefix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public void setVideoPrefix(String videoPrefix) {
        this.videoPrefix = videoPrefix;
    }

    public String getImagePrefix() {
        return imagePrefix;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public String getVideoPrefix() {
        return videoPrefix;
    }
}
