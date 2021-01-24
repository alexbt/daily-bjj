package com.alexbt.bjj.dailybjj.util;

import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.alexbt.bjj.dailybjj.entries.Data;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class EntryHelper {
    private final Logger LOG = Logger.getLogger(EntryHelper.class);
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(LocalDate.class, new DateDeserializer())
            .registerTypeAdapter(LocalDate.class, new DateSerializer())
            .create();

    private static final String WEBVIDEO_PREFIX = "https://www.youtube.com/watch?v=";
    private static final String DATA_FILE = "/data.json";
    private static final String YOUTUBE_PREFIX = "vnd.youtube:";
    private static final String IMAGE_PREFIX = "https://img.youtube.com/vi/";
    private static final String IMAGE_SUFFIX = "/0.jpg";
    private static EntryHelper INSTANCE = new EntryHelper();

    public static EntryHelper getInstance() {
        return INSTANCE;
    }

    public Data loadData(String cacheDir) {
        LOG.info(String.format("Entering 'loadData' with cacheDir={}", cacheDir));
        Data data = loadFromCache(cacheDir);
        if (data != null) {
            if (isDataVersionLatest(data) && hasVideoForNextWeek(data)) {
                return data;
            }
        }
        data = fetchFromRemote();
        saveCacheData(data, cacheDir);
        LOG.info(String.format("Exiting 'loadData' with data={}", data));
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
        LOG.info(String.format("Entering 'isDataVersionLatest'"));
        String currentVersion = fetchCurrentVersion();
        boolean comparedResult = data.getVersion().compareTo(currentVersion) == 0;

        LOG.info(String.format("Exiting 'isDataVersionLatest' with comparedResult={}", comparedResult));
        return comparedResult;
    }

    private boolean hasVideoForNextWeek(Data data) {
        LOG.info(String.format("Entering 'hasVideoForNextWeek'"));
        boolean hasVideo = getVideo(data, DateHelper.getNextWeek()) != null;

        LOG.info(String.format("Exiting 'hasVideoForNextWeek'"));
        return hasVideo;
    }

    private Data loadFromCache(String cacheDir) {
        Data data = null;
        LOG.info(String.format("Entering 'loadFromCache'"));
        File f = new File(cacheDir, DATA_FILE);
        if (!f.exists()) {
            LOG.error(String.format("Exiting 'loadFromCache' with cacheDir={} not exist", cacheDir));
            return data;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            String jsonContent = new String(ByteStreams.toByteArray(fileInputStream));
            data = GSON.fromJson(jsonContent, Data.class);
            return data;
        } catch (Exception e) {
            LOG.error(String.format("Error 'loadFromCache'"), e);
        }

        LOG.error(String.format("Exiting 'loadFromCache' with data={}}", data));
        return data;
    }

    private Data fetchFromRemote() {
        Data data = null;
        LOG.info(String.format("Entering 'fetchFromRemote'"));
        try {
            URL url = new URL("https://raw.githubusercontent.com/alexbt/daily-bjj/master/data/data.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            String jsonContent = new String(ByteStreams.toByteArray(connection.getInputStream()));

            data = GSON.fromJson(jsonContent, Data.class);
            LOG.info(String.format("Exiting 'fetchFromRemote' with data={}", data));
        } catch (MalformedURLException e) {
            LOG.error(String.format("Error when 'fetchFromRemote'"), e);
        } catch (IOException e) {
            LOG.error(String.format("Error when 'fetchFromRemote'"), e);
        }
        LOG.info(String.format("Exiting 'fetchFromRemote' with data={}", data));
        return data;
    }

    private String fetchCurrentVersion() {
        LOG.info(String.format("Entering 'fetchCurrentVersion'"));
        String currentVersion = null;
        try {
            URL url = new URL("https://raw.githubusercontent.com/alexbt/daily-bjj/master/data/current_version.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            currentVersion = new String(ByteStreams.toByteArray(connection.getInputStream()));
        } catch (MalformedURLException e) {
            LOG.error(String.format("Error when 'fetchCurrentVersion'"), e);
        } catch (IOException e) {
            LOG.error(String.format("Error when 'fetchCurrentVersion'"), e);
        }

        LOG.info(String.format("Exiting 'fetchCurrentVersion' with currentVersion={}", currentVersion));
        return currentVersion;
    }

    private void saveCacheData(Data data, String cacheDir) {
        LOG.info(String.format("Entering 'saveCacheData' with cacheDir={}", cacheDir));
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

    public static String getWebVideoUrl(String videoId) {
        return WEBVIDEO_PREFIX + videoId;
    }
}
