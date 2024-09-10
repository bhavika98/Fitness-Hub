package com.app.incroyable.fitnes_hub.model;

import java.io.Serializable;

public class WorkoutData implements Serializable {

    private String day;
    private int excCycles;
    private int excDescResId;
    private String excName;
    private String[] excNameList;
    private int position;
    private float progress;
    private int[] workoutList;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getExcCycles() {
        return excCycles;
    }

    public void setExcCycles(int excCycles) {
        this.excCycles = excCycles;
    }

    public int getExcDescResId() {
        return excDescResId;
    }

    public void setExcDescResId(int excDescResId) {
        this.excDescResId = excDescResId;
    }

    public String getExcName() {
        return excName;
    }

    public void setExcName(String excName) {
        this.excName = excName;
    }

    public String[] getExcNameList() {
        return excNameList;
    }

    public void setExcNameList(String[] excNameList) {
        this.excNameList = excNameList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int[] getWorkoutList() {
        return workoutList;
    }

    public void setWorkoutList(int[] workoutList) {
        this.workoutList = workoutList;
    }
}
