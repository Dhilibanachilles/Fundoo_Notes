package com.example.loginapp.alarm_manager;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.loginapp.R;
import com.example.loginapp.dashboard.HomeActivity;
import com.example.loginapp.data_manager.SharedPreferenceHelper;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    SharedPreferenceHelper sharedPreference = new SharedPreferenceHelper(this);

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID,
                      channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    String title = sharedPreference.getNoteTitle();
    String description = sharedPreference.getNoteDescription();

    public NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent = new Intent(this, HomeActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, channelID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.fundoo1)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }
}