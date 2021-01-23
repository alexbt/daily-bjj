package com.alexbt.bjj.dailybjj.util;

import android.content.Context;

import com.alexbt.bjj.dailybjj.R;

public class FileSystemHelper {

    public static String getCacheDir(Context context) {
        String cacheDir = context.getResources().getString(R.string.cache_dir);
        return context.getCacheDir().getAbsolutePath() + "/" + cacheDir;
    }

}
