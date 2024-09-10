package com.app.incroyable.fitnes_hub.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.activity.MainActivity;
import com.app.incroyable.fitnes_hub.model.Reminder;
import com.app.incroyable.fitnes_hub.utils.AlarmHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationPublisher extends BroadcastReceiver {

    private static final String PREFERENCE_LAST_NOTIFY_ID = "PREFERENCE_LAST_NOTIFY_ID";
    private static final boolean DEBUG = true;
    private static final String TAG = "NotificationPublisher";
    private AlarmHelper alarmHelper;
    private SharedPreferences sharedPreferences;

    private int getNextNotificationId() {
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIFY_ID, 0) + 1;
        if (id == Integer.MAX_VALUE) {
            id = 0;
        }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_NOTIFY_ID, id).apply();
        return id;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNotification(Context context) {
        PendingIntent existingAlarm = alarmHelper.existAlarm(sharedPreferences.getInt(PREFERENCE_LAST_NOTIFY_ID, 0));
        if (existingAlarm != null) {
            existingAlarm.cancel();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showLegacyNotification(context);
        } else {
            showOreoNotification(context);
        }
    }

    private void showLegacyNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(PendingIntent.getActivity(context, getNextNotificationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_fcm_notification)
                .setContentTitle("Hey! It's Workout time")
                .setContentText("Let's reduce some weight today.")
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        if (DEBUG || notificationManager != null) {
            notificationManager.notify(getNextNotificationId(), notification);
        } else {
            throw new AssertionError();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showOreoNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_id_01";
        NotificationChannel channel = new NotificationChannel(channelId, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        channel.enableLights(true);
        channel.setLightColor(Color.MAGENTA);
        channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        channel.enableVibration(true);

        if (DEBUG || notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Intent intent = new Intent(context, MainActivity.class)
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(context, getNextNotificationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_fcm_notification)
                    .setContentTitle("Hey! It's Workout time")
                    .setContentText("Let's reduce some weight today.");
            notificationManager.notify(getNextNotificationId(), builder.build());
        } else {
            throw new AssertionError();
        }
    }

    public SimpleDateFormat getHourFormat() {
        return new SimpleDateFormat("hh", Locale.ENGLISH);
    }

    public SimpleDateFormat getMinuteFormat() {
        return new SimpleDateFormat("mm", Locale.ENGLISH);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<Reminder> reminders = new Gson().fromJson(sharedPreferences.getString("Reminder_customObjectList", null), new TypeToken<List<Reminder>>() {}.getType());
        Calendar now = Calendar.getInstance();

        if (reminders != null && !reminders.isEmpty()) {
            alarmHelper = new AlarmHelper(context);

            for (Reminder reminder : reminders) {
                if (reminder.getTime().equals(startTimeFormat().format(now.getTime())) && reminder.isOnOff()) {
                    if (shouldScheduleReminder(reminder, now.get(Calendar.DAY_OF_WEEK))) {
                        scheduleReminder(reminder);
                    }
                }
            }

            if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
                for (Reminder reminder : reminders) {
                    scheduleReminder(reminder);
                }
            }
        }
    }

    private boolean shouldScheduleReminder(Reminder reminder, int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return reminder.isSun();
            case Calendar.MONDAY: return reminder.isMon();
            case Calendar.TUESDAY: return reminder.isTue();
            case Calendar.WEDNESDAY: return reminder.isWen();
            case Calendar.THURSDAY: return reminder.isThr();
            case Calendar.FRIDAY: return reminder.isFri();
            case Calendar.SATURDAY: return reminder.isSat();
            default: return false;
        }
    }

    private void scheduleReminder(Reminder reminder) {
        String time = reminder.getTime();
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        int amPm = time.toLowerCase().contains("pm") ? 1 : 0;

        alarmHelper.schedulePendingIntent(hour, minute, amPm, 86400000);
    }

    public SimpleDateFormat startTimeFormat() {
        return new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    }
}

