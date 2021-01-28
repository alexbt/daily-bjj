package com.alexbt.bjj.dailybjj.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.EntryHelper;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationService extends Service {
    private final Logger LOG = Logger.getLogger(NotificationService.class);
    private static final int NOTIFICATION_ID = 1;
    private static final int PENDING_INTENT_REQUEST_CODE = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.info("Entering 'onStartCommand'");
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //new Thread(() -> displayNotification("base", getBaseContext())).start();
            displayNotification("application", getApplicationContext());
        } catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
        LOG.info("Exiting 'onStartCommand'");
        return Service.START_STICKY;
    }

    private void displayNotification(String name, Context context) {
        LOG.info("Entering 'displayNotification'");
        String cacheDir = FileSystemHelper.getCacheDir(context);
        LOG.info(String.format("cacheDir=%s", cacheDir));
        DailyEntry today = EntryHelper.getInstance().getTodayVideo(cacheDir);
        LOG.info(String.format("today's DailyEntry=%s", today));
        if (today == null) {
            LOG.warn("Exiting 'displayNotification' with today's DailyEntry={}");
            return;
        }

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        final String master = today.getMaster();
        final String description = today.getDescription();
        final String videoId = today.getYoutubeId();
        final String videoUrl = EntryHelper.getInstance().getWebVideoUrl(videoId);
        final String imageUrl = EntryHelper.getInstance().getImageUrl(videoId);
        resultIntent.setData(Uri.parse(videoUrl));
        LOG.info("Created resultIntent");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        LOG.info("Created pendingIntent");

        Bitmap youtubeImage = getBitmapFromUrl(imageUrl);
        String channelId = createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentIntent(pendingIntent)
                .setContentTitle(name + ": " + master)
                .setContentText(description)
                .setLargeIcon(youtubeImage)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigLargeIcon(null)
                        .bigPicture(youtubeImage))
                .setChannelId(channelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        LOG.info("Before notifying pendingIntent");
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        SharedPreferences.Editor edit = context.getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE).edit();
        edit.putString("last_notification_time", DateHelper.getNow().toString());
        edit.apply();

        LOG.info("Notified pendingIntent");
        LOG.info("Exiting 'displayNotification'");
    }


    private Bitmap getBitmapFromUrl(String url) {
        LOG.info("Entering 'getBitmapFromUrl'");
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

    private String createNotificationChannel(Context context) {
        LOG.info("Entering 'createNotificationChannel'");

        String channelId = null;
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = context.getResources().getString(R.string.app_name);
            // The id of the channel.
            channelId = appName + "_ChannelId";

            // The user-visible name of the channel.
            // The user-visible description of the channel.
            String channelDescription = appName + " Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
            //            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, appName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            //            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        }

        LOG.info(String.format("Exiting 'createNotificationChannel' with channelId=%s", channelId));
        return channelId;
    }

    public NotificationService(){
        LOG.info("Performing 'constructor'");
    }

    @Override
    public void onCreate() {
        LOG.info("Entering 'onCreate'");
        super.onCreate();
        LOG.info("Exiting 'onCreate'");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LOG.info("Performing 'onBind'");
        return null;
    }
}