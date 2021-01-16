package com.alexbt.bjj.dailybjj.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.entries.DailyEntry;
import com.alexbt.bjj.dailybjj.entries.Data;
import com.alexbt.bjj.dailybjj.util.EntryHelper;
import com.alexbt.bjj.dailybjj.util.FileSystemHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static final int PENDING_INTENT_REQUEST_CODE = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        new Thread() {
            @Override
            public void run() {
                displayNotification(context);
            }
        }.start();
    }

    private void displayNotification(Context context) {
        String cacheDir = FileSystemHelper.getCacheDir(context);
        DailyEntry today = EntryHelper.getInstance().getTodayVideo(cacheDir);
        if (today == null) {
            return;
        }

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        final String master = today.getMaster();
        final String description = today.getDescription();
        final String videoId = today.getYoutubeId();
        final String videoUrl = EntryHelper.getInstance().getWebVideoUrl(videoId);
        final String imageUrl = EntryHelper.getInstance().getImageUrl(videoId);
        resultIntent.setData(Uri.parse(videoUrl));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap youtubeImage = getBitmapFromUrl(imageUrl);
        String channelId = createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentIntent(pendingIntent)
                .setContentTitle(master)
                .setContentText(description)
                .setLargeIcon(youtubeImage)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigLargeIcon(null)
                        .bigPicture(youtubeImage))
                .setChannelId(channelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    private Bitmap getBitmapFromUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createNotificationChannel(Context context) {
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = context.getResources().getString(R.string.app_name);
            // The id of the channel.
            String channelId = appName + "_ChannelId";

            // The user-visible name of the channel.
            CharSequence channelName = appName;
            // The user-visible description of the channel.
            String channelDescription = appName + " Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
            //            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
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
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
}