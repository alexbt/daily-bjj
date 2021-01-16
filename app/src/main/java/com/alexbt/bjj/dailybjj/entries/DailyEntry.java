package com.alexbt.bjj.dailybjj.entries;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.List;

public class DailyEntry {

    @SerializedName(value = "master")
    private String master;

    @SerializedName(value = "notificationDate")
    private LocalDate notificationDate;

    @SerializedName(value = "videoDate")
    private LocalDate videoDate;

    @SerializedName(value = "title")
    private String title;

    @SerializedName(value = "desc")
    private String description;

    @SerializedName(value = "youtubeId")
    private String youtubeId;

    @SerializedName(value = "tags")
    private List<String> tags;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getDescription() {
        return description;
    }

    public String getMaster() {
        return master;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setNotificationDate(LocalDate date) {
        this.notificationDate = date;
    }

    public LocalDate getNotificationDate() {
        return notificationDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getVideoDate() {
        return videoDate;
    }

    public void setVideoDate(LocalDate videoDate) {
        this.videoDate = videoDate;
    }
}
