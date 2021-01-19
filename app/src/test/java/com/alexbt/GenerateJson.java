package com.alexbt;

import com.alexbt.bjj.dailybjj.entries.DailyEntryStatus;
import com.alexbt.bjj.dailybjj.entries.Data;
import com.alexbt.bjj.dailybjj.util.MyDateDeserializer;
import com.alexbt.bjj.dailybjj.util.MyDateSerializer;
import com.google.common.io.ByteStreams;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenerateJson {


    public static final String IMAGE_PREFIX = "https://img.youtube.com/vi/";
    public static final String IMAGE_SUFFIX = "/0.jpg";
    public static final String VIDEO_PREFIX = "https://www.youtube.com/watch?v=";

    @Test
    public void test() throws IOException {
        int i = 0;
        final int COLUMN_INDEX_STATUS = i++;
        final int COLUMN_INDEX_NOTIFICATION_DATE = i++;
        final int COLUMN_INDEX_VIDEO_DATE = i++;
        final int COLUMN_INDEX_VIDEO_URL = i++;
        final int COLUMN_INDEX_MASTER = i++;
        final int COLUMN_INDEX_TAGS = i++;
        final int COLUMN_INDEX_TITLE = i++;
        final int COLUMN_INDEX_DESCRIPTION = i++;
        final int COLUMN_INDEX_YOUTUBE_ID = i++;
        final int COLUMN_INDEX_IMAGE_URL = i++;

        File file = new File("../data/current_version.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        String currentVersion = new String(ByteStreams.toByteArray(fileInputStream));

        String urlStr = "https://docs.google.com/spreadsheets/u/0/d/18OQBKtzy-Fk5mf5fLEbbey72R1x-AcBeAVe7dwRbYvw/gviz/tq?tqx=out:HTML&tq=select+*";

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        String content = new String(ByteStreams.toByteArray(connection.getInputStream()));
        Document doc = Jsoup.parse(content);
        Elements rows = doc.getElementsByTag("tr");
        List<Element> elements = rows.size() > 1 ? rows.subList(1, rows.size()) : Collections.emptyList();
        Map<String, DailyEntryJson> dailyEntries = elements.stream()
                .map(e -> e.getElementsByTag("td").eachText())
                .filter(s -> {
                    DailyEntryStatus dailyEntryStatus = toEnum(DailyEntryStatus.class, s.get(COLUMN_INDEX_STATUS));
                    return dailyEntryStatus == DailyEntryStatus.OK;
                })
                .map(s -> {
                    DailyEntryJson dailyEntry = new DailyEntryJson();
                    dailyEntry.setStatus(toEnum(DailyEntryStatus.class, s.get(COLUMN_INDEX_STATUS)));
                    dailyEntry.setNotificationDate(LocalDate.parse(s.get(COLUMN_INDEX_NOTIFICATION_DATE)));
                    dailyEntry.setVideoDate(LocalDate.parse(s.get(COLUMN_INDEX_VIDEO_DATE)));
                    dailyEntry.setVideoUrl(s.get(COLUMN_INDEX_VIDEO_URL));
                    dailyEntry.setMaster(s.get(COLUMN_INDEX_MASTER));
                    dailyEntry.setTags(Arrays.asList(s.get(COLUMN_INDEX_TAGS).replaceAll(" ", "").split(",")));
                    dailyEntry.setTitle(s.get(COLUMN_INDEX_TITLE));
                    dailyEntry.setDescription(s.get(COLUMN_INDEX_DESCRIPTION));
                    dailyEntry.setYoutubeId(s.get(COLUMN_INDEX_YOUTUBE_ID));
                    dailyEntry.setImageUrl(s.get(COLUMN_INDEX_IMAGE_URL));
                    return dailyEntry;
                }).collect(Collectors.toMap(d -> d.getNotificationDate().toString(), Function.identity()));

        DataJson data = new DataJson();
        data.setDailyEntries(dailyEntries);
        data.setVersion(currentVersion);
        data.setImagePrefix(IMAGE_PREFIX);
        data.setImageSuffix(IMAGE_SUFFIX);
        data.setVideoPrefix(VIDEO_PREFIX);
        String s = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDate.class, new MyDateSerializer())
                .create().toJson(data);

        file = new File("../data/data.json");
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(s);
        fileWriter.flush();
        fileWriter.close();

        Data parsed = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDate.class, new MyDateDeserializer())
                .create().fromJson(s, Data.class);
        System.out.println(parsed);
    }

    private DailyEntryStatus toEnum(Class<DailyEntryStatus> dailyEntryStatusClass, String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return Enum.valueOf(dailyEntryStatusClass, s);
    }
}
