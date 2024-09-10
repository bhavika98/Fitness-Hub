package com.app.incroyable.fitnes_hub.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.activity.MainActivity;

import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    private Context context;
    private final String[] notificationMessages = {
            "Aerobics helps you lose weight",
            "Aerobics increases mobility as you age",
            "Regular Aerobics helps you live healthier & longer",
            "Aerobics strengthens your heart muscles",
            "Aerobics reduces tension & promotes relaxation"
    };

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_notification",
                    "Reminder Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Include all the notifications");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void setLocalNotification() {
        String notificationMessage = notificationMessages[new Random().nextInt(notificationMessages.length)];
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_banner);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_notification")
                .setContentIntent(PendingIntent.getActivity(context, 100,
                        new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.mipmap.ic_fcm_notification)
                .setContentTitle("Aerobics")
                .setContentText(notificationMessage)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(largeIcon)
                        .setBigContentTitle("Aerobics")
                        .setSummaryText(notificationMessage))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(100, builder.build());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        createNotificationChannel(context);
        setLocalNotification();
    }
}

