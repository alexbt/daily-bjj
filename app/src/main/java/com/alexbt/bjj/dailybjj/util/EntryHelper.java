package com.alexbt.bjj.dailybjj.util;

import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.alexbt.bjj.dailybjj.entries.Data;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntryHelper {
    private static final Gson GSON = new Gson();
    private static final int ONE_WEEK = 7;
    private static final String WEBVIDEO_PREFIX = "https://www.youtube.com/watch?v=";
    private static final String DATA_FILE = "/data.json";
    private static final String YOUTUBE_PREFIX = "vnd.youtube:";
    private static final String IMAGE_PREFIX = "https://img.youtube.com/vi/";
    private static final String IMAGE_SUFFIX = "/0.jpg";
    public static final String COMMA = ",";
    public static final String HIGHER_OR_EQUAL = "%3E%3D";
    public static final String SPACE = "+";
    public static final String EQUAL = "%3D";
    public static final String DATE_KEYWORD = "date" + SPACE;
    public static final String QUOTE = "'";
    public static final String AND_KEYWORD = "+and+";
    public static final String LOWER_OR_EQUAL = "%3C%3D";
    public static final String STATUS_OK = "'OK'";
    public static final String CLOSING_PARENTHESIS = ")";
    public static final String OPENING_PARENTHESIS = "(";
    public static final String WHERE_KEYWORD = SPACE + "where" + SPACE;
    public static final String SELECT_KEYWORD = "select" + SPACE;
    public static final String DOCUMENT_ID = "18OQBKtzy-Fk5mf5fLEbbey72R1x-AcBeAVe7dwRbYvw";
    private static EntryHelper INSTANCE = new EntryHelper();
    private static final int COLUMN_STATUS_INDEX = 0;
    private static final String COLUMN_STATUS_CHAR = "A";
    private static final int COLUMN_NOTIFICATION_DATE_INDEX = 1;
    private static final String COLUMN_NOTIFICATION_DATE_CHAR = "B";
    private static final int COLUMN_VIDEO_DATE_INDEX = 2;
    private static final String COLUMN_VIDEO_DATE_CHAR = "C";
    private static final int COLUMN_YOUTUBE_ID_INDEX = 3;
    private static final String COLUMN_YOUTUBE_ID_CHAR = "I";
    private static final int COLUMN_MASTER_INDEX = 4;
    private static final String COLUMN_MASTER_CHAR = "E";
    private static final int COLUMN_TITLE_INDEX = 5;
    private static final String COLUMN_TITLE_CHAR = "G";
    private static final int COLUMN_DESCRIPTION_INDEX = 6;
    private static final String COLUMN_DESCRIPTION_CHAR = "H";
    private static final int COLUMN_TAGS_INDEX = 7;
    private static final String COLUMN_TAGS_CHAR = "F";


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
            String currentVersion = fetchCurrentVersion();

            //https://developers.google.com/chart/interactive/docs/querylanguage
            //https://docs.google.com/spreadsheets/u/0/d/18OQBKtzy-Fk5mf5fLEbbey72R1x-AcBeAVe7dwRbYvw/gviz/tq?tqx=out:HTML&tq=select+A,B,H,D,F,G,E+where+(A%3D'OK'+and+B%3E%3Ddate+'2021-01-10'+and+B%3C%3Ddate+'2021-01-16')
            String urlStr = "https://docs.google.com/spreadsheets/u/0/d/" + DOCUMENT_ID + "/gviz/tq" + "?tqx=out:HTML&tq="
                    + SELECT_KEYWORD
                    + COLUMN_STATUS_CHAR + COMMA
                    + COLUMN_NOTIFICATION_DATE_CHAR + COMMA
                    + COLUMN_VIDEO_DATE_CHAR + COMMA
                    + COLUMN_YOUTUBE_ID_CHAR + COMMA
                    + COLUMN_MASTER_CHAR + COMMA
                    + COLUMN_TITLE_CHAR + COMMA
                    + COLUMN_DESCRIPTION_CHAR + COMMA
                    + COLUMN_TAGS_CHAR
                    + WHERE_KEYWORD + OPENING_PARENTHESIS
                    + COLUMN_STATUS_CHAR + EQUAL + STATUS_OK
                    + AND_KEYWORD + COLUMN_NOTIFICATION_DATE_CHAR + HIGHER_OR_EQUAL + DATE_KEYWORD + QUOTE + DateHelper.getLastWeek().toString() + QUOTE
                    + AND_KEYWORD + COLUMN_NOTIFICATION_DATE_CHAR + LOWER_OR_EQUAL + DATE_KEYWORD + QUOTE + DateHelper.getToday().toString() + QUOTE + CLOSING_PARENTHESIS;

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            String content = new String(ByteStreams.toByteArray(connection.getInputStream()));
            Document doc = Jsoup.parse(content);
            Elements rows = doc.getElementsByTag("tr");
            List<Element> elements = rows.size() > 1 ? rows.subList(1, rows.size()) : Collections.emptyList();
            Map<String, DailyEntry> dailyEntries = elements.stream()
                    .map(e -> {
                        Elements td = e.getElementsByTag("td");
                        List<String> strings = td.eachText();
                        DailyEntry dailyEntry = new DailyEntry();
                        dailyEntry.setNotificationDate(LocalDate.parse(strings.get(COLUMN_NOTIFICATION_DATE_INDEX).replace(DATE_KEYWORD, "")));
                        dailyEntry.setVideoDate(LocalDate.parse(strings.get(COLUMN_VIDEO_DATE_INDEX).replace(DATE_KEYWORD, "")));
                        dailyEntry.setMaster(strings.get(COLUMN_MASTER_INDEX));
                        dailyEntry.setTitle(strings.get(COLUMN_TITLE_INDEX));
                        dailyEntry.setDescription(strings.get(COLUMN_DESCRIPTION_INDEX));
                        dailyEntry.setYoutubeId(strings.get(COLUMN_YOUTUBE_ID_INDEX));
                        dailyEntry.setTags(Arrays.asList(strings.get(COLUMN_TAGS_INDEX).replaceAll(" ", "").split(",")));
                        return dailyEntry;
                    }).collect(Collectors.toMap(d -> d.getNotificationDate().toString(), Function.identity()));

            Data data = new Data();
            data.setDailyEntries(dailyEntries);
            data.setVersion(currentVersion);
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
            URL url = new URL("https://drive.google.com/uc?id=1_fyr2itNkeY7Kg3zcr37je9y9_b6oHxv");
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
