package com.app.incroyable.fitnes_hub.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class DailyBroadcast extends BroadcastReceiver {

    private static final int ALARM_REQUEST_CODE = 100;
    private static final long REPEAT_INTERVAL_MS = 86400000; // 24 hours
    private static final int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isBootOrPowerOnAction(intent.getAction())) {
            setAlarm(context);
        }
    }

    private boolean isBootOrPowerOnAction(String action) {
        return Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                "android.intent.action.QUICKBOOT_POWERON".equals(action);
    }

    private void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                new Intent(context, NotificationReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    ALARM_TYPE,
                    calendar.getTimeInMillis(),
                    REPEAT_INTERVAL_MS,
                    pendingIntent
            );
        }
    }
}
