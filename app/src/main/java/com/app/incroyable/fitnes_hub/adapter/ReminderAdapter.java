package com.app.incroyable.fitnes_hub.adapter;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.model.Reminder;
import com.app.incroyable.fitnes_hub.utils.AlarmHelper;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ProductViewHolder> {

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private final AlarmHelper alarmHelper;
    private final Gson gson;
    private final Context context;
    private final SharedPreferences.Editor prefsEditor;
    private final List<Reminder> reminderList;
    private long lastClickTime = 0;

    public ReminderAdapter(Context context, List<Reminder> reminders, Gson gson, SharedPreferences sharedPreferences, SharedPreferences.Editor editor, AlarmHelper alarmHelper) {
        this.context = context;
        this.reminderList = reminders;
        this.gson = gson;
        this.prefsEditor = editor;
        this.alarmHelper = alarmHelper;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView timeView, day1, day2, day3, day4, day5, day6, day7;
        ImageView deleteIcon;
        SwitchCompat reminderSwitch;

        public ProductViewHolder(View view) {
            super(view);
            timeView = view.findViewById(R.id.time);
            day1 = view.findViewById(R.id.day1);
            day2 = view.findViewById(R.id.day2);
            day3 = view.findViewById(R.id.day3);
            day4 = view.findViewById(R.id.day4);
            day5 = view.findViewById(R.id.day5);
            day6 = view.findViewById(R.id.day6);
            day7 = view.findViewById(R.id.day7);
            deleteIcon = view.findViewById(R.id.timedelete);
            reminderSwitch = view.findViewById(R.id.timeswitch);
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_reminder, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);

        holder.timeView.setText(reminder.getTime());
        holder.timeView.setOnClickListener(v -> showTimePickerDialog(reminder, position));
        updateDaysVisibility(holder, reminder);

        holder.reminderSwitch.setChecked(reminder.isOnOff());
        holder.reminderSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> toggleReminder(reminder, isChecked));

        holder.deleteIcon.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - lastClickTime >= 1000) {
                lastClickTime = SystemClock.elapsedRealtime();
                removeReminderAt(position);
            }
        });
    }

    private void updateDaysVisibility(ProductViewHolder holder, Reminder reminder) {
        holder.day1.setVisibility(reminder.isMon() ? View.VISIBLE : View.GONE);
        holder.day2.setVisibility(reminder.isTue() ? View.VISIBLE : View.GONE);
        holder.day3.setVisibility(reminder.isWen() ? View.VISIBLE : View.GONE);
        holder.day4.setVisibility(reminder.isThr() ? View.VISIBLE : View.GONE);
        holder.day5.setVisibility(reminder.isFri() ? View.VISIBLE : View.GONE);
        holder.day6.setVisibility(reminder.isSat() ? View.VISIBLE : View.GONE);
        holder.day7.setVisibility(reminder.isSun() ? View.VISIBLE : View.GONE);
    }

    private void toggleReminder(Reminder reminder, boolean isOn) {
        reminder.setOnOff(isOn);
        updateReminderList();
    }

    private void removeReminderAt(int position) {
        reminderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reminderList.size());
        updateReminderList();
    }

    private void updateReminderList() {
        String reminderListJson = gson.toJson(reminderList);
        prefsEditor.putString("Reminder_customObjectList", reminderListJson);
        prefsEditor.apply();
    }

    private void showTimePickerDialog(Reminder reminder, int position) {
        Calendar currentTime = Calendar.getInstance();
        new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            currentTime.set(Calendar.MINUTE, minute);
            showDaysSelectionDialog(currentTime, reminder, position);
        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show();
    }

    private void showDaysSelectionDialog(Calendar calendar, Reminder reminder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Days");
        final ArrayList<Integer> selectedDays = new ArrayList<>();
        String[] dayList = context.getResources().getStringArray(R.array.day_list);

        builder.setMultiChoiceItems(dayList, null, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedDays.add(which);
            } else {
                selectedDays.remove((Integer) which);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (!selectedDays.isEmpty()) {
                setReminderDays(calendar, reminder, selectedDays);
                scheduleReminder(calendar, reminder);
                updateReminderList();
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Please select at least one day", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void setReminderDays(Calendar calendar, Reminder reminder, List<Integer> selectedDays) {
        reminder.setTime(timeFormat.format(calendar.getTime()));
        reminder.setMon(selectedDays.contains(0));
        reminder.setTue(selectedDays.contains(1));
        reminder.setWen(selectedDays.contains(2));
        reminder.setThr(selectedDays.contains(3));
        reminder.setFri(selectedDays.contains(4));
        reminder.setSat(selectedDays.contains(5));
        reminder.setSun(selectedDays.contains(6));
        reminder.setOnOff(true);
    }

    private void scheduleReminder(Calendar calendar, Reminder reminder) {
        int hour = Integer.parseInt(new SimpleDateFormat("hh", Locale.getDefault()).format(calendar.getTime()));
        int minute = Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(calendar.getTime()));
        boolean isAM = timeFormat.format(calendar.getTime()).endsWith("AM");

        alarmHelper.schedulePendingIntent(hour, minute, isAM ? 0 : 1);
    }
}

