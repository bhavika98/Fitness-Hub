package com.app.incroyable.fitnes_hub.receiver;

import static com.app.incroyable.fitnes_hub.utils.AlarmHelper.ACTION_NOTIFY;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.app.incroyable.fitnes_hub.model.Reminder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BootReceiver extends BroadcastReceiver {

    private static final String PREFERENCE_LAST_REQUEST_CODE = "PREFERENCE_LAST_REQUEST_CODE";
    private static final String TAG = "BootReceiver";
    private static final String REMINDER_CUSTOM_LIST_KEY = "Reminder_customObjectList";

    private Context context;
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, "onReceive - Intent Action: " + intent.getAction());

        try {
            initializeSharedPreferences();
            setAlarms();
        } catch (Exception e) {
            Log.e(TAG, "Error in onReceive: " + e.getMessage(), e);
        }
    }

    private void initializeSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void setAlarms() {
        List<Reminder> reminders = getReminders();
        if (reminders != null && !reminders.isEmpty()) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

            for (Reminder reminder : reminders) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(timeFormat.parse(reminder.getTime().substring(0, 5)));

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int amPm = isAmTime(calendar) ? 0 : 1;

                    schedulePendingIntent(hour, minute, amPm);
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing time: " + e.getMessage(), e);
                }
            }
        }
    }

    private List<Reminder> getReminders() {
        String json = sharedPreferences.getString(REMINDER_CUSTOM_LIST_KEY, null);
        return new Gson().fromJson(json, new TypeToken<List<Reminder>>() {
        }.getType());
    }

    @SuppressLint("ScheduleExactAlarm")
    private void schedulePendingIntent(int hour, int minute, int amPm) {
        Calendar calendar = getCalendarWithTime(hour, minute);

        long triggerTime = calendar.getTimeInMillis();
        PendingIntent pendingIntent = getPendingIntent();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            setBootReceiverEnabled();
        }
    }

    private Calendar getCalendarWithTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(ACTION_NOTIFY);
        intent.setClass(context, NotificationPublisher.class);
        return PendingIntent.getBroadcast(context, getNextRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private int getNextRequestCode() {
        int requestCode = sharedPreferences.getInt(PREFERENCE_LAST_REQUEST_CODE, 0) + 1;
        if (requestCode == Integer.MAX_VALUE) {
            requestCode = 0;
        }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_REQUEST_CODE, requestCode).apply();
        return requestCode;
    }

    private void setBootReceiverEnabled() {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    private boolean isAmTime(Calendar calendar) {
        return new SimpleDateFormat("a", Locale.ENGLISH).format(calendar.getTime()).equalsIgnoreCase("AM");
    }
}

