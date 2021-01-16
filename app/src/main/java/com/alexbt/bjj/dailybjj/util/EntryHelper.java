package com.alexbt.bjj.dailybjj.util;

import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.alexbt.bjj.dailybjj.entries.Data;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(LocalDate.class, new MyDateDeserializer())
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
        Data data = loadFromCache(cacheDir);
        if (data != null) {
            if (isDataVersionLatest(data) && hasVideoForNextWeek(data)) {
                return data;
            }
        }
        data = fetchFromRemote();
        saveCacheData(data, cacheDir);
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
        String currentVersion = fetchCurrentVersion();
        return data.getVersion().compareTo(currentVersion) == 0;
    }

    private boolean hasVideoForNextWeek(Data data) {
        return getVideo(data, DateHelper.getNextWeek()) != null;
    }

    private Data loadFromCache(String cacheDir) {
        File f = new File(cacheDir, cacheDir + DATA_FILE);
        if (!f.exists()) {
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            String jsonContent = new String(ByteStreams.toByteArray(fileInputStream));
            return GSON.fromJson(jsonContent, Data.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Data fetchFromRemote() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/alexbt/daily-bjj/master/data/data.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            String jsonContent = new String(ByteStreams.toByteArray(connection.getInputStream()));

            Data data = GSON.fromJson(jsonContent, Data.class);
            return data;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String fetchCurrentVersion() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/alexbt/daily-bjj/master/data/current_version.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            return new String(ByteStreams.toByteArray(connection.getInputStream()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveCacheData(Data data, String cacheDir) {
        try {
            String content = new Gson().toJson(data);
            File cachedDataFile = new File(cacheDir, cacheDir + DATA_FILE);
            if (cachedDataFile.exists() && cachedDataFile.isDirectory()) {
                cachedDataFile.delete();
                cachedDataFile = new File(cacheDir, cacheDir + DATA_FILE);
            }
            Files.createDirectories(Paths.get(cachedDataFile.getParentFile().getPath()));
            FileWriter fw = new FileWriter(cachedDataFile, false);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
