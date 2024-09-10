package com.app.incroyable.fitnes_hub.model;

public class WeightDay {
    String Day_no;
    String day_value;
    int w_day_id;

    public WeightDay(){

    }

    public WeightDay(String day_no, String day_value) {
        this.Day_no = day_no;
        this.day_value = day_value;
    }

    public WeightDay(int w_day_id, String day_no, String day_value) {
        this.w_day_id = w_day_id;
        this.Day_no = day_no;
        this.day_value = day_value;
    }

    public int getW_day_id() {
        return this.w_day_id;
    }

    public void setW_day_id(int w_day_id) {
        this.w_day_id = w_day_id;
    }

    public void setDay_no(String day_no) {
        this.Day_no = day_no;
    }

    public String getDay_no() {
        return this.Day_no;
    }

    public String getDay_value() {
        return this.day_value;
    }

    public void setDay_value(String day_value) {
        this.day_value = day_value;
    }
}
