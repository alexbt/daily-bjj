package com.alexbt.bjj.dailybjj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alexbt.bjj.dailybjj.util.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        SharedPreferences preferences = getSharedPreferences("com.alexbt.DailyNotificationPreference", Context.MODE_PRIVATE);
        if (!preferences.contains("notification_time")) {
            SharedPreferences.Editor editor = preferences.edit();
            int hours = getApplicationContext().getResources().getInteger(R.integer.default_notification_time_hours);
            editor.putInt("notification_time", hours * 60);
            editor.commit();
            NotificationHelper.scheduleNotification(getApplicationContext(), true);
        }

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        */
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
