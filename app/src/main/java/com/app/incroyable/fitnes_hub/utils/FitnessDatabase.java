package com.app.incroyable.fitnes_hub.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.incroyable.fitnes_hub.model.RepeatData;
import com.app.incroyable.fitnes_hub.model.WeightDay;

import java.util.ArrayList;
import java.util.List;

public class FitnessDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Daily_fitness_DB";
    private static final int DATABASE_VERSION = 1;

    private static final String KEY_DIET_PLAN_DAY_ID = "diet_plan_day_id";
    private static final String KEY_DIET_PLAN_VALUE = "diet_plan_day_value";
    private static final String KEY_MORNING_MEAL_0 = "key_morning_meal_0";
    private static final String KEY_MORNING_MEAL_1 = "key_morning_meal_1";
    private static final String KEY_LIGHT_MEAL_0 = "key_light_meal_0";
    private static final String KEY_LIGHT_MEAL_1 = "key_light_meal_1";
    private static final String KEY_LUNCH_0 = "key_lunch_0";
    private static final String KEY_LUNCH_1 = "key_lunch_1";
    private static final String KEY_DINNER_0 = "key_dinner_0";
    private static final String KEY_DINNER_1 = "key_dinner_1";
    private static final String KEY_R_DAY_NO = "id_rep_day_no";

    public static final String TABLE_DIET_PLAN_DAY = "table_diet_plan_day";
    public static final String TABLE_REP = "table_rep";

    public FitnessDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS table_setting");
        db.execSQL("DROP TABLE IF EXISTS table_day_ex");
        db.execSQL("DROP TABLE IF EXISTS table_ex");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIET_PLAN_DAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REP);
        db.execSQL("DROP TABLE IF EXISTS table_reminder");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE table_setting(id INTEGER PRIMARY KEY, key_notification TEXT, key_language TEXT, key_voice TEXT, key_height TEXT, key_f_height TEXT, key_m_height TEXT, key_weight TEXT, key_gender TEXT, key_bod TEXT, key_progress_value TEXT, key_weight_unit TEXT, key_height_unit TEXT, key_p_value TEXT, key_reset_time TEXT)");
        db.execSQL("CREATE TABLE table_day_ex(workout_id INTEGER PRIMARY KEY, workout_day_id TEXT, workout_day_value TEXT)");
        db.execSQL("CREATE TABLE table_ex(id_ex INTEGER PRIMARY KEY, id_day_no TEXT, steps_header TEXT, r_detail TEXT, ex_img TEXT, steps_detail TEXT, one_day_value TEXT, key_video TEXT, key_repeat_de TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_DIET_PLAN_DAY + "(diet_plan_id INTEGER PRIMARY KEY, " + KEY_DIET_PLAN_DAY_ID + " TEXT, " + KEY_DIET_PLAN_VALUE + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_REP + "(id_rep_no INTEGER PRIMARY KEY, " + KEY_R_DAY_NO + " TEXT, " + KEY_MORNING_MEAL_0 + " TEXT, " + KEY_MORNING_MEAL_1 + " TEXT, " + KEY_LIGHT_MEAL_0 + " TEXT, " + KEY_LIGHT_MEAL_1 + " TEXT, " + KEY_LUNCH_0 + " TEXT, " + KEY_LUNCH_1 + " TEXT, " + KEY_DINNER_0 + " TEXT, " + KEY_DINNER_1 + " TEXT)");
        db.execSQL("CREATE TABLE table_reminder(key_reminder_id INTEGER PRIMARY KEY, key_reminder_time TEXT, key_reminder_on_off TEXT, key_reminder_sun_value TEXT, key_reminder_mon_value TEXT, key_reminder_tue_value TEXT, key_reminder_wed_value TEXT, key_reminder_thu_value TEXT, key_reminder_fri_value TEXT, key_reminder_sat_value TEXT)");
    }

    public void addDietPlanDay(WeightDay weightDayNo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DIET_PLAN_DAY_ID, weightDayNo.getDay_no());
        values.put(KEY_DIET_PLAN_VALUE, weightDayNo.getDay_value());
        db.insert(TABLE_DIET_PLAN_DAY, null, values);
        db.close();
    }

    public List<WeightDay> getDietPlanDays() {
        List<WeightDay> dietPlanDays = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_DIET_PLAN_DAY, null);
        if (cursor.moveToFirst()) {
            do {
                WeightDay dayNo = new WeightDay();
                dayNo.setW_day_id(cursor.getInt(0));
                dayNo.setDay_no(cursor.getString(1));
                dayNo.setDay_value(cursor.getString(2));
                dietPlanDays.add(dayNo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dietPlanDays;
    }

    public boolean updateDietPlanDayValue(String dayId, String dayValue) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DIET_PLAN_VALUE, dayValue);
        int rowsUpdated = db.update(TABLE_DIET_PLAN_DAY, contentValues, KEY_DIET_PLAN_DAY_ID + " = ?", new String[]{dayId});
        return rowsUpdated > 0;
    }

    public void addRepData(RepeatData repDataModel) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_R_DAY_NO, repDataModel.getRep_day_no());
        values.put(KEY_MORNING_MEAL_0, repDataModel.getMorning_meal_0());
        values.put(KEY_MORNING_MEAL_1, repDataModel.getMorning_meal_1());
        values.put(KEY_LIGHT_MEAL_0, repDataModel.getLight_meal_0());
        values.put(KEY_LIGHT_MEAL_1, repDataModel.getLight_meal_1());
        values.put(KEY_LUNCH_0, repDataModel.getLunch_0());
        values.put(KEY_LUNCH_1, repDataModel.getLunch_1());
        values.put(KEY_DINNER_0, repDataModel.getDinner_0());
        values.put(KEY_DINNER_1, repDataModel.getDinner_1());
        db.insert(TABLE_REP, null, values);
        db.close();
    }

    public List<RepeatData> getAllDiet() {
        List<RepeatData> allDiet = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_REP, null);
        if (cursor.moveToFirst()) {
            do {
                RepeatData repData = new RepeatData();
                repData.setRep_day_no(cursor.getString(1));
                repData.setMorning_meal_0(cursor.getString(2));
                repData.setMorning_meal_1(cursor.getString(3));
                repData.setLight_meal_0(cursor.getString(4));
                repData.setLight_meal_1(cursor.getString(5));
                repData.setLunch_0(cursor.getString(6));
                repData.setLunch_1(cursor.getString(7));
                repData.setDinner_0(cursor.getString(8));
                repData.setDinner_1(cursor.getString(9));
                allDiet.add(repData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allDiet;
    }
}

