package com.app.incroyable.fitnes_hub.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.adapter.ReminderAdapter;
import com.app.incroyable.fitnes_hub.model.Reminder;
import com.app.incroyable.fitnes_hub.utils.AlarmHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReminderFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "ReminderFragment";

    private SimpleDateFormat timeFormat;
    private AlarmHelper alarmHelper;
    private Gson gson;
    private ReminderAdapter mAdapter;
    private List<Reminder> reminderList;
    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPreferences;
    private TextView noRemindersText;
    private SharedPreferences.Editor prefsEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        view.setTag(TAG);

        alarmHelper = new AlarmHelper(getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mRecyclerView = view.findViewById(R.id.reminderlist);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noRemindersText = view.findViewById(R.id.noreminder);
        gson = new Gson();

        reminderList = gson.fromJson(sharedPreferences.getString("Reminder_customObjectList", null), new ReminderListTypeToken().getType());

        if (reminderList == null || reminderList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            noRemindersText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new ReminderAdapter(getActivity(), reminderList, gson, sharedPreferences, prefsEditor, alarmHelper);
            mRecyclerView.setAdapter(mAdapter);
            noRemindersText.setVisibility(View.GONE);
        }

        FloatingActionButton addReminderButton = view.findViewById(R.id.addreminder);
        addReminderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        return view;
    }

    private void showReminderDialog(final Calendar calendar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Days");

        final List<Integer> selectedDays = new ArrayList<>();
        builder.setMultiChoiceItems(getResources().getStringArray(R.array.day_list), null, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedDays.add(which);
            } else {
                selectedDays.remove((Integer) which);
            }
        });

        builder.setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
            if (selectedDays.isEmpty()) {
                Toast.makeText(getActivity(), "Please select at least one day", Toast.LENGTH_SHORT).show();
            } else {
                createReminder(calendar, selectedDays);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void createReminder(Calendar calendar, List<Integer> selectedDays) {
        Reminder newReminder = new Reminder();
        newReminder.setTime(startTimeFormat().format(calendar.getTime()));

        for (Integer day : selectedDays) {
            switch (day) {
                case 0:
                    newReminder.setMon(true);
                    break;
                case 1:
                    newReminder.setTue(true);
                    break;
                case 2:
                    newReminder.setWen(true);
                    break;
                case 3:
                    newReminder.setThr(true);
                    break;
                case 4:
                    newReminder.setFri(true);
                    break;
                case 5:
                    newReminder.setSat(true);
                    break;
                case 6:
                    newReminder.setSun(true);
                    break;
            }
        }

        scheduleReminder(calendar);
        newReminder.setOnOff(true);

        if (reminderList == null) {
            reminderList = new ArrayList<>();
        }
        reminderList.add(newReminder);

        saveReminderList();
        refreshReminderList();
    }

    private void scheduleReminder(Calendar calendar) {
        AlarmHelper alarmHelper = this.alarmHelper;
        String amPm = startTimeFormat().format(calendar.getTime());
        int hour = Integer.parseInt(getHourFormat().format(calendar.getTime()));
        int minute = Integer.parseInt(getMinuteFormat().format(calendar.getTime()));
        int amPmFlag = amPm.endsWith("AM") ? 0 : 1;

        alarmHelper.schedulePendingIntent(hour, minute, amPmFlag);
    }

    private void saveReminderList() {
        String reminderJson = gson.toJson(reminderList);
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("Reminder_customObjectList", reminderJson);
        prefsEditor.apply();
    }

    private void refreshReminderList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        noRemindersText.setVisibility(View.GONE);
        mAdapter = new ReminderAdapter(getActivity(), reminderList, gson, sharedPreferences, prefsEditor, alarmHelper);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (view.isShown()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            showReminderDialog(calendar);
        }
    }

    private static class ReminderListTypeToken extends TypeToken<List<Reminder>> {
    }

    private void showTimePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        TimePickerDialog timePicker = TimePickerDialog.newInstance(
                this,
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                false
        );
        timePicker.show(getActivity().getFragmentManager(), "timePicker");
    }

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat getHourFormat() {
        timeFormat = new SimpleDateFormat("hh");
        return timeFormat;
    }

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat getMinuteFormat() {
        timeFormat = new SimpleDateFormat("mm");
        return timeFormat;
    }

    public SimpleDateFormat startTimeFormat() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }
}

