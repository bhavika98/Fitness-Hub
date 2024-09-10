package com.app.incroyable.fitnes_hub.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.incroyable.fitnes_hub.model.WorkoutData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase sqlite;

    public DatabaseOperations(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public int isDatabaseEmpty() {
        sqlite = dbHelper.getReadableDatabase();
        Cursor query = sqlite.rawQuery("SELECT count(*) FROM " + DatabaseHelper.TABLE_NAME, null);
        query.moveToFirst();
        return query.getInt(0) > 0 ? 1 : 0;
    }

    public void deleteTable() {
        sqlite = dbHelper.getWritableDatabase();
        int rowsDeleted = sqlite.delete(DatabaseHelper.TABLE_NAME, null, null);
        Log.e("Result", " => " + rowsDeleted);
        sqlite.close();
    }

    @SuppressLint("Range")
    public List<WorkoutData> getAllDaysProgress() {
        List<WorkoutData> workoutDataList = new ArrayList<>();
        sqlite = dbHelper.getReadableDatabase();
        Cursor query = sqlite.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null);

        if (query.moveToFirst()) {
            do {
                WorkoutData workoutData = new WorkoutData();
                workoutData.setDay(query.getString(query.getColumnIndex(DatabaseHelper.DAY_COLUMN)));
                workoutData.setProgress(query.getFloat(query.getColumnIndex(DatabaseHelper.PROGRESS_COLUMN)));
                workoutDataList.add(workoutData);
            } while (query.moveToNext());
        }
        sqlite.close();
        return workoutDataList;
    }

    @SuppressLint("Range")
    public float getDayProgress(String day) {
        sqlite = dbHelper.getReadableDatabase();
        float progress = 0.0f;

        Cursor query = sqlite.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.DAY_COLUMN + " = '" + day + "'", null);
        if (query.moveToFirst()) {
            progress = query.getFloat(query.getColumnIndex(DatabaseHelper.PROGRESS_COLUMN));
        }
        sqlite.close();
        return progress;
    }

    public void insertAllDayData() {
        long result = 0;
        sqlite = dbHelper.getWritableDatabase();

        for (int i = 1; i <= 30; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.DAY_COLUMN, "Day " + i);
            contentValues.put(DatabaseHelper.PROGRESS_COLUMN, 0.0);

            try {
                result = sqlite.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                Log.e("Result", " => " + result);
            } catch (Exception e) {
                Log.e("Error", " => " + e.getMessage());
            }
        }
        sqlite.close();
    }

    public void updateDayProgress(String day, float progress) {
        int result = 0;
        sqlite = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PROGRESS_COLUMN, progress);

        try {
            result = sqlite.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.DAY_COLUMN + "=?", new String[]{day});
            Log.e("Result", " => " + result);
        } catch (Exception e) {
            Log.e("Error", " => " + e.getMessage());
        }

        sqlite.close();
    }
}

