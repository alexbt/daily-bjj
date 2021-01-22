package com.alexbt;

import com.google.gson.annotations.Expose;

import java.time.LocalDate;
import java.util.List;

public class DailyEntryJson {

    @Expose
    private String master;

    @Expose
    private LocalDate notificationDate;

    @Expose
    private LocalDate videoDate;

    @Expose
    private String title;

    @Expose
    private String description;

    @Expose(serialize = false)
    private String videoUrl;

    @Expose(serialize = false)
    private String imageUrl;

    @Expose
    private String youtubeId;

    @Expose
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
