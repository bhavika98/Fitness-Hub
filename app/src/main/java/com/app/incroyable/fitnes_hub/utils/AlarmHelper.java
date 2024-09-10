package com.app.incroyable.fitnes_hub.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.app.incroyable.fitnes_hub.receiver.BootReceiver;
import com.app.incroyable.fitnes_hub.receiver.NotificationPublisher;

import java.util.Calendar;

public class AlarmHelper {

    private static final String PREFERENCE_LAST_REQUEST_CODE = "PREFERENCE_LAST_REQUEST_CODE";
    public static final String ACTION_NOTIFY = "com.app.incroyable.fitnes_hub.alarmmanagerdemo.NOTIFY_ACTION";
    private final AlarmManager alarmManager;
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public AlarmHelper(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private int getNextRequestCode() {
        int requestCode = sharedPreferences.getInt(PREFERENCE_LAST_REQUEST_CODE, 0) + 1;
        if (requestCode == Integer.MAX_VALUE) {
            requestCode = 0;
        }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_REQUEST_CODE, requestCode).apply();
        return requestCode;
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(ACTION_NOTIFY);
        intent.setClass(context, NotificationPublisher.class);
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        return PendingIntent.getBroadcast(context, getNextRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void setBootReceiverEnabled() {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootReceiver.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public PendingIntent existAlarm(int requestCode) {
        Intent intent = new Intent(ACTION_NOTIFY);
        intent.setClass(context, NotificationPublisher.class);
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
    }

    public void schedulePendingIntent(int hour, int minute, int amPm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, amPm);
        schedulePendingIntent(calendar.getTimeInMillis(), getPendingIntent());
    }

    public void schedulePendingIntent(int hour, int minute, int amPm, long delay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, amPm);
        schedulePendingIntent(calendar.getTimeInMillis(), getPendingIntent(), delay);
    }

    @SuppressLint("ScheduleExactAlarm")
    public void schedulePendingIntent(long triggerAtMillis, PendingIntent pendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        setBootReceiverEnabled();
    }

    @SuppressLint("ScheduleExactAlarm")
    public void schedulePendingIntent(long triggerAtMillis, PendingIntent pendingIntent, long delay) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis + delay, pendingIntent);
        setBootReceiverEnabled();
    }
}

