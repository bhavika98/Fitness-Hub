package com.app.incroyable.fitnes_hub.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.fragment.ReminderFragment;

public class AlarmMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_main);

        setupToolbar();
        if (savedInstanceState == null) {
            loadReminderFragment();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.exc_details_layout_mtoolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadReminderFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, new ReminderFragment());
        transaction.commit();
    }
}

