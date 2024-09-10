package com.app.incroyable.fitnes_hub.utils;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.app.incroyable.fitnes_hub.R;

import java.util.Locale;

public class AbsWomenApplication extends Application {

    private static AbsWomenApplication instance;
    private TextToSpeech textToSpeech;

    public static AbsWomenApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        new Thread(() -> {
            if (textToSpeech == null) {
                textToSpeech = new TextToSpeech(AbsWomenApplication.getInstance(), status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeech.setLanguage(Locale.US);
                    }
                });
            }
        }).start();
    }

    public void playEarCorn(String str) {
        if (textToSpeech != null) {
            try {
                textToSpeech.playEarcon("tick", TextToSpeech.QUEUE_FLUSH, null, str);
            } catch (Throwable e) {
                Log.e("Error", " => " + e.getMessage());
            }
        }
    }

    public void addEarCorn(String str) {
        if (textToSpeech != null) {
            try {
                textToSpeech.addEarcon("tick", str, R.raw.clocktick_trim);
            } catch (Throwable e) {
                Log.e("Error", " => " + e.getMessage());
            }
        }
    }

    public boolean isSpeaking() {
        return textToSpeech != null && textToSpeech.isSpeaking();
    }

    public void speak(String str) {
        if (textToSpeech != null) {
            try {
                textToSpeech.setSpeechRate(1.0f);
                textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
            } catch (Throwable e) {
                Log.e("Error", " => " + e.getMessage());
            }
        }
    }

    public void stop() {
        if (textToSpeech != null) {
            try {
                textToSpeech.stop();
            } catch (Throwable e) {
                Log.e("Error", " => " + e.getMessage());
            }
        }
    }
}

