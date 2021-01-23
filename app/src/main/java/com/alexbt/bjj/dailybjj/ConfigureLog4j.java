package com.alexbt.bjj.dailybjj;

import android.content.Context;

import com.alexbt.bjj.dailybjj.util.FileSystemHelper;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class ConfigureLog4j {
    private static final long maxFileSize = 512 * 1024;

    public static void configure(Context applicationContext) {
        final LogConfigurator logConfigurator = new LogConfigurator();

        logConfigurator.setFileName(FileSystemHelper.getCacheDir(applicationContext) + "/dailybjj.log");
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.configure();

        logConfigurator.setMaxFileSize(maxFileSize);
    }
}
