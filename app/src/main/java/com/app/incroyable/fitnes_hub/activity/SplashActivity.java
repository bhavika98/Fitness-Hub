package com.app.incroyable.fitnes_hub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.incroyable.fitnes_hub.R;

public class SplashActivity extends AppCompatActivity {

    private TextView text1, text2, text3, text4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeViews();
        startAnimationSequence();
    }

    private void initializeViews() {
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
    }

    private void startAnimationSequence() {
        animateView(text1, () -> animateView(text2, () -> animateView(text3, () -> animateView(text4, this::navigateToMain))));
    }

    private void animateView(View view, Runnable onAnimationEnd) {
        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        animation.setAnimationListener(new SimpleAnimationListener(onAnimationEnd));
        view.startAnimation(animation);
    }

    private void navigateToMain() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }, 1000);
    }

    private static class SimpleAnimationListener implements Animation.AnimationListener {

        private final Runnable onAnimationEnd;

        SimpleAnimationListener(Runnable onAnimationEnd) {
            this.onAnimationEnd = onAnimationEnd;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onAnimationEnd.run();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}

