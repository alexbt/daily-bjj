package com.alexbt.bjj.dailybjj;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alexbt.bjj.dailybjj.notification.NotificationReceiver;
import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //TODO
    //private Map<Class, Object> cache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_videos, R.id.navigation_home)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        SharedPreferences preferences = getSharedPreferences("DailyBjjPreference", MODE_PRIVATE);
        if (!preferences.getBoolean("isFirstTime", false)) {
            showNotification(R.integer.default_notification_time_hours, R.integer.default_notification_time_minutes);

            final SharedPreferences pref = getSharedPreferences("DailyBjjPreference", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirstTime", true);
            editor.commit();
        }
    }

    public void showNotification(int hour, int minutes) {
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Calendar notificationTime = DateHelper.getNotificationTime(hour, minutes);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //TODO
//    public <T> T getData(Class<T> clazz) {
//        return (T)cache.get(clazz);
//    }

    //TODO
//    public <T> void setData(T o) {
//        cache.put(o.getClass(), o);
//    }
}
