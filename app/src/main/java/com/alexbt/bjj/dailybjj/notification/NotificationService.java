package com.alexbt.bjj.dailybjj.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.model.DailyEntry;
import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.RemoteHelper;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;
import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.alexbt.bjj.dailybjj.util.PreferenceHelper;

import org.apache.log4j.Logger;

public class NotificationService extends Service {
    private final Logger LOG = Logger.getLogger(NotificationService.class);
    private static final int NOTIFICATION_ID = 1;
    private static final int PENDING_INTENT_REQUEST_CODE = 0;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.info("Entering 'onStartCommand'");
        try {
            //TODO bad code...
            stopForeground(true);
            new Thread(() -> {
                displayNotification("application", getApplicationContext());
            }).start();
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
        DailyEntry today = RemoteHelper.getInstance().getTodayVideo(cacheDir);
        LOG.info(String.format("today's DailyEntry=%s", today));
        if (today == null) {
            LOG.warn("Exiting 'displayNotification' with today's DailyEntry={}");
            return;
        }

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        final String master = today.getMaster();
        final String description = today.getDescription();
        final String videoId = today.getYoutubeId();
        final String videoUrl = RemoteHelper.getInstance().getWebVideoUrl(videoId);
        final String imageUrl = RemoteHelper.getInstance().getImageUrl(videoId);
        resultIntent.setData(Uri.parse(videoUrl));
        LOG.info("Created resultIntent");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        LOG.info("Created pendingIntent");

        Bitmap youtubeImage = RemoteHelper.getBitmapFromUrl(imageUrl);
        String channelId = NotificationHelper.createNotificationChannel(context);
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

        PreferenceHelper.updateLastNotificationTime(getApplicationContext(), DateHelper.getNow());

        LOG.info("Notified pendingIntent");
        LOG.info("Exiting 'displayNotification'");
    }

    public NotificationService() {
        LOG.info("Performing 'constructor'");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String channel = NotificationHelper.createNotificationChannel(getApplicationContext());
        Notification notification = new NotificationCompat.Builder(this, channel).build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LOG.info("Performing 'onBind'");
        return null;
    }
}