package com.app.incroyable.fitnes_hub.model;

public class Reminder {

    private boolean sun;
    private boolean mon;
    private boolean tue;
    private boolean wen;
    private boolean thr;
    private boolean fri;
    private boolean sat;

    private String time;
    private boolean onOff;

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean isWen() {
        return wen;
    }

    public void setWen(boolean wen) {
        this.wen = wen;
    }

    public boolean isThr() {
        return thr;
    }

    public void setThr(boolean thr) {
        this.thr = thr;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isOnOff() {
        return onOff;
    }

    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
    }
}
