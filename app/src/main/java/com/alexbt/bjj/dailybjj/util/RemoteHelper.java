package com.alexbt.bjj.dailybjj.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;

import com.alexbt.bjj.dailybjj.model.DailyEntry;
import com.alexbt.bjj.dailybjj.model.Data;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class RemoteHelper {
    private static final Logger LOG = Logger.getLogger(RemoteHelper.class);
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(LocalDate.class, new DateDeserializer())
            .registerTypeAdapter(LocalDate.class, new DateSerializer())
            .create();

    private static final StrictMode.ThreadPolicy THREAD_POLICY = new StrictMode.ThreadPolicy.Builder()
            .permitAll()
            .build();

    private static final String WEBVIDEO_PREFIX = "https://www.youtube.com/watch?v=";
    private static final String DATA_FILE = "/data.json";
    private static final String YOUTUBE_PREFIX = "vnd.youtube:";
    private static final String IMAGE_PREFIX = "https://img.youtube.com/vi/";
    private static final String IMAGE_SUFFIX = "/0.jpg";
    private static final RemoteHelper INSTANCE = new RemoteHelper();

    public static RemoteHelper getInstance() {
        return INSTANCE;
    }

    public static Bitmap getBitmapFromUrl(String url) {
        LOG.info("Entering 'getBitmapFromUrl'");
        StrictMode.setThreadPolicy(THREAD_POLICY);

        Bitmap myBitmap = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            LOG.error("error 'getBitmapFromUrl'", e);
        }

        LOG.info("Exiting 'getBitmapFromUrl'");
        return myBitmap;
    }

    public Data loadData(String cacheDir) {
        LOG.info(String.format("Entering 'loadData' with cacheDir=%s", cacheDir));
        Data data = loadFromCache(cacheDir);
        if (data != null) {
            if (isDataVersionLatest(data) && hasVideoForNextWeek(data)) {
                return data;
            }
        }
        data = fetchFromRemote();
        saveCacheData(data, cacheDir);
        LOG.info(String.format("Exiting 'loadData' with data=%s", data));
        return data;
    }

    public DailyEntry getVideo(Data data, LocalDate date) {
        boolean isEmpty = Optional.of(data).map(Data::getDailyEntries).map(Map::isEmpty).orElse(true);
        return isEmpty ? null : data.getDailyEntries().get(date.toString());
    }

    public DailyEntry getTodayVideo(String cacheDir) {
        Data data = loadData(cacheDir);
        return getVideo(data, DateHelper.getToday());
    }

    private boolean isDataVersionLatest(Data data) {
        LOG.info("Entering 'isDataVersionLatest'");
        String currentVersion = fetchCurrentVersion();
        boolean comparedResult = Optional.ofNullable(data.getVersion())
                .map(s -> s.compareTo(currentVersion) == 0).orElse(false);

        LOG.info(String.format("Exiting 'isDataVersionLatest' with comparedResult=%s", comparedResult));
        return comparedResult;
    }

    private boolean hasVideoForNextWeek(Data data) {
        LOG.info("Entering 'hasVideoForNextWeek'");
        boolean hasVideo = getVideo(data, DateHelper.getNextWeek()) != null;

        LOG.info("Exiting 'hasVideoForNextWeek'");
        return hasVideo;
    }

    private Data loadFromCache(String cacheDir) {
        Data data = null;
        LOG.info("Entering 'loadFromCache'");
        File f = new File(cacheDir, DATA_FILE);
        if (!f.exists()) {
            LOG.error(String.format("Exiting 'loadFromCache' with cacheDir=%s not exist", cacheDir));
            return data;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            String jsonContent = new String(ByteStreams.toByteArray(fileInputStream));
            data = GSON.fromJson(jsonContent, Data.class);
            return data;
        } catch (Exception e) {
            LOG.error("Error 'loadFromCache'", e);
        }

        LOG.error(String.format("Exiting 'loadFromCache' with data=%s", data));
        return data;
    }

    private Data fetchFromRemote() {
        StrictMode.setThreadPolicy(THREAD_POLICY);

        Data data = null;
        LOG.info("Entering 'fetchFromRemote'");
        try {
            URL url = new URL("https://raw.githubusercontent.com/alexbt/daily-bjj-android/master/data/data.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            String jsonContent = new String(ByteStreams.toByteArray(connection.getInputStream()));

            data = GSON.fromJson(jsonContent, Data.class);
            LOG.info(String.format("Exiting 'fetchFromRemote' with data=%s", data));
        } catch (IOException e) {
            LOG.error("Error when 'fetchFromRemote'", e);
        }
        LOG.info(String.format("Exiting 'fetchFromRemote' with data=%s", data));
        return data;
    }

    private String fetchCurrentVersion() {
        StrictMode.setThreadPolicy(THREAD_POLICY);

        String currentVersion = null;
        try {
            URL url = new URL("https://raw.githubusercontent.com/alexbt/daily-bjj-android/master/data/current_version.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            currentVersion = new String(ByteStreams.toByteArray(connection.getInputStream()));
        } catch (MalformedURLException e) {
            LOG.error("Error when 'fetchCurrentVersion'", e);
        } catch (IOException e) {
            LOG.error("Error when 'fetchCurrentVersion'", e);
        }

        LOG.info(String.format("Exiting 'fetchCurrentVersion' with currentVersion=%s", currentVersion));
        return currentVersion;
    }

    private void saveCacheData(Data data, String cacheDir) {
        LOG.info(String.format("Entering 'saveCacheData' with cacheDir=%s", cacheDir));
        try {
            String content = GSON.toJson(data);
            File cachedDataFile = new File(cacheDir, DATA_FILE);
            if (cachedDataFile.exists() && cachedDataFile.isDirectory()) {
                cachedDataFile.delete();
                cachedDataFile = new File(cacheDir, DATA_FILE);
            }
            Files.createDirectories(Paths.get(cachedDataFile.getParentFile().getPath()));
            FileWriter fw = new FileWriter(cachedDataFile, false);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            LOG.error("Failed to save cache", e);
        }
        LOG.info(String.format("Exiting 'saveCacheData'"));
    }

    public String getImageUrl(String videoId) {
        return IMAGE_PREFIX + videoId + IMAGE_SUFFIX;
    }

    public String getYoutubeVideoUrl(String videoId) {
        return YOUTUBE_PREFIX + videoId;
    }

    public String getWebVideoUrl(String videoId) {
        return WEBVIDEO_PREFIX + videoId;
    }
}
