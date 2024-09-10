package com.app.incroyable.fitnes_hub.model;

public class RepeatData {
    String dinner_0;
    String dinner_1;
    String light_meal_0;
    String light_meal_1;
    String lunch_0;
    String lunch_1;
    String morning_meal_0;
    String morning_meal_1;
    String rep_day_no;

    public RepeatData() {

    }

    public RepeatData(String rep_day_no, String morning_meal_0, String morning_meal_1, String light_meal_0, String light_meal_1, String lunch_0, String lunch_1, String dinner_0, String dinner_1) {
        this.rep_day_no = rep_day_no;
        this.morning_meal_0 = morning_meal_0;
        this.morning_meal_1 = morning_meal_1;
        this.light_meal_0 = light_meal_0;
        this.light_meal_1 = light_meal_1;
        this.lunch_0 = lunch_0;
        this.lunch_1 = lunch_1;
        this.dinner_0 = dinner_0;
        this.dinner_1 = dinner_1;
    }

    public String getRep_day_no() {
        return this.rep_day_no;
    }

    public void setRep_day_no(String rep_day_no) {
        this.rep_day_no = rep_day_no;
    }

    public String getMorning_meal_0() {
        return this.morning_meal_0;
    }

    public void setMorning_meal_0(String morning_meal_0) {
        this.morning_meal_0 = morning_meal_0;
    }

    public String getMorning_meal_1() {
        return this.morning_meal_1;
    }

    public void setMorning_meal_1(String morning_meal_1) {
        this.morning_meal_1 = morning_meal_1;
    }

    public String getLight_meal_0() {
        return this.light_meal_0;
    }

    public void setLight_meal_0(String light_meal_0) {
        this.light_meal_0 = light_meal_0;
    }

    public String getLight_meal_1() {
        return this.light_meal_1;
    }

    public void setLight_meal_1(String light_meal_1) {
        this.light_meal_1 = light_meal_1;
    }

    public String getLunch_0() {
        return this.lunch_0;
    }

    public void setLunch_0(String lunch_0) {
        this.lunch_0 = lunch_0;
    }

    public String getLunch_1() {
        return this.lunch_1;
    }

    public void setLunch_1(String lunch_1) {
        this.lunch_1 = lunch_1;
    }

    public String getDinner_0() {
        return this.dinner_0;
    }

    public void setDinner_0(String dinner_0) {
        this.dinner_0 = dinner_0;
    }

    public String getDinner_1() {
        return this.dinner_1;
    }

    public void setDinner_1(String dinner_1) {
        this.dinner_1 = dinner_1;
    }
}
