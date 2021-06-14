package com.example.loginapp.work_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.loginapp.R;

public class MyWorker extends Worker {
    public static final String NOTIFICATION_TITLE = "Title";
    public static final String NOTIFICATION_MESSAGE = "Description";
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String notificationTitle = getInputData().getString(NOTIFICATION_TITLE);
        String notificationMessage = getInputData().getString(NOTIFICATION_MESSAGE);
        displayNotification(notificationTitle, notificationMessage);
        return Result.success();
    }

    private void displayNotification(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ChannelID",
                    "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.
                Builder(getApplicationContext(), "ChannelID")
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.fundoo1);

        notificationManager.notify(1, notification.build());
    }
}
