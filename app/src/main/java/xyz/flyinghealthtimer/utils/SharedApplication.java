package xyz.flyinghealthtimer.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class SharedApplication extends Application {

    public static final String CHANNEL_ID = "xyz.flyinghealthtimer.channel";
    public static final String CHANNEL_NAME = "flyinghealthtimer_channel";
    public static final String CHANNEL_DESCRIPTION = "channel for flying health timer notification";


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    CHANNEL_DESCRIPTION,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
        }
    }

    private void createNotificationChannel(String id, String name, String description,
                                           int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    id,
                    name,
                    importance
            );
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}