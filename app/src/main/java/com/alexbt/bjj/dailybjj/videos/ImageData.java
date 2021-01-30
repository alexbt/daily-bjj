package com.alexbt.bjj.dailybjj.videos;

import android.graphics.Bitmap;

import com.alexbt.bjj.dailybjj.model.DailyEntry;
import com.alexbt.bjj.dailybjj.model.Data;

public class ImageData {
    private Bitmap image;
    private String url;
    private String youtubeId;
    private Data data;
    private DailyEntry today;

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setToday(DailyEntry today) {
        this.today = today;
    }

    public DailyEntry getToday() {
        return today;
    }
}
